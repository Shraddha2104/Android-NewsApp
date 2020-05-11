package com.example.newsapp;

public class CardItem {
    private String ImageUrl;
    private String title;
    private String time;
    private String section;
    private String article_url;
    private String webTitle;
    private String bookmark_date;

    public CardItem(String imageUrl, String Title, String Time, String Section, String Article_url, String webt,String date2) {
        ImageUrl = imageUrl;
        title = Title;
        time = Time;
        section=Section;
        article_url=Article_url;
        webTitle=webt;
        bookmark_date=date2;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public String getTime() {
        return time;
    }
    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getArticle_url(){
        return article_url;
    }
    public String getWebTitle(){
        return webTitle;
    }

    public String getBookmark_date() {
        return bookmark_date;
    }
}