package com.example.headphonespace.Models;

import java.util.HashMap;

public class HeadphoneList {
    private String name ="";
    private HashMap<String, Headphone> headphones = new HashMap<>();

    public HeadphoneList()
    {
    }

    public String getName(){return name;}

    public HashMap<String, Headphone> getHeadphones() {
        return headphones;
    }

    public HeadphoneList setHeadphones(HashMap<String, Headphone> headphones) {
        this.headphones = headphones;
        return this;
    }

    public HeadphoneList setName(String name) {
        this.name = name;
        return this;
    }

    public HeadphoneList addHeadphone(String position, Headphone headphone)
    {
        headphones.put(position, headphone);
        return this;
    }

    @Override
    public String toString() {
        return "HeadphoneList{" +
                "name='" + name + '\'' +
                ", headphones=" + headphones +
                '}';
    }
}
