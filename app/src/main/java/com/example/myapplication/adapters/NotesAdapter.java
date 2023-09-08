package com.example.myapplication.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemNoteBinding;
import com.example.myapplication.models.CheckListModel;
import com.example.myapplication.models.ImageSpanModel;
import com.example.myapplication.models.NoteModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
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
    Gson gson = new Gson();
    public NotesAdapter(Context context, AdapterView.OnItemClickListener onItemClickListener, AdapterView.OnItemLongClickListener onItemLongClickListener) {
        super(diffCallback);
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
        if (convertCheckList(noteModel).isEmpty()) {
            List<ImageSpanModel> imageSpans = gson.fromJson(images, new TypeToken<List<ImageSpanModel>>() {
            }.getType());
            SpannableString text = new SpannableString(noteModel.getDescription());
            for (ImageSpanModel span : imageSpans) {
                try {
                    ContentResolver contentResolver = context.getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(Uri.parse(span.getImageDrawable()));
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (bitmap != null) {
                        int originalWidth = bitmap.getWidth();
                        int originalHeight = bitmap.getHeight();
                        int newWidth = 160;
                        int newHeight = Math.round((float) newWidth * originalHeight / originalWidth);
                        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                        drawable.setBounds(0, 0, newWidth, newHeight);
                        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                        text.setSpan(imageSpan, span.getStart(), span.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            drawable.setBounds(0,0,35,35);
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
            for (CheckListModel check:
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
}
