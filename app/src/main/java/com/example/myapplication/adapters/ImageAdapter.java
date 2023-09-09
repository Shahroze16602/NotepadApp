package com.example.myapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemImageBinding;
import com.example.myapplication.models.ImageModel;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    Context context;
    ArrayList<ImageModel> imageList;
    AdapterView.OnItemClickListener onItemClickListener = null;
    AdapterView.OnItemLongClickListener onItemLongClickListener = null;
    int adapterPosition;
    public ImageAdapter(Context context, ArrayList<ImageModel> imageList, AdapterView.OnItemClickListener onItemClickListener, int adapterPosition) {
        this.context = context;
        this.imageList = imageList;
        this.onItemClickListener = onItemClickListener;
        this.adapterPosition = adapterPosition;
    }
    public ImageAdapter(Context context, ArrayList<ImageModel> imageList, AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.context = context;
        this.imageList = imageList;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel currentItem = imageList.get(position);
        holder.binding.imageView.setImageURI(Uri.parse(currentItem.getImageDrawable()));
        if (onItemLongClickListener != null) holder.binding.getRoot().setOnLongClickListener(view -> onItemLongClickListener.onItemLongClick(null, holder.binding.getRoot(), holder.getAdapterPosition(), 0));
        if (onItemClickListener != null) holder.binding.getRoot().setOnClickListener(view -> onItemClickListener.onItemClick(null, holder.binding.getRoot(), adapterPosition, 0));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemImageBinding.bind(itemView);
        }
    }
}
