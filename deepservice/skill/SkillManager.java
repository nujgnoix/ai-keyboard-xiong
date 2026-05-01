package deepservice.skill;

import deepservice.model.Action;

import java.util.*;
import java.util.stream.Collectors;

public class SkillManager {
    private final Map<String, Skill> skills;
    private final int minUsageForLearning;
    private final double similarityThreshold;

    public SkillManager() {
        this.skills = new HashMap<>();
        this.minUsageForLearning = 2;
        this.similarityThreshold = 0.6;
    }

    public void registerSkill(Skill skill) {
        if (skill == null || skill.getId() == null) {
            return;
        }
        skills.put(skill.getId(), skill);
    }

    public void unregisterSkill(String skillId) {
        if (skillId != null) {
            skills.remove(skillId);
        }
    }

    public Skill getSkillById(String skillId) {
        return skillId != null ? skills.get(skillId) : null;
    }

    public Skill findMatchingSkill(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return null;
        }

        List<Skill> matchingSkills = new ArrayList<>();
        for (Skill skill : skills.values()) {
            if (skill.matches(userInput)) {
                matchingSkills.add(skill);
            }
        }

        if (matchingSkills.isEmpty()) {
            return null;
        }

        matchingSkills.sort((s1, s2) -> {
            int score1 = calculateMatchScore(s1, userInput);
            int score2 = calculateMatchScore(s2, userInput);
            return Integer.compare(score2, score1);
        });

        Skill bestMatch = matchingSkills.get(0);
        bestMatch.incrementUsage();
        return bestMatch;
    }

    private int calculateMatchScore(Skill skill, String userInput) {
        String lowerInput = userInput.toLowerCase();
        int score = 0;
        for (String keyword : skill.getTriggerKeywords()) {
            if (lowerInput.contains(keyword.toLowerCase())) {
                score += keyword.length();
            }
        }
        score += skill.getUsageCount() * 10;
        return score;
    }

    public Skill learnFromActions(String userInput, List<Action> actions) {
        if (userInput == null || userInput.trim().isEmpty() || actions == null || actions.isEmpty()) {
            return null;
        }

        String skillName = extractSkillName(userInput);
        List<String> keywords = extractKeywords(userInput);

        Skill existingSkill = findSimilarSkill(keywords);
        if (existingSkill != null) {
            existingSkill.incrementUsage();
            for (String keyword : keywords) {
                existingSkill.addTriggerKeyword(keyword);
            }
            return existingSkill;
        }

        Skill newSkill = new Skill();
        newSkill.setName(skillName);
        newSkill.setDescription("从用户操作学习: " + truncateDescription(userInput));
        newSkill.setTriggerKeywords(keywords);
        newSkill.setActions(actions);
        newSkill.setUsageCount(1);

        registerSkill(newSkill);
        return newSkill;
    }

    private Skill findSimilarSkill(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }

        for (Skill skill : skills.values()) {
            double similarity = calculateKeywordSimilarity(keywords, skill.getTriggerKeywords());
            if (similarity >= similarityThreshold) {
                return skill;
            }
        }
        return null;
    }

    private double calculateKeywordSimilarity(List<String> keywords1, List<String> keywords2) {
        if (keywords1 == null || keywords2 == null || keywords1.isEmpty() || keywords2.isEmpty()) {
            return 0.0;
        }

        Set<String> set1 = new HashSet<>(keywords1);
        Set<String> set2 = new HashSet<>(keywords2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private String extractSkillName(String userInput) {
        String[] words = userInput.toLowerCase().split("\\s+");
        StringBuilder name = new StringBuilder();
        int wordCount = Math.min(3, words.length);
        for (int i = 0; i < wordCount; i++) {
            if (name.length() > 0) {
                name.append(" ");
            }
            name.append(capitalize(words[i]));
        }
        return name.toString();
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    private List<String> extractKeywords(String userInput) {
        List<String> keywords = new ArrayList<>();
        String[] words = userInput.toLowerCase().split("\\s+");

        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
                "have", "has", "had", "do", "does", "did", "will", "would", "could",
                "should", "may", "might", "must", "shall", "can", "need", "dare",
                "to", "of", "in", "for", "on", "with", "at", "by", "from", "up",
                "about", "into", "over", "after", "and", "but", "or", "nor", "so",
                "yet", "both", "either", "neither", "not", "only", "own", "same",
                "than", "too", "very", "just", "please", "help", "me", "i", "my"
        ));

        for (String word : words) {
            String cleaned = word.replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "");
            if (cleaned.length() >= 2 && !stopWords.contains(cleaned.toLowerCase())) {
                keywords.add(cleaned.toLowerCase());
            }
        }

        return keywords.stream().distinct().collect(Collectors.toList());
    }

    private String truncateDescription(String text) {
        if (text == null) {
            return "";
        }
        return text.length() > 50 ? text.substring(0, 50) + "..." : text;
    }

    public List<Skill> getPopularSkills(int limit) {
        return skills.values().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getUsageCount(), s1.getUsageCount()))
                .limit(limit > 0 ? limit : Integer.MAX_VALUE)
                .collect(Collectors.toList());
    }

    public List<Skill> getRecentSkills(int limit) {
        return skills.values().stream()
                .sorted((s1, s2) -> Long.compare(s2.getLastUsedAt(), s1.getLastUsedAt()))
                .limit(limit > 0 ? limit : Integer.MAX_VALUE)
                .collect(Collectors.toList());
    }

    public List<Skill> getAllSkills() {
        return new ArrayList<>(skills.values());
    }

    public int getSkillCount() {
        return skills.size();
    }

    public void clearAllSkills() {
        skills.clear();
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSkills", skills.size());
        stats.put("totalUsage", skills.values().stream().mapToInt(Skill::getUsageCount).sum());
        stats.put("averageUsage", skills.isEmpty() ? 0 :
                skills.values().stream().mapToInt(Skill::getUsageCount).average().orElse(0));
        return stats;
    }
}
