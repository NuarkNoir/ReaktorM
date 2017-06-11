package com.mobile.nuark.reaktor;

/**
 * Created by Nuark with love on 21.05.2017.
 * Protected by QPL-1.0
 */

public class MPost {
    private String author, imageUrl;

    public MPost(String author, String imageUrl) {

        this.author = author;
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
