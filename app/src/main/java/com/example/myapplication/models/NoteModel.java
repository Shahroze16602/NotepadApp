package com.example.myapplication.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "notes_tbl")
public class NoteModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String formatting_style;
    private String formatting_color;
    private String images;
    private String sizes;
    private int is_pinned;

    public NoteModel(String title, String description, String formatting_style, String formatting_color, String images, String sizes, int is_pinned) {
        this.title = title;
        this.description = description;
        this.formatting_style = formatting_style;
        this.formatting_color = formatting_color;
        this.images = images;
        this.sizes = sizes;
        this.is_pinned = is_pinned;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormatting_style() {
        return formatting_style;
    }

    public void setFormatting_style(String formatting_style) {
        this.formatting_style = formatting_style;
    }

    public String getFormatting_color() {
        return formatting_color;
    }

    public void setFormatting_color(String formatting_color) {
        this.formatting_color = formatting_color;
    }

    public int getIs_pinned() {
        return is_pinned;
    }

    public void setIs_pinned(int is_pinned) {
        this.is_pinned = is_pinned;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    @Override
    public String toString() {
        return "NoteModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", formatting_style='" + formatting_style + '\'' +
                ", formatting_color='" + formatting_color + '\'' +
                ", images='" + images + '\'' +
                ", sizes='" + sizes + '\'' +
                '}';
    }
}
