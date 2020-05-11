package com.example.newsapp;

public class BookmarkItem {
    private String title;
    private String section;
    private String image;
    private String detailed_url;
    private String date;

    BookmarkItem(String bk_title,String bk_section,String bk_img,String date1,String detailed_url1){
        title=bk_title;
        section=bk_section;
        image=bk_img;
        date=date1;
        detailed_url=detailed_url1;
    }
    public String getDate() {
        return date;
    }

    public String getDetailed_url() {
        return detailed_url;
    }

    public String getImage() {
        return image;
    }

    public String getSection() {
        return section;
    }

    public String getTitle() {
        return title;
    }
}

