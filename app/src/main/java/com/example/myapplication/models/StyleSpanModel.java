package com.example.myapplication.models;

import androidx.annotation.NonNull;

public class StyleSpanModel {
    private int style;
    private int start;
    private int end;

    public StyleSpanModel(int style, int start, int end) {
        this.style = style;
        this.start = start;
        this.end = end;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
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
        return "StyleSpanModel{" +
                "style=" + style +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
