package com.example.myapplication.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemNoteBinding;
import com.example.myapplication.models.CheckListModel;
import com.example.myapplication.models.ImageModel;
import com.example.myapplication.models.NoteModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends ListAdapter<NoteModel, NotesAdapter.NotesViewHolder> {
    int[] colors = {
            R.color.item_green,
            R.color.item_yellow,
            R.color.item_red,
            R.color.item_purple,
            R.color.item_blue,
            R.color.item_cyan
    };
    Context context;
    AdapterView.OnItemClickListener onItemClickListener;
    AdapterView.OnItemLongClickListener onItemLongClickListener;
    ImageAdapter imageAdapter;
    Gson gson = new Gson();
    MainActivity mainActivity;

    public NotesAdapter(MainActivity mainActivity, Context context, AdapterView.OnItemClickListener onItemClickListener, AdapterView.OnItemLongClickListener onItemLongClickListener) {
        super(diffCallback);
        this.mainActivity = mainActivity;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private static final DiffUtil.ItemCallback<NoteModel> diffCallback = new DiffUtil.ItemCallback<NoteModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull NoteModel oldItem, @NonNull NoteModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NoteModel oldItem, @NonNull NoteModel newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) && oldItem.getDescription().equals(newItem.getDescription());
        }
    };

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        NoteModel noteModel = getItem(position);
        String images = noteModel.getImages();
        setImages(images, holder);
        if (convertCheckList(noteModel).isEmpty()) {
            SpannableString text = new SpannableString(noteModel.getDescription());
            convertBullets(text);
            holder.binding.tvNote.setText(text);
        } else {
            holder.binding.tvNote.setText(convertCheckList(noteModel));
        }
        int startColor = ContextCompat.getColor(context, colors[position % colors.length]);
        int endColor = ContextCompat.getColor(context, colors[(position + 1) % colors.length]);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
        holder.binding.background.setBackground(gradientDrawable);
        holder.binding.tvTitle.setText(noteModel.getTitle());
        if (noteModel.getIs_pinned() == 0)
            holder.binding.imgPin.setVisibility(View.VISIBLE);
        else
            holder.binding.imgPin.setVisibility(View.GONE);
        holder.binding.rvImageList.setLayoutManager(new LinearLayoutManager(context));
        holder.binding.getRoot().setOnClickListener(view -> onItemClickListener.onItemClick(null, holder.binding.getRoot(), holder.getAdapterPosition(), 0));
        holder.binding.getRoot().setOnLongClickListener(view -> onItemLongClickListener.onItemLongClick(null, holder.binding.getRoot(), holder.getAdapterPosition(), 0));
    }

    public NoteModel getNote(int position) {
        return getItem(position);
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        ItemNoteBinding binding;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNoteBinding.bind(itemView);
        }
    }

    private void convertBullets(SpannableString spannable) {
        String text = spannable.toString();
        int start = text.indexOf("●");
        while (start != -1) {
            int startPos, endPos;
            Drawable drawable;
            startPos = start;
            endPos = startPos + 1;
            drawable = ContextCompat.getDrawable(context, R.drawable.baseline_radio_button_unchecked_24);
            assert drawable != null;
            drawable.setBounds(0, 0, 35, 35);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            spannable.setSpan(span, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = text.indexOf("●", endPos);
        }
    }

    private String convertCheckList(NoteModel noteModel) {
        try {
            ArrayList<CheckListModel> arrayList = gson.fromJson(noteModel.getDescription(), new TypeToken<List<CheckListModel>>() {
            }.getType());
            StringBuilder note = new StringBuilder();
            for (CheckListModel check :
                    arrayList) {
                if (check.isChecked()) {
                    note.append("[X] ").append(check.getText()).append("\n");
                } else {
                    note.append("[ ] ").append(check.getText()).append("\n");
                }
            }

            return note.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private void setImages(String images, NotesViewHolder holder) {
        ArrayList<ImageModel> imageList = gson.fromJson(images, new TypeToken<List<ImageModel>>() {
        }.getType());
        if (imageList.size() > 0) {
            ArrayList<ImageModel> temp = new ArrayList<>();
            temp.add(imageList.get(0));
            imageAdapter = new ImageAdapter(context, temp, onItemClickListener, holder.getAdapterPosition());
            holder.binding.rvImageList.setAdapter(imageAdapter);
            imageAdapter.notifyItemInserted(0);
            holder.binding.rvImageList.setVisibility(View.VISIBLE);
        } else {
            holder.binding.rvImageList.setVisibility(View.GONE);
        }
    }
    public ImageAdapter getImageAdapter(){
        return imageAdapter;
    }
}
