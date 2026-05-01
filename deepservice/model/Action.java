package deepservice.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Action {
    @SerializedName("type")
    private String typeStr;

    private transient ActionType type;
    private List<String> keys;
    private String text;
    private String description;

    public Action() {}

    public Action(ActionType type, String description) {
        this.type = type;
        this.typeStr = type.getValue();
        this.description = description;
    }

    public ActionType getType() {
        if (type == null && typeStr != null) {
            type = ActionType.fromString(typeStr);
        }
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
        this.typeStr = type.getValue();
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
        this.type = ActionType.fromString(typeStr);
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
