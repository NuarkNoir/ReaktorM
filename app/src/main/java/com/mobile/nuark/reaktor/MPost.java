package com.mobile.nuark.reaktor;

/**
 * Created by Nuark with love on 21.05.2017.
 * Protected by QPL-1.0
 */

class MPost {
    private String author, imageUrl;

    MPost(String author, String imageUrl) {
        this.author = author;
        this.imageUrl = imageUrl;
    }

    String getAuthor() {
        return author;
    }

    String getImageUrl() {
        return imageUrl;
    }
}
