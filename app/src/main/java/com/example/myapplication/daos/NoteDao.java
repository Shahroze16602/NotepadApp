package com.example.myapplication.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.models.NoteModel;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(NoteModel note);
    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(NoteModel... note);
    @Delete
    void delete(NoteModel note);
    @Query("SELECT * FROM notes_tbl ORDER BY is_pinned ASC")
    LiveData<List<NoteModel>> getAllNotes();
    @Query("SELECT * FROM notes_tbl WHERE title LIKE '%' || :title || '%'")
    LiveData<List<NoteModel>> searchNotes(String title);
}
