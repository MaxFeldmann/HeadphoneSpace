package com.example.headphonespace.Adapters;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.headphonespace.Models.Review;
import com.example.headphonespace.R;
import com.example.headphonespace.Utilities.ImageLoader;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final ArrayList<Review> reviewList;

    public ReviewAdapter(ArrayList<Review> reviewList)
    {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_headphone_item, parent, false);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        if (review.getUserUri() != null)
            ImageLoader.getInstance().load(String.valueOf(review.getUserUri()), holder.user_IMG_picture);
        else
            holder.user_IMG_picture.setImageResource(R.drawable.unavailable_photo);
        if (review.getUserName() != null)
            holder.review_LBL_username.setText(review.getUserName());
        else
            holder.review_LBL_username.setText(R.string.anonymous);
        holder.review_LBL_contents.setText(review.getContents());
        holder.review_LBL_title.setText(review.getTitle());
        holder.review_LBL_headphone_name.setText(review.getHeadphoneName());
        holder.review_RTNG_rating.setRating(review.getRating());
        holder.review_CARD_data.setOnClickListener(v -> {
            ArrayList<ObjectAnimator> animations = new ArrayList<>();
            if (review.isCollapsed()){
                animations.add(ObjectAnimator
                        .ofInt(holder.review_LBL_contents, "maxLines", holder.review_LBL_contents.getLineCount())
                        .setDuration((Math.max(holder.review_LBL_contents.getLineCount() - Review.MIN_LINES_COLLAPSED, 0)) * 50L));
            }
            else {
                animations.add(ObjectAnimator
                        .ofInt(holder.review_LBL_contents, "maxLines", Review.MIN_LINES_COLLAPSED)
                        .setDuration((Math.max(holder.review_LBL_contents.getLineCount() - Review.MAX_LINES_COLLAPSED, 0)) * 50L));

            }
            animations.forEach(ObjectAnimator::start);

            review.setCollapsed(!review.isCollapsed());
        });
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();
    }
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView user_IMG_picture;
        private final MaterialTextView review_LBL_username;
        private final MaterialTextView review_LBL_title;
        private final MaterialTextView review_LBL_contents;
        private final MaterialTextView review_LBL_headphone_name;
        private final AppCompatRatingBar review_RTNG_rating;
        private final CardView review_CARD_data;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            review_CARD_data = itemView.findViewById(R.id.review_CARD_data);
            user_IMG_picture = itemView.findViewById(R.id.user_IMG_picture);
            review_LBL_username = itemView.findViewById(R.id.review_LBL_username);
            review_LBL_title = itemView.findViewById(R.id.review_LBL_title);
            review_LBL_contents = itemView.findViewById(R.id.review_LBL_contents);
            review_LBL_headphone_name = itemView.findViewById(R.id.review_LBL_headphone_name);
            review_RTNG_rating = itemView.findViewById(R.id.review_RTNG_rating);
        }
    }
}
