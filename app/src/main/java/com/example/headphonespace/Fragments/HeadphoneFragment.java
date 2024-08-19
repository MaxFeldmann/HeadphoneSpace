package com.example.headphonespace.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
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
import com.example.headphonespace.Models.ReviewList;
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
import java.util.stream.Collectors;

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
    private HeadphoneList headphoneList = new HeadphoneList();

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
                HeadphoneList headphoneList1 = new HeadphoneList();
                Headphone headphone1 = new Headphone();
                int i = 0;
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren())
                {
                    headphone1 = headphoneSnapshot.getValue(Headphone.class);
                    if (headphone1 != null){
                        headphoneList1.addHeadphone(String.valueOf(i++), headphone1);
                        if (headphone1.getName().equals(headphone.getName()))
                            headphoneList1.getHeadphones().replace(String.valueOf(i), headphone);
                    }
                }
                ReviewAdapter reviewAdapter = new ReviewAdapter(new ArrayList<>(headphone.getReviewList().values()));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                main_LST_reviews.setLayoutManager(linearLayoutManager);
                main_LST_reviews.setAdapter(reviewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveUpdatedWishlist(Headphone headphone, boolean favorite) {
        DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        HeadphoneList wishlist = new HeadphoneList();
        final int[] position = new int[1];
        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren()) {
                    if (favorite && headphoneSnapshot.getValue(Headphone.class).getName().equals(headphone.getName()))
                        position[0] = i;
                    wishlist.addHeadphone(String.valueOf(i++), headphoneSnapshot.getValue(Headphone.class));
                }
                if (favorite)
                    wishlist.addHeadphone(String.valueOf(wishlist.getHeadphones().size()), headphone);
                else
                    wishlist.getHeadphones().remove(String.valueOf(position[0]));
                wishlistRef.setValue(wishlist.getHeadphones());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveHeadphoneDatabase() {
        DatabaseReference headphoneRef = FirebaseDatabase.getInstance().getReference("HeadphoneList");
        headphoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HeadphoneList headphoneList1 = new HeadphoneList();
                int i = 0;
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren())
                {
                    Headphone headphone = headphoneSnapshot.getValue(Headphone.class);
                    if (headphone != null)
                        headphoneList1.addHeadphone(String.valueOf(i++), headphone);
                }
                for (i = 0; i < headphoneList1.getHeadphones().size(); i++)
                {
                    if (headphoneList1.getHeadphones().get(String.valueOf(i)).getName().equals(headphone.getName())) {
                        headphoneList1.getHeadphones().replace(String.valueOf(i), headphone);
                        break;
                    }
                }
                headphoneList.setHeadphones(headphoneList1.getHeadphones());
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