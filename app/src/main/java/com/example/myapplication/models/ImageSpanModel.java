package com.example.myapplication.models;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class ImageSpanModel {
    private String imageDrawable;
    private int start;
    private int end;

    public ImageSpanModel(String imageDrawable, int start, int end) {
        this.imageDrawable = imageDrawable;
        this.start = start;
        this.end = end;
    }

    public String getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(String imageDrawable) {
        this.imageDrawable = imageDrawable;
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

    @Override
    public String toString() {
        return "ImageSpanModel{" +
                "imageDrawable='" + imageDrawable + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
