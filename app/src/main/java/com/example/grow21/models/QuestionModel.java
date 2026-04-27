package com.example.grow21.models;

import java.util.List;

public class QuestionModel {
    private String id;
    private String type;
    private String category;
    private String question;
    private String image;
    private List<String> options;
    private String answer;

    public QuestionModel() {
    }

    public QuestionModel(String id, String type, String category, String question,
                         String image, List<String> options, String answer) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.question = question;
        this.image = image;
        this.options = options;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
