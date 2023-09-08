package com.example.myapplication.models;

public class CheckListModel {
    private int id;
    private boolean checked;
    private String text;

    public CheckListModel() {
    }

    public CheckListModel(boolean checked, String text) {
        this.checked = checked;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
