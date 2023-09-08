package com.example.myapplication.repos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.daos.NoteDao;
import com.example.myapplication.database.NotesDatabase;
import com.example.myapplication.models.NoteModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteRepo {
    private NoteDao noteDao;
    private LiveData<List<NoteModel>> noteList;
    private Executor executor;

    public NoteRepo(Application application) {
        NotesDatabase notesDatabase = NotesDatabase.getInstance(application);
        noteDao = notesDatabase.noteDao();
        noteList = noteDao.getAllNotes();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<NoteModel>> getAllNotes() {
        return noteList;
    }

    public LiveData<List<NoteModel>> searchNotes(String title) {
        return noteDao.searchNotes(title);
    }

    public void insertNote(NoteModel note) {
        executor.execute(() -> noteDao.insert(note));
    }

    public void updateNote(NoteModel... note) {
        executor.execute(() -> noteDao.update(note));
    }

    public void deleteNote(NoteModel note) {
        executor.execute(() -> noteDao.delete(note));
    }
}
