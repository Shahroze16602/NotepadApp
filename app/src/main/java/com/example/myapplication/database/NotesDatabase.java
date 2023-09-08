package com.example.myapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.daos.NoteDao;
import com.example.myapplication.models.NoteModel;

@Database(entities = NoteModel.class, version = 2)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase instance;
    public abstract NoteDao noteDao();
    public static synchronized NotesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),NotesDatabase.class, "notes_db").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
