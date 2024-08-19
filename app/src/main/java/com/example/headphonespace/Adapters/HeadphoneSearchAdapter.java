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

public class HeadphoneSearchAdapter extends RecyclerView.Adapter<HeadphoneSearchAdapter.HeadphoneViewHolder>{
    private final HeadphoneList headphones;
    private HeadphoneCallback headphoneCallback;

    public HeadphoneSearchAdapter(HeadphoneList headphones)
    {
        this.headphones = headphones;
    }

    public void setHeadphoneCallback(HeadphoneCallback headphoneCallback)
    {
        this.headphoneCallback = headphoneCallback;
    }

    @NonNull
    @Override
    public HeadphoneSearchAdapter.HeadphoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_headphone_item, parent, false);
        return new HeadphoneSearchAdapter.HeadphoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadphoneSearchAdapter.HeadphoneViewHolder holder, int position) {
        Headphone headphone = headphones.getHeadphones().get(String.valueOf(position));

        holder.search_IMG_poster.setImageResource(headphone.getHeadphone_IMG());
        /*ImageLoader.getInstance().load(ContextCompat
                        .getDrawable(holder.search_IMG_poster.getContext(), headphone.getHeadphone_IMG())
                , holder.search_IMG_poster);*/
        holder.search_LBL_name.setText(headphone.getName());
        holder.search_LBL_overview.setText(String.join(", ", headphone.getAttributes()));
        holder.search_LBL_info.setText(headphone.getHeadphone_info());
        holder.search_RTNG_rating.setRating(headphone.getRating());
        if (headphone.isFavorite())
            holder.search_IMG_favorite.setImageResource(R.drawable.full_wishlist);
        else
            holder.search_IMG_favorite.setImageResource(R.drawable.empty_wishlist);
        holder.search_IMG_poster.setOnClickListener(v -> {
            //move to headphone page
        });
        holder.search_LBL_name.setOnClickListener(v -> {
            //move to headphone page
        });

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
        private final ShapeableImageView search_IMG_poster;
        private final ShapeableImageView search_IMG_favorite;
        private final MaterialTextView search_LBL_name;
        private final MaterialTextView search_LBL_info;
        private final MaterialTextView search_LBL_overview;
        private final AppCompatRatingBar search_RTNG_rating;
        private final CardView search_CARD_data;

        public HeadphoneViewHolder(@NonNull View itemView) {
            super(itemView);
            search_IMG_poster = itemView.findViewById(R.id.search_IMG_poster);
            search_IMG_favorite = itemView.findViewById(R.id.search_IMG_favorite);
            search_LBL_name = itemView.findViewById(R.id.search_LBL_name);
            search_LBL_info = itemView.findViewById(R.id.search_LBL_info);
            search_LBL_overview = itemView.findViewById(R.id.search_LBL_overview);
            search_RTNG_rating = itemView.findViewById(R.id.search_RTNG_rating);
            search_CARD_data = itemView.findViewById(R.id.search_CARD_data);
            search_CARD_data.setOnClickListener(v -> {
                if (headphoneCallback != null)
                    headphoneCallback.moveToHeadphone(getItem(getAdapterPosition()), getAdapterPosition());
            });
            search_IMG_favorite.setOnClickListener(v ->{
                if (headphoneCallback != null)
                    headphoneCallback.wishListHeadphone(getItem(getAdapterPosition()),getAdapterPosition());
            });
        }
    }
}