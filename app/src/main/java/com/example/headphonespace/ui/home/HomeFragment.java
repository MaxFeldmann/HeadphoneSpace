package com.example.headphonespace.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.headphonespace.Adapters.HeadphoneDefaultAdapter;
import com.example.headphonespace.Adapters.HeadphoneFeaturedAdapter;
import com.example.headphonespace.Interfaces.HeadphoneCallback;
import com.example.headphonespace.Models.Headphone;
import com.example.headphonespace.Models.HeadphoneList;
import com.example.headphonespace.R;
import com.example.headphonespace.databinding.FragmentHomeBinding;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private HeadphoneList headphoneList;
    private HeadphoneList featuredHeadphoneList;
    private FragmentHomeBinding binding;
    private RecyclerView main_LST_horizontal_headphone;
    private RecyclerView main_LST_headphones;
    private MaterialTextView text_home;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        featuredHeadphoneList = new HeadphoneList().setName("FeaturedHeadphoneList").setHeadphones(new HashMap<>());
        headphoneList = new HeadphoneList().setHeadphones(new HashMap<>());
        headphoneList.setName("HeadphoneList");

        findViews(root);
        initViews();
        return root;
    }

    private void initViews() {
        loadHeadphoneList();
        text_home.setOnClickListener(v -> moveToPopular());
    }

    private void moveToPopular() {
        FragmentActivity activity = (FragmentActivity) binding.getRoot().getContext();
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_popular, new Bundle());
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

    private void findViews(View root) {
        main_LST_horizontal_headphone = root.findViewById(R.id.main_LST_horizontal_headphone);
        main_LST_headphones = root.findViewById(R.id.main_LST_headphones);
        text_home = root.findViewById(R.id.text_home);
    }

    public void loadHeadphoneList() {
        DatabaseReference headphoneRef = FirebaseDatabase.getInstance().getReference("HeadphoneList");
        headphoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                headphoneList = new HeadphoneList();
                int i = 0;
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren())
                {
                    Headphone headphone = headphoneSnapshot.getValue(Headphone.class);
                    if (headphone != null)
                        headphoneList.addHeadphone(String.valueOf(i++), headphone);
                }
                updateWishlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference featuredHeadphoneRef = FirebaseDatabase.getInstance().getReference("FeaturedHeadphoneList");
        featuredHeadphoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                featuredHeadphoneList = new HeadphoneList();
                int i = 0;
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren())
                {
                    Headphone headphone = headphoneSnapshot.getValue(Headphone.class);
                    if (headphone != null)
                        featuredHeadphoneList.addHeadphone(String.valueOf(i++), headphone);
                }
                updateWishlistFeatured();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateWishlistFeatured();
                updateWishlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateWishlist() {
        if (FirebaseAuth.getInstance().getUid() != null && headphoneList != null) {
            DatabaseReference userWishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(FirebaseAuth.getInstance().getUid());
            userWishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot wishlistSnapshot : snapshot.getChildren()) {
                        Headphone headphone = wishlistSnapshot.getValue(Headphone.class);
                        if (headphone != null) {
                            for (Headphone headphoneFromList : headphoneList.getHeadphones().values()) {
                                if (headphone.getName().equals(headphoneFromList.getName())) {
                                    headphoneFromList.setFavorite(true);
                                    break;
                                }
                            }
                        }
                    }
                    setAdapter(headphoneList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void setAdapter(HeadphoneList adapterList) {
        HeadphoneDefaultAdapter headphoneDefaultAdapter = new HeadphoneDefaultAdapter(adapterList);
        LinearLayoutManager linearLayoutManager_vertical = new LinearLayoutManager(getActivity());
        linearLayoutManager_vertical.setOrientation(RecyclerView.VERTICAL);
        main_LST_headphones.setLayoutManager(linearLayoutManager_vertical);
        main_LST_headphones.setAdapter(headphoneDefaultAdapter);
        headphoneDefaultAdapter.setHeadphoneCallback(new HeadphoneCallback() {
            @Override
            public void wishListHeadphone(Headphone headphone, int position) {
                headphone.setFavorite(!headphone.isFavorite());
                headphoneDefaultAdapter.notifyItemChanged(position);
                saveUpdatedWishlist(headphone, headphone.isFavorite());
            }

            @Override
            public void moveToHeadphone(Headphone headphone, int position) {
                FragmentActivity activity = (FragmentActivity) binding.getRoot().getContext();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                Bundle args = new Bundle();
                args.putSerializable("headphone", headphone);
                navController.navigate(R.id.nav_headphone, args);
            }
        });
    }

    private void updateWishlistFeatured() {
        if (FirebaseAuth.getInstance().getUid() != null && featuredHeadphoneList != null) {
            DatabaseReference userWishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(FirebaseAuth.getInstance().getUid());
            userWishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot wishlistSnapshot : snapshot.getChildren()) {
                        Headphone headphone = wishlistSnapshot.getValue(Headphone.class);
                        if (headphone != null) {
                            for (Headphone headphoneFromList : featuredHeadphoneList.getHeadphones().values()) {
                                if (headphone.getName().equals(headphoneFromList.getName())) {
                                    headphoneFromList.setFavorite(true);
                                    break;
                                }
                            }
                        }
                    }
                    setFeaturedAdapter(featuredHeadphoneList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void setFeaturedAdapter(HeadphoneList headphoneList) {
        HeadphoneFeaturedAdapter headphoneFeaturedAdapter = new HeadphoneFeaturedAdapter(headphoneList);
        LinearLayoutManager linearLayoutManager_horizontal = new LinearLayoutManager(getActivity());
        linearLayoutManager_horizontal.setOrientation(RecyclerView.HORIZONTAL);
        main_LST_horizontal_headphone.setLayoutManager(linearLayoutManager_horizontal);
        main_LST_horizontal_headphone.setAdapter(headphoneFeaturedAdapter);
        headphoneFeaturedAdapter.setHeadphoneCallback(new HeadphoneCallback() {
            @Override
            public void wishListHeadphone(Headphone headphone, int position) {
                headphone.setFavorite(!headphone.isFavorite());
                headphoneFeaturedAdapter.notifyItemChanged(position);
                saveUpdatedWishlist(headphone, headphone.isFavorite());
            }

            @Override
            public void moveToHeadphone(Headphone headphone, int position) {
                FragmentActivity activity = (FragmentActivity) binding.getRoot().getContext();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                Bundle args = new Bundle();
                args.putSerializable("headphone", headphone);
                navController.navigate(R.id.nav_headphone, args);
            }
        });
    }

    public void saveHeadphoneList() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("HeadphoneList");

        myRef.setValue(headphoneList.getHeadphones());

        DatabaseReference myRefFeatured = FirebaseDatabase.getInstance().getReference("FeaturedHeadphoneList");

        myRefFeatured.setValue(featuredHeadphoneList.getHeadphones());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}