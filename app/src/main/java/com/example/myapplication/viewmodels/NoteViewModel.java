package com.example.myapplication.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.repos.NoteRepo;
import com.example.myapplication.models.NoteModel;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    NoteRepo noteRepo;
    public LiveData<List<NoteModel>> noteList;
    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepo = new NoteRepo(application);
        noteList = noteRepo.getAllNotes();
    }
    public void insert(NoteModel note) {
        noteRepo.insertNote(note);
    }
    public void update(NoteModel... note) {
        noteRepo.updateNote(note);
    }
    public void delete(NoteModel note) {
        noteRepo.deleteNote(note);
    }
    public LiveData<List<NoteModel>> getAllNotes() {
        return noteList;
    }
    public LiveData<List<NoteModel>> searchNotes(String title) {
        return noteRepo.searchNotes(title);
    }

}
