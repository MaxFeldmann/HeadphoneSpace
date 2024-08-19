package com.example.headphonespace.Interfaces;

import com.example.headphonespace.Models.Headphone;

public interface HeadphoneCallback {
    void wishListHeadphone(Headphone headphone, int position);
    void moveToHeadphone(Headphone headphone, int position);
}
