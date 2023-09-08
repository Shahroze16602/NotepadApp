package com.example.myapplication.models;

import androidx.annotation.NonNull;

public class ForegroundColorSpanModel {
    private int color;
    private int start;
    private int end;

    public ForegroundColorSpanModel(int color, int start, int end) {
        this.color = color;
        this.start = start;
        this.end = end;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @NonNull
    @Override
    public String toString() {
        return "ForegroundColorSpanModel{" +
                "color=" + color +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
