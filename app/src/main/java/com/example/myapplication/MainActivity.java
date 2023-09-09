package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.adapters.NotesAdapter;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.models.NoteModel;
import com.example.myapplication.viewmodels.NoteViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    NoteViewModel noteViewModel;
    NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        noteViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
        binding.rvNotes.requestFocus();
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivityForResult(intent, 101);
        });
        binding.rvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
//        binding.rvNotes.setLayoutManager(new LinearLayoutManager(this));

        binding.rvNotes.setHasFixedSize(true);
        adapter = new NotesAdapter(this, this, (adapterView, view, i, l) -> {
            NoteModel noteModel = Objects.requireNonNull(noteViewModel.noteList.getValue()).get(i);
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("Id", noteModel.getId());
            intent.putExtra("Title", noteModel.getTitle());
            intent.putExtra("Note", noteModel.getDescription());
            intent.putExtra("Formatting_style", noteModel.getFormatting_style());
            intent.putExtra("Formatting_color", noteModel.getFormatting_color());
            intent.putExtra("Images", noteModel.getImages());
            intent.putExtra("Sizes", noteModel.getSizes());
            intent.putExtra("Is_pinned", noteModel.getIs_pinned());
            startActivityForResult(intent, 102);
        }, (adapterView, view, i, l) -> {
            NoteModel noteModel = adapter.getNote(i);
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.drop_down_menu, popup.getMenu());
            if (noteModel.getIs_pinned() == 0) {
                popup.getMenu().getItem(0).setTitle("Unpin note");
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.opt_pin) {
                    if (noteModel.getIs_pinned() == 1) {
                        noteModel.setIs_pinned(0);
                        Toast.makeText(MainActivity.this, "Item pinned", Toast.LENGTH_SHORT).show();
                    } else {
                        noteModel.setIs_pinned(1);
                        Toast.makeText(MainActivity.this, "Item unpinned", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyItemChanged(i);
                    noteViewModel.update(noteModel);
                } else {
                    noteViewModel.delete(noteModel);
                    Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popup.show();
            return true;
        });
        binding.rvNotes.setAdapter(adapter);
        noteViewModel.getAllNotes().observe(this, adapter::submitList);
        ItemTouchHelper.SimpleCallback deleteSwipe = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                try {
                    NoteModel note = Objects.requireNonNull(noteViewModel.noteList.getValue()).get(viewHolder.getAdapterPosition());
                    NoteModel targetNote = noteViewModel.noteList.getValue().get(target.getAdapterPosition());
                    int temp = note.getId();
                    note.setId(targetNote.getId());
                    targetNote.setId(temp);
                    noteViewModel.update(note, targetNote);
                } catch (Exception e) {
                    Log.e("ERROR", "onMove: " + e.getMessage());
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // nothing
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(deleteSwipe);
        itemTouchHelper.attachToRecyclerView(binding.rvNotes);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.trim().isEmpty()) {
                    noteViewModel.searchNotes(newText).observe(MainActivity.this, adapter::submitList);
                } else {
                    noteViewModel.getAllNotes().observe(MainActivity.this, adapter::submitList);
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String title = data.getStringExtra("Title");
            String note = data.getStringExtra("Note");
            String formatting_style = data.getStringExtra("Formatting_style");
            String formatting_color = data.getStringExtra("Formatting_color");
            String images = data.getStringExtra("Images");
            String sizes = data.getStringExtra("Sizes");
            int isPinned = data.getIntExtra("Is_pinned", 1);
            if (requestCode == 101) {
                NoteModel noteModel = new NoteModel(title, note, formatting_style, formatting_color, images, sizes, isPinned);
                noteViewModel.insert(noteModel);
            } else if (requestCode == 102) {
                NoteModel noteModel = new NoteModel(title, note, formatting_style, formatting_color, images, sizes, isPinned);
                noteModel.setId(data.getIntExtra("Id", 0));
                noteViewModel.update(noteModel);
            }
        }
        binding.searchView.setQuery("",false);
        adapter.notifyDataSetChanged();
    }
}