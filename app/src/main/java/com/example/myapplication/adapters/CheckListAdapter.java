package com.example.myapplication.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AddNoteActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemChecklistBinding;
import com.example.myapplication.models.CheckListModel;

import java.util.ArrayList;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.CheckListViewHolder> {
    Context context;
    ArrayList<CheckListModel> arrayList;
    AddNoteActivity addNoteActivity;

    public CheckListAdapter(Context context, ArrayList<CheckListModel> arrayList, AddNoteActivity addNoteActivity) {
        this.context = context;
        this.arrayList = arrayList;
        this.addNoteActivity = addNoteActivity;
    }

    @NonNull
    @Override
    public CheckListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist, parent, false);
        return new CheckListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CheckListModel currentItem = arrayList.get(position);
        holder.binding.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            currentItem.setChecked(b);
            if (b) {
                holder.binding.edtText.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            } else {
                holder.binding.edtText.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            }
        });
        holder.binding.edtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!currentItem.getText().equals(charSequence.toString())) {
                    currentItem.setText(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        holder.binding.edtText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (holder.binding.edtText.getText().toString().trim().isEmpty()) {
                    int positionToRemove = holder.getAdapterPosition();
                    if (positionToRemove != RecyclerView.NO_POSITION) {
                        arrayList.remove(positionToRemove);
                        notifyItemRemoved(positionToRemove);
                        for (int i = positionToRemove; i < arrayList.size(); i++) {
                            arrayList.get(i).setId(i);
                        }
                        addNoteActivity.moveFocus(currentItem.getId() - 1);
                        if (arrayList.isEmpty()) {
                            addNoteActivity.hideCheckList();
                        }
                    }
                    return true;
                }
            } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                int positionToAdd = holder.getAdapterPosition() + 1;
                if (positionToAdd >= 0 && positionToAdd <= arrayList.size()) {
                    arrayList.add(positionToAdd, new CheckListModel(false, ""));
                    notifyItemInserted(positionToAdd);
                    for (int i = positionToAdd; i < arrayList.size(); i++) {
                        arrayList.get(i).setId(i);
                    }
                    addNoteActivity.moveFocus(currentItem.getId() + 1);
                }
                return true;
            }
            return false;
        });
        holder.binding.checkbox.setChecked(currentItem.isChecked());
        holder.binding.edtText.setText(currentItem.getText());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class CheckListViewHolder extends RecyclerView.ViewHolder {
        public ItemChecklistBinding binding;

        public CheckListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemChecklistBinding.bind(itemView);
        }
    }
}
