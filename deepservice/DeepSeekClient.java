package deepservice;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import deepservice.exception.ActionParseException;
import deepservice.exception.DeepSeekAPIException;
import deepservice.model.Action;
import deepservice.model.ClipboardContext;
import deepservice.model.Platform;
import deepservice.prompt.SystemPromptGenerator;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeepSeekClient {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL = "deepseek-v4-flash";
    private static final int TIMEOUT_SECONDS = 30;

    private final String apiKey;
    private Platform platform;
    private final OkHttpClient client;
    private final Gson gson;

    public DeepSeekClient(String apiKey, String platform) {
        this.apiKey = apiKey;
        this.platform = Platform.fromString(platform);
        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void setPlatform(String platform) {
        this.platform = Platform.fromString(platform);
    }

    public List<Action> execute(String instruction) throws DeepSeekAPIException, ActionParseException {
        return execute(instruction, (String) null);
    }

    public List<Action> execute(String instruction, ClipboardContext clipboard) throws DeepSeekAPIException, ActionParseException {
        String clipboardContent = clipboard != null ? clipboard.getContent() : null;
        return execute(instruction, clipboardContent);
    }

    public List<Action> execute(String instruction, String clipboardContent) throws DeepSeekAPIException, ActionParseException {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", SystemPromptGenerator.generatePromptWithClipboard(platform, clipboardContent)));
        messages.add(new Message("user", instruction));

        String response = callApi(messages);
        return parseResponse(response);
    }

    private String callApi(List<Message> messages) throws DeepSeekAPIException {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", MODEL);
        requestBody.addProperty("temperature", 0.1);
        requestBody.addProperty("max_tokens", 1000);
        requestBody.addProperty("response_format", "{\"type\":\"json_object\"}");

        JsonArray messagesArray = gson.toJsonTree(messages).getAsJsonArray();
        requestBody.add("messages", messagesArray);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new DeepSeekAPIException(String.valueOf(response.code()),
                        response.body() != null ? response.body().string() : "Unknown error");
            }
            return response.body().string();
        } catch (IOException e) {
            throw new DeepSeekAPIException("IO_ERROR", e.getMessage());
        }
    }

    private List<Action> parseResponse(String responseText) throws ActionParseException {
        try {
            JsonObject responseJson = gson.fromJson(responseText, JsonObject.class);
            JsonArray choices = responseJson.getAsJsonArray("choices");
            if (choices == null || choices.size() == 0) {
                throw new ActionParseException("No choices in response", responseText);
            }

            JsonObject choice = choices.get(0).getAsJsonObject();
            JsonObject message = choice.getAsJsonObject("message");
            String content = message.get("content").getAsString();

            JsonObject contentJson = gson.fromJson(content, JsonObject.class);
            JsonArray actionsArray = contentJson.getAsJsonArray("actions");

            if (actionsArray == null) {
                throw new ActionParseException("No actions in response", responseText);
            }

            Type listType = new TypeToken<List<Action>>() {}.getType();
            return gson.fromJson(actionsArray, listType);

        } catch (Exception e) {
            throw new ActionParseException(e.getMessage(), responseText);
        }
    }

    private static class Message {
        String role;
        String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
