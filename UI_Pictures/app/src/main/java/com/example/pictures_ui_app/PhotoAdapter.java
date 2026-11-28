package com.example.pictures_ui_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

final class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.Holder> {
    interface OnPhotoClick {
        void open(int position);
    }

    private final OnPhotoClick listener;

    PhotoAdapter(OnPhotoClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        Holder holder = new Holder(view);
        holder.thumbnail.setOnClickListener(clicked -> {
            int position = holder.getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.open(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Photo photo = Photo.GALLERY[position];
        String title = holder.itemView.getResources().getString(photo.title);
        holder.thumbnail.setContentDescription(
                holder.itemView.getResources().getString(R.string.open_photo, title));
        Glide.with(holder.thumbnail).load(photo.image).centerCrop().into(holder.thumbnail);
    }

    @Override
    public void onViewRecycled(@NonNull Holder holder) {
        Glide.with(holder.thumbnail).clear(holder.thumbnail);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return Photo.GALLERY.length;
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ImageView thumbnail;

        Holder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            thumbnail.setClipToOutline(true);
        }
    }
}
