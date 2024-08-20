package com.example.headphonespace.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.headphonespace.Adapters.ReviewAdapter;
import com.example.headphonespace.Models.Headphone;
import com.example.headphonespace.Models.HeadphoneList;
import com.example.headphonespace.Models.Review;
import com.example.headphonespace.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HeadphoneFragment extends Fragment {
    private TextView full_LBL_name;
    private ShapeableImageView headphone_IMG_poster;
    private MaterialTextView headphone_LBL_overview;
    private ShapeableImageView headphone_graph_poster;
    private RecyclerView main_LST_reviews;
    private EditText review_TXT_title;
    private EditText review_TXT_body;
    private AppCompatRatingBar review_RTNG_rating;
    private MaterialButton review_BTN_send;
    private ShapeableImageView headphone_frag_IMG_favorite;

    private Headphone headphone;
   /* private HeadphoneList headphoneList;*/

    public HeadphoneFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_headphone, container, false);

        if (getArguments() != null)
            headphone = (Headphone) getArguments().getSerializable("headphone");

        findViews(v);
        initViews();
        return v;
    }

    private void initViews() {
        full_LBL_name.setText(headphone.getName());
        headphone_IMG_poster.setImageResource(headphone.getHeadphone_IMG());
        headphone_LBL_overview.setText(headphone.getHeadphone_info());
        headphone_graph_poster.setImageResource(headphone.getHeadphone_IMG_graph());

        review_BTN_send.setOnClickListener( v -> {
            String title = Objects.requireNonNull(String.valueOf(review_TXT_title.getText()));
            String contents = Objects.requireNonNull(String.valueOf(review_TXT_body.getText()));

            if (!title.isEmpty() && !contents.isEmpty() && review_RTNG_rating.getRating() > 0)
            {
                Review review = new Review(FirebaseAuth.getInstance().getCurrentUser(), contents, title,headphone.getName(), review_RTNG_rating.getRating());
                headphone.addReview(String.valueOf(headphone.getReviewList().size()), review);
                saveHeadphoneDatabase();
                saveReviewDatabase(review);
                Toast.makeText(this.getContext(), "Review sent!", Toast.LENGTH_SHORT).show();
                review_TXT_title.setText("");
                review_TXT_body.setText("");
                review_RTNG_rating.setRating(0.0f);
            }
            else
                Toast.makeText(this.getContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
        });
        getUpdatedReviews();
    }

    private void saveReviewDatabase(Review userReview) {
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("ReviewList").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        reviewRef.push().setValue(userReview);
    }

    private void getUpdatedReviews() {
        DatabaseReference headphonesRef = FirebaseDatabase.getInstance().getReference("HeadphoneList");
        headphonesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren())
                {
                    Headphone headphoneFromSnap = headphoneSnapshot.getValue(Headphone.class);
                    if (headphoneFromSnap != null && headphoneFromSnap.getName().equals(headphone.getName())){
                        headphone = headphoneFromSnap;
                    }
                }
                updateWishlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateWishlist() {
        if (FirebaseAuth.getInstance().getUid() != null && headphone != null) {
            DatabaseReference userWishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(FirebaseAuth.getInstance().getUid());
            userWishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot wishlistSnapshot : snapshot.getChildren()) {
                        Headphone headphoneWishlist = wishlistSnapshot.getValue(Headphone.class);
                        if (headphoneWishlist != null && headphoneWishlist.getName().equals(headphone.getName()))
                            headphone.setFavorite(true);
                    }
                    setAdapter(headphone);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void setAdapter(Headphone headphone) {
        if (headphone.isFavorite())
            headphone_frag_IMG_favorite.setImageResource(R.drawable.full_wishlist);
        else
            headphone_frag_IMG_favorite.setImageResource(R.drawable.empty_wishlist);

        headphone_frag_IMG_favorite.setOnClickListener(v -> {
            headphone.setFavorite(!headphone.isFavorite());
            saveUpdatedWishlist(headphone, headphone.isFavorite());
            if (headphone.isFavorite())
                headphone_frag_IMG_favorite.setImageResource(R.drawable.full_wishlist);
            else
                headphone_frag_IMG_favorite.setImageResource(R.drawable.empty_wishlist);
        });

        ReviewAdapter reviewAdapter = new ReviewAdapter(new ArrayList<>(headphone.getReviewList().values()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        main_LST_reviews.setLayoutManager(linearLayoutManager);
        main_LST_reviews.setAdapter(reviewAdapter);
    }

    private void saveUpdatedWishlist(Headphone headphone, boolean favorite) {
        DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        wishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Headphone> wishlist = new HashMap<>();
                boolean found = false;
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren()) {
                    Headphone foundHeadphone = headphoneSnapshot.getValue(Headphone.class);
                    if (favorite && foundHeadphone != null && foundHeadphone.getName().equals(headphone.getName()))
                        found = true;
                    else
                        wishlist.put(headphoneSnapshot.getKey(), foundHeadphone);
                }
                if (favorite && !found){
                    String newKey = wishlistRef.push().getKey();
                    if (newKey != null)
                        wishlist.put(newKey, headphone);
                }
                wishlistRef.setValue(wishlist);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveHeadphoneDatabase() {
        DatabaseReference headphoneRef = FirebaseDatabase.getInstance().getReference("HeadphoneList");
        headphoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HeadphoneList headphoneList = new HeadphoneList();
                int i = 0;
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren())
                {
                    Headphone headphone = headphoneSnapshot.getValue(Headphone.class);
                    if (headphone != null)
                        headphoneList.addHeadphone(String.valueOf(i++), headphone);
                }
                for (i = 0; i < headphoneList.getHeadphones().size(); i++)
                {
                    if (Objects.requireNonNull(headphoneList.getHeadphones().get(String.valueOf(i))).getName().equals(headphone.getName())) {
                        headphoneList.getHeadphones().replace(String.valueOf(i), headphone);
                        break;
                    }
                }
                headphoneRef.setValue(headphoneList.getHeadphones());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void findViews(View v) {
        full_LBL_name = v.findViewById(R.id.full_LBL_name);
        headphone_frag_IMG_favorite = v.findViewById(R.id.headphone_frag_IMG_favorite);
        headphone_IMG_poster = v.findViewById(R.id.headphone_IMG_poster);
        headphone_LBL_overview = v.findViewById(R.id.headphone_LBL_overview);
        headphone_graph_poster = v.findViewById(R.id.headphone_graph_poster);
        main_LST_reviews = v.findViewById(R.id.main_LST_reviews);
        review_TXT_title = v.findViewById(R.id.review_TXT_title);
        review_TXT_body = v.findViewById(R.id.review_TXT_body);
        review_RTNG_rating = v.findViewById(R.id.review_RTNG_rating);
        review_BTN_send = v.findViewById(R.id.review_BTN_send);
    }
}