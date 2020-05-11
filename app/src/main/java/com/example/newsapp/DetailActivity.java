package com.example.newsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bottomnavigation.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {
    ImageView imgView;
    TextView title1;
    TextView desc;
    TextView date1;
    TextView section1;
    TextView full_news;
    TextView progress_t;
    Toolbar toolbar;
    private ProgressBar spinner;
    private MenuItem bookmark;
    private MenuItem bookmarked;
    private MenuItem twitter;
    CardView cardView;
    String main_title;
    BookmarkItem bk;
    String full_news1;
    BookmarkItem bk_delete;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        imgView = findViewById(R.id.image_view_detail);
        title1 = findViewById(R.id.detailed_title);
        desc = findViewById(R.id.detailed_desc);
        date1=findViewById(R.id.detailed_date);
        section1=findViewById(R.id.detailed_section);
        full_news=findViewById(R.id.detailed_url);
        cardView=findViewById(R.id.card_view);
        progress_t=findViewById(R.id.progress_text);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        progress_t.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.INVISIBLE);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent=getIntent();
        url=intent.getStringExtra("article_url");
        parseJSON(url);


    }
    @Override
    public void onResume()
    {
        super.onResume(); // paste toast here

        parseJSON(url);
    }
    public int check_bookmarked(String title,String url){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        if(arr!=null) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getTitle().equals(title)) {

                    return 1;
                }


            }
        }
        return 0;

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar, menu);
        bookmark = menu.findItem(R.id.bookmark);
        bookmarked = menu.findItem(R.id.bookmarked);
        twitter=menu.findItem(R.id.share);
        bookmarked.setVisible(false);
        bookmark.setVisible(false);
        twitter.setVisible(false);

        return true;
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            //Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {
            share(); // close this activity and return to preview activity (if there is any)
        }
//
        return super.onOptionsItemSelected(item);
    }
    public void share(){
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                urlEncode("Check out this Link:"),
                urlEncode(full_news1));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
        startActivity(intent);
    }
    private void parseJSON(final String url){

        String uri="https://androidbackend-276418.wl.r.appspot.com/gid?id="+url;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,uri,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String article_url=url;
                            JSONObject data = response.getJSONObject("data");
//                            JSONObject response = response.getJSONObject("response");
                            JSONObject json = data.getJSONObject("response");
                            //JSONObject json = response.getJSONObject("response");
                            JSONObject  content=json.getJSONObject("content");
                            String title=content.getString("webTitle");

                            String date=content.getString("webPublicationDate");
                            String date2=formatDate(date);
                            System.out.println(date2);
                            date1.setText(date2);

                            String section=content.getString("sectionName");
//

                            full_news1=content.getString("webUrl");

                            if(check_bookmarked(title,url)==1)
                            {
                                bookmark.setVisible(false);
                                // show the menu item
                                bookmarked.setVisible(true);
                                twitter.setVisible(true);
                            }
                            else{
                                bookmarked.setVisible(false);
                                bookmark.setVisible(true);
                                twitter.setVisible(true);
                            }
                            title1.setText(title);
                            main_title=title;
                            section1.setText(section);
                            toolbar.setTitle(title);
                            full_news.setClickable(true);
                            Spanned html = Html.fromHtml(
                                    "<a href='"+full_news1+"'>View Full Article</a>");

                            full_news.setMovementMethod(LinkMovementMethod.getInstance());

                            // Set TextView text from html
                            full_news.setText(html);
//

                            JSONObject blocks=content.getJSONObject("blocks");
                            String imageUrl;
                            if(blocks.has("main")) {
                                JSONObject main = blocks.getJSONObject("main");
                                JSONArray elements = main.getJSONArray("elements");
                                JSONObject ele_index = elements.getJSONObject(0);

                                JSONArray assets = ele_index.getJSONArray("assets");
                                if(assets.length()!=0) {
                                    JSONObject ass_index = assets.getJSONObject(0);
                                    imageUrl = ass_index.getString("file");

                                }
                                else
                                    imageUrl="https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";

                            }
                            else
                                imageUrl="https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";

                            JSONArray body=blocks.getJSONArray("body");
                            String desc1="";
                            for(int i=0;i<body.length();i++){
                                JSONObject index = body.getJSONObject(i);
                                String bodyHTML=index.getString("bodyHtml");
                                desc1=bodyHTML+desc1;
                            }

                            bk=new BookmarkItem(title,section,imageUrl,bookmark_date(date),article_url);

                            Spanned sp = Html.fromHtml(desc1);
                            desc.setText(sp);
                            Picasso.with(DetailActivity.this).load(imageUrl).fit().centerInside().into(imgView);
                            spinner.setVisibility(View.GONE);
                            progress_t.setVisibility(View.GONE);
                            cardView.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
//
    }
    @SuppressLint("SimpleDateFormat")
    private String formatDate(String str_date) throws ParseException {

        str_date=str_date.substring(0,10);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(str_date);
        String date = format.format(date1);

        if(date.endsWith("01") && !date.endsWith("11"))
            format = new SimpleDateFormat("dd MMM yyyy");

        else if(date.endsWith("02") && !date.endsWith("12"))
            format = new SimpleDateFormat("dd MMM yyyy");

        else if(date.endsWith("03") && !date.endsWith("13"))
            format = new SimpleDateFormat("dd MMM yyyy");

        else
            format = new SimpleDateFormat("dd MMM yyyy");

        String yourDate = format.format(date1);
        return yourDate;
    }
    private String bookmark_date(String str_date) throws ParseException {

        str_date=str_date.substring(0,10);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(str_date);
        String date = format.format(date1);

        if(date.endsWith("01") && !date.endsWith("11"))
            format = new SimpleDateFormat("d MMM");

        else if(date.endsWith("02") && !date.endsWith("12"))
            format = new SimpleDateFormat("d MMM");

        else if(date.endsWith("03") && !date.endsWith("13"))
            format = new SimpleDateFormat("d MMM");

        else
            format = new SimpleDateFormat("d MMM");

        String yourDate = format.format(date1);
        return yourDate;
    }

    public void set_bookmark(MenuItem item) {

        Toast.makeText(getApplicationContext(),
                '"'+bk.getTitle()+'"'+ " was added to bookmarks", Toast.LENGTH_SHORT).show();
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //sharedPreferences.edit().clear().commit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        if(arr==null){
            arr=new ArrayList<>();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        arr.add(bk);
        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();
        bookmark.setVisible(false);
        bookmarked.setVisible(true);


    }

    public void remove_bookmark(MenuItem item) {

        Toast.makeText(getApplicationContext(),
                '"'+bk.getTitle()+'"'+ " was removed from bookmarks", Toast.LENGTH_SHORT).show();
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(bk.getTitle()))
                arr.remove(i);

        }
        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();
        bookmark.setVisible(true);
        bookmarked.setVisible(false);

    }
}
