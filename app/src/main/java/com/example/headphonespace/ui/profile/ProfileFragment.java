package com.example.headphonespace.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.headphonespace.Adapters.ReviewAdapter;
import com.example.headphonespace.Models.Review;
import com.example.headphonespace.R;
import com.example.headphonespace.Utilities.ImageLoader;
import com.example.headphonespace.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private AppCompatImageView profile_IMG_image;
    private TextView profile_LBL_name;
    private TextView profile_LBL_email;
    private TextView profile_LBL_phone;
    private TextView profile_LBL_wishlist;
    private RecyclerView profile_LST_review;
    private FragmentProfileBinding binding;
    private ArrayList<Review> reviewList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findView(root);
        initViews();
        return root;
    }

    private void initViews() {
        loadReviewList();
        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl() == null)
            profile_IMG_image.setImageResource(R.drawable.unavailable_photo);
        else
            ImageLoader.getInstance().load(String.valueOf(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl()), profile_IMG_image);
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null)
            profile_LBL_name.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null)
            profile_LBL_email.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null)
            profile_LBL_phone.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
        profile_LBL_wishlist.setOnClickListener(v -> moveToWishlist());
    }

    private void moveToWishlist() {
        FragmentActivity activity = (FragmentActivity) binding.getRoot().getContext();
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_wishlist, new Bundle());
    }

    private void findView(View root) {
        profile_IMG_image = root.findViewById(R.id.profile_IMG_image);
        profile_LBL_name = root.findViewById(R.id.profile_LBL_name);
        profile_LBL_email = root.findViewById(R.id.profile_LBL_email);
        profile_LBL_phone = root.findViewById(R.id.profile_LBL_phone);
        profile_LBL_wishlist = root.findViewById(R.id.profile_LBL_wishlist);
        profile_LST_review = root.findViewById(R.id.profile_LST_review);
    }

    private void loadReviewList()
    {
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("ReviewList").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        reviewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList = new ArrayList<>();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren())
                {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null)
                        reviewList.add(review);
                }
                setAdapter(reviewList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapter(ArrayList<Review> reviewList) {
        ReviewAdapter reviewAdapter = new ReviewAdapter(reviewList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        profile_LST_review.setLayoutManager(linearLayoutManager);
        profile_LST_review.setAdapter(reviewAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}