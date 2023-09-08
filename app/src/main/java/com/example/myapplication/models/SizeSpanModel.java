package com.example.myapplication.models;

import androidx.annotation.NonNull;

public class SizeSpanModel {
    private float relativeSize;
    private int start;
    private int end;

    public SizeSpanModel(float relativeSize, int start, int end) {
        this.relativeSize = relativeSize;
        this.start = start;
        this.end = end;
    }

    public float getRelativeSize() {
        return relativeSize;
    }

    public void setRelativeSize(float relativeSize) {
        this.relativeSize = relativeSize;
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
                "color=" + relativeSize +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
