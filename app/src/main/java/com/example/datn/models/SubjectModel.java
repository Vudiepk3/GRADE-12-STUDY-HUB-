package com.example.datn.models;

public class SubjectModel {
    private int imageResId; // ID của hình ảnh
    private String name;

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SubjectModel(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }
}
