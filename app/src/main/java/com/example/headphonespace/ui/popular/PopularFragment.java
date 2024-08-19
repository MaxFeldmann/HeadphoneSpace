package com.example.headphonespace.ui.popular;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.headphonespace.Adapters.HeadphoneDefaultAdapter;
import com.example.headphonespace.Interfaces.HeadphoneCallback;
import com.example.headphonespace.MainActivity;
import com.example.headphonespace.Models.Headphone;
import com.example.headphonespace.Models.HeadphoneList;
import com.example.headphonespace.R;
import com.example.headphonespace.databinding.FragmentPopularBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class PopularFragment extends Fragment {
    private RecyclerView popular_LST_headphones;
    private FragmentPopularBinding binding;
    private HeadphoneList headphoneList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentPopularBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findViews(root);
        initViews();
        return root;
    }

    private void findViews(View root) {
        popular_LST_headphones = root.findViewById(R.id.popular_LST_headphones);
    }

    private void initViews() {
        loadPopularHeadphones();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (favorite)
            wishlist.addHeadphone(String.valueOf(wishlist.getHeadphones().size()), headphone);
        else
            wishlist.getHeadphones().remove(String.valueOf(position[0]));
        wishlistRef.setValue(wishlist.getHeadphones());
    }

    private void loadPopularHeadphones() {
        DatabaseReference headphoneRef = FirebaseDatabase.getInstance().getReference("HeadphoneList");
        headphoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                headphoneList = new HeadphoneList();
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren())
                {
                    Headphone headphone = headphoneSnapshot.getValue(Headphone.class);
                    if (headphone != null && headphone.getRating() > 4.0f)
                        headphoneList.addHeadphone(String.valueOf(i++), headphone);
                }
                HeadphoneDefaultAdapter headphoneDefaultAdapter = new HeadphoneDefaultAdapter(headphoneList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                popular_LST_headphones.setLayoutManager(linearLayoutManager);
                popular_LST_headphones.setAdapter(headphoneDefaultAdapter);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}