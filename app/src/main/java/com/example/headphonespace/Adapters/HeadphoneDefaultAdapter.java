package com.example.headphonespace.Adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.headphonespace.Interfaces.HeadphoneCallback;
import com.example.headphonespace.Models.Headphone;
import com.example.headphonespace.Models.HeadphoneList;
import com.example.headphonespace.R;
import com.example.headphonespace.Utilities.ImageLoader;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HeadphoneDefaultAdapter extends RecyclerView.Adapter<HeadphoneDefaultAdapter.HeadphoneViewHolder>{
    private final HeadphoneList headphones;
    private HeadphoneCallback headphoneCallback;

    public HeadphoneDefaultAdapter(HeadphoneList headphones)
    {
        this.headphones = headphones;
    }

    public void setHeadphoneCallback(HeadphoneCallback headphoneCallback)
    {
        this.headphoneCallback = headphoneCallback;
    }

    @NonNull
    @Override
    public HeadphoneDefaultAdapter.HeadphoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_headphone_item, parent, false);
        return new HeadphoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadphoneDefaultAdapter.HeadphoneViewHolder holder, int position) {
        Headphone headphone = headphones.getHeadphones().get(String.valueOf(position));

        assert headphone != null;
        holder.headphone_IMG_poster.setImageResource(headphone.getHeadphone_IMG());

        holder.headphone_LBL_name.setText(headphone.getName());
        holder.headphone_LBL_overview.setText(String.join(", ", headphone.getAttributes()));
        holder.headphone_LBL_info.setText(headphone.getHeadphone_info());
        holder.headphone_RTNG_rating.setRating(headphone.getRating());
        if (headphone.isFavorite())
            holder.headphone_IMG_favorite.setImageResource(R.drawable.full_wishlist);
        else
            holder.headphone_IMG_favorite.setImageResource(R.drawable.empty_wishlist);
    }

    @Override
    public int getItemCount() {
        return headphones == null ? 0 : headphones.getHeadphones().size();
    }

    private Headphone getItem(int position)
    {
        return headphones.getHeadphones().get(String.valueOf(position));
    }

    public class HeadphoneViewHolder extends RecyclerView.ViewHolder{
        private final ShapeableImageView headphone_IMG_poster;
        private final ShapeableImageView headphone_IMG_favorite;
        private final MaterialTextView headphone_LBL_name;
        private final MaterialTextView headphone_LBL_info;
        private final MaterialTextView headphone_LBL_overview;
        private final AppCompatRatingBar headphone_RTNG_rating;

        public HeadphoneViewHolder(@NonNull View itemView) {
            super(itemView);
            headphone_IMG_poster = itemView.findViewById(R.id.headphone_IMG_poster);
            headphone_IMG_favorite = itemView.findViewById(R.id.headphone_IMG_favorite);
            headphone_LBL_name = itemView.findViewById(R.id.headphone_LBL_name);
            headphone_LBL_info = itemView.findViewById(R.id.headphone_LBL_info);
            headphone_LBL_overview = itemView.findViewById(R.id.headphone_LBL_overview);
            headphone_RTNG_rating = itemView.findViewById(R.id.headphone_RTNG_rating);
            headphone_LBL_name.setOnClickListener(v -> {
                if (headphoneCallback != null)
                    headphoneCallback.moveToHeadphone(getItem(getAdapterPosition()), getAdapterPosition());
            });
            headphone_IMG_poster.setOnClickListener(v -> {
                if (headphoneCallback != null)
                    headphoneCallback.moveToHeadphone(getItem(getAdapterPosition()), getAdapterPosition());
            });
            headphone_IMG_favorite.setOnClickListener(v -> {
                if (headphoneCallback != null)
                    headphoneCallback.wishListHeadphone(getItem(getAdapterPosition()),getAdapterPosition());
            });
        }
    }
}
