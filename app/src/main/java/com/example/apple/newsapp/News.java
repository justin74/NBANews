package com.example.apple.newsapp;

/**
 * Created by justin on 2017/7/21.
 */

public class News {
    private String title;
    private String author;
    private String date;
    private String webUrl;

    public News(String title, String author, String date, String webUrl) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.webUrl = webUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
