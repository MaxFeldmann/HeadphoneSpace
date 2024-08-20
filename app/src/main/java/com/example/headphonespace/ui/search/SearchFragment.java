package com.example.headphonespace.ui.search;

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

import com.example.headphonespace.Adapters.HeadphoneSearchAdapter;
import com.example.headphonespace.Interfaces.HeadphoneCallback;
import com.example.headphonespace.Models.Headphone;
import com.example.headphonespace.Models.HeadphoneList;
import com.example.headphonespace.R;
import com.example.headphonespace.databinding.FragmentSearchBinding;
import androidx.appcompat.widget.SearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;


public class SearchFragment extends Fragment {
    private RecyclerView search_LST_headphones;
    private SearchView searchview_bar;
    private FragmentSearchBinding binding;

    private HeadphoneList headphoneList;
    private HeadphoneList searchHeadphone;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findViews(root);
        initView();
        return root;
    }

    private void initView() {
        loadHeadphoneList();

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
                    if (favorite && foundHeadphone != null && headphone.getName().equals(foundHeadphone.getName()))
                        found = true;
                    else if (foundHeadphone != null && !foundHeadphone.getName().equals(headphone.getName()))
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

    private void loadHeadphoneList() {
        DatabaseReference headphoneRef = FirebaseDatabase.getInstance().getReference("HeadphoneList");
        headphoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                headphoneList = new HeadphoneList();
                for (DataSnapshot headphoneSnapshot : snapshot.getChildren()) {
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

    private void setAdapter(HeadphoneList headphoneList) {
        searchview_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchHeadphone = new HeadphoneList();
                int i = 0;
                for (Headphone headphone : headphoneList.getHeadphones().values())
                {
                    if ((headphone.getName().toLowerCase()).contains(query.toLowerCase()) || headphone.getAttributes().toLowerCase().contains(query.toLowerCase()))
                        searchHeadphone.addHeadphone(String.valueOf(i++), headphone);
                }
                HeadphoneSearchAdapter headphoneSearchAdapter = new HeadphoneSearchAdapter(searchHeadphone);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                search_LST_headphones.setLayoutManager(linearLayoutManager);
                search_LST_headphones.setAdapter(headphoneSearchAdapter);
                headphoneSearchAdapter.setHeadphoneCallback(new HeadphoneCallback() {
                    @Override
                    public void wishListHeadphone(Headphone headphone, int position) {
                        headphone.setFavorite(!headphone.isFavorite());
                        headphoneSearchAdapter.notifyItemChanged(position);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void findViews(View root) {
        search_LST_headphones = root.findViewById(R.id.search_LST_headphones);
        searchview_bar = root.findViewById(R.id.searchview_bar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}