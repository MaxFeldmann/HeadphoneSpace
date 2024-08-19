package com.example.headphonespace.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.headphonespace.Interfaces.HeadphoneCallback;
import com.example.headphonespace.Models.Headphone;
import com.example.headphonespace.Models.HeadphoneList;
import com.example.headphonespace.R;
import com.example.headphonespace.Utilities.ImageLoader;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class HeadphoneFeaturedAdapter extends RecyclerView.Adapter<HeadphoneFeaturedAdapter.HeadphoneViewHolder> {
    private final HeadphoneList headphones;
    private HeadphoneCallback headphoneCallback;

    public HeadphoneFeaturedAdapter(HeadphoneList headphones) {
        this.headphones = headphones;
    }

    public void setHeadphoneCallback(HeadphoneCallback headphoneCallback) {
        this.headphoneCallback = headphoneCallback;
    }

    @NonNull
    @Override
    public HeadphoneFeaturedAdapter.HeadphoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_headphone_item, parent, false);
        return new HeadphoneFeaturedAdapter.HeadphoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadphoneFeaturedAdapter.HeadphoneViewHolder holder, int position) {
        Headphone headphone = headphones.getHeadphones().get(String.valueOf(position));

        holder.headphone_vertical_IMG_poster.setImageResource(headphone.getHeadphone_IMG());
/*        ImageLoader.getInstance().load(ContextCompat
                        .getDrawable(holder.headphone_vertical_IMG_poster.getContext(), headphone.getHeadphone_IMG())
                , holder.headphone_vertical_IMG_poster);*/
        holder.headphone_vertical_IMG_poster.setOnClickListener(v -> {

        });
        holder.headphone_vertical_LBL_name.setText(headphone.getName());
        holder.headphone_vertical_LBL_info.setText(headphone.getHeadphone_info());
        holder.headphone_vertical_RTNG_rating.setRating(headphone.getRating());
        if (headphone.isFavorite())
            holder.headphone_vertical_IMG_favorite.setImageResource(R.drawable.full_wishlist);
        else
            holder.headphone_vertical_IMG_favorite.setImageResource(R.drawable.empty_wishlist);
        holder.headphone_vertical_LBL_name.setOnClickListener(v -> {
            //move to headphone page
        });
        holder.headphone_vertical_IMG_poster.setOnClickListener(v -> {
            //move to headphone page
        });


    }

    @Override
    public int getItemCount() {
        return headphones == null ? 0 : headphones.getHeadphones().size();
    }

    private Headphone getItem(int position) {
        return headphones.getHeadphones().get(String.valueOf(position));
    }

    public class HeadphoneViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView headphone_vertical_IMG_poster;
        private final ShapeableImageView headphone_vertical_IMG_favorite;
        private final MaterialTextView headphone_vertical_LBL_name;
        private final MaterialTextView headphone_vertical_LBL_info;
        private final AppCompatRatingBar headphone_vertical_RTNG_rating;

        public HeadphoneViewHolder(@NonNull View itemView) {
            super(itemView);
            headphone_vertical_IMG_poster = itemView.findViewById(R.id.headphone_vertical_IMG_poster);
            headphone_vertical_IMG_favorite = itemView.findViewById(R.id.headphone_vertical_IMG_favorite);
            headphone_vertical_LBL_name = itemView.findViewById(R.id.headphone_vertical_LBL_name);
            headphone_vertical_LBL_info = itemView.findViewById(R.id.headphone_vertical_LBL_info);
            headphone_vertical_RTNG_rating = itemView.findViewById(R.id.headphone_vertical_RTNG_rating);
            CardView headphone_vertical_CARD_data = itemView.findViewById(R.id.headphone_vertical_CARD_data);
            headphone_vertical_CARD_data.setOnClickListener(v -> {
                if (headphoneCallback != null)
                    headphoneCallback.moveToHeadphone(getItem(getAdapterPosition()), getAdapterPosition());
            });
            headphone_vertical_IMG_favorite.setOnClickListener(v -> {
                if (headphoneCallback != null)
                    headphoneCallback.wishListHeadphone(getItem(getAdapterPosition()), getAdapterPosition());
            });
        }
    }
}
