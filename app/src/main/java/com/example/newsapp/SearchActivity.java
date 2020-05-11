package com.example.newsapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity implements CardAdaptor.OnItemClickListener, CardAdaptor.OnItemLongClickListener{
    private RecyclerView mRecyclerView;
    View view;
    public static  String article_url = "article_url";
    private ArrayList<CardItem> mExampleList;
    private CardAdaptor mCardAdaptor;
    private String section_name;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView progress_t;
    BookmarkItem bk;
    BookmarkItem bk_delete;
    String query;
    Toolbar toolbar;
    TextView no_search1;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("query string",query);

        }

            query = intent.getStringExtra("query");
            Log.i("query string",query);



        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        progress_t=findViewById(R.id.progress_text);
        no_search1=findViewById(R.id.no_search);
        spinner = findViewById(R.id.progressBar1);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        spinner.setVisibility(View.VISIBLE);
        progress_t.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mExampleList = new ArrayList<>();
        no_search1.setVisibility(View.GONE);
        search_results();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                spinner.setVisibility(View.GONE);
                progress_t.setVisibility(View.GONE);
                search_results();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

    }
    @Override
    public void onResume()
    {
        super.onResume(); // paste toast here

        search_results();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void search_results() {


        toolbar.setTitle("Search Results for "+query);
        final ArrayList<CardItem>  mExampleList1 = new ArrayList<>();
        mExampleList1.clear();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://androidbackend-276418.wl.r.appspot.com/gsearch?q="+query;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            //JSONObject response = data.getJSONObject("response");
                            JSONObject json = data.getJSONObject("response");
                            JSONArray results=json.getJSONArray("results");
                            if(results.length()==0)
                            {
                                no_search1.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.GONE);
                                progress_t.setVisibility(View.GONE);
                                return;
                            }

                            for (int i = 0; i < results.length(); i++) {

                                JSONObject index = results.getJSONObject(i);
                                JSONObject blocks=index.getJSONObject("blocks");
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

                                String title=index.getString("webTitle");
                                String time= (String) index.get("webPublicationDate");
                                String date2=formatDate(time);
                                time= format_time(time);

                                String section=(String) index.get("sectionName");
                                String webUrl=(String) index.get("webUrl");
                                article_url =index.getString("id");
                                mExampleList1.add(new CardItem(imageUrl, title, time, section, article_url,webUrl,date2));
                            }
                            if(mCardAdaptor==null)
                            {
                                mExampleList.clear();
                                mExampleList.addAll(mExampleList1);
                                mCardAdaptor = new CardAdaptor(Objects.requireNonNull(getApplicationContext()), mExampleList);
                                mRecyclerView.setAdapter(mCardAdaptor);
                                mCardAdaptor.setOnItemClickListener(SearchActivity.this);
                                mCardAdaptor.setOnItemLongClickListener(SearchActivity.this);
                            }
                            else{

                                mExampleList.clear();
                                mExampleList.addAll(mExampleList1);
                                mCardAdaptor.notifyDataSetChanged();

                            }
                            spinner.setVisibility(View.GONE);
                            progress_t.setVisibility(View.GONE);

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    public void onItemClick(int position) {

        Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        CardItem clickedItem = mExampleList.get(position);
        detailIntent.putExtra("article_url", clickedItem.getArticle_url());
        startActivity(detailIntent);
    }
    public void onItemLongClick(int position, final ImageView card_bookmarks, final ImageView card_bookmarked) {
        Log.i("in item long click","longggg");
        final CardItem clickedItem = mExampleList.get(position);
//        final Dialog dialog = new Dialog(Objects.requireNonNull(getApplicationContext()));
        final Dialog dialog = new Dialog(SearchActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog);

        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(clickedItem.getTitle());
        ImageView img = dialog.findViewById(R.id.imageDialog);
        Picasso.with(getApplicationContext()).load(clickedItem.getImageUrl()).fit().centerInside().into(img);

        ImageView twitter = dialog.findViewById(R.id.twitter);


        final ImageView bookmarks = dialog.findViewById(R.id.bookmark);
        final ImageView bookmarked=dialog.findViewById(R.id.bookmarked);
        if(check_bookmarked(clickedItem.getTitle(),clickedItem.getArticle_url())==1){
            bookmarks.setVisibility(View.INVISIBLE);
            bookmarked.setVisibility(View.VISIBLE);
            card_bookmarks.setVisibility(View.INVISIBLE);
            card_bookmarked.setVisibility(View.VISIBLE);
        }
        else{
            bookmarks.setVisibility(View.VISIBLE);
            bookmarked.setVisibility(View.INVISIBLE);
            card_bookmarks.setVisibility(View.VISIBLE);
            card_bookmarked.setVisibility(View.INVISIBLE);

        }
        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarks.setVisibility(View.INVISIBLE);
                bookmarked.setVisibility(View.VISIBLE);
                card_bookmarks.setVisibility(View.INVISIBLE);
                card_bookmarked.setVisibility(View.VISIBLE);
                bk=new BookmarkItem(clickedItem.getTitle(),clickedItem.getSection(),clickedItem.getImageUrl(),clickedItem.getBookmark_date(),clickedItem.getArticle_url());
                try {
                    bookmark_item();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(v.getContext(),
                        '"'+clickedItem.getTitle()+'"'+ " was added to bookmarks", Toast.LENGTH_SHORT).show();
            }
        });
        bookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmarks.setVisibility(View.VISIBLE);
                bookmarked.setVisibility(View.INVISIBLE);
                card_bookmarks.setVisibility(View.VISIBLE);
                card_bookmarked.setVisibility(View.INVISIBLE);

                bk_delete=new BookmarkItem(clickedItem.getTitle(),clickedItem.getSection(),clickedItem.getImageUrl(),clickedItem.getBookmark_date(),clickedItem.getArticle_url());
                remove_bookmark_item();

                Toast.makeText(v.getContext(),
                        '"'+clickedItem.getTitle()+'"'+ " was removed to bookmarks", Toast.LENGTH_SHORT).show();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                        urlEncode("Check out this Link:"),
                        urlEncode(clickedItem.getWebTitle()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                startActivity(intent);
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }
    String format_time(String dataDate) {
        String convTime;
        String suffix = "ago";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone gmtTime = TimeZone.getTimeZone("PST");
        dateFormat.setTimeZone(gmtTime);
        Date pasTime = null;
        Date nowTime = new Date();

        try { pasTime = dateFormat.parse(dataDate); }
        catch (ParseException e) { e.printStackTrace(); }

        long dateDiff = nowTime.getTime() - pasTime.getTime();
        long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
        long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
        long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
        long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

        if (second < 60) { convTime = second+"s "+suffix; }
        else if (minute < 60) { convTime = minute+"m "+suffix; }
        else if (hour < 24) { convTime = hour+"h "+suffix; }
        else { convTime = day+"d "+suffix; }

        return convTime;
    }
    private String formatDate(String str_date) throws ParseException {

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
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
    public void bookmark_item() throws JSONException {

        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getApplicationContext()));
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

    }

    public void remove_bookmark_item(){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getApplicationContext()));
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(bk_delete.getTitle()) && arr.get(i).getDetailed_url().equals(bk_delete.getDetailed_url()))
                arr.remove(i);

        }
        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();

    }
    public int check_bookmarked(String title,String url){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getApplicationContext()));
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);

        if(arr!=null) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getTitle().equals(title) && arr.get(i).getDetailed_url().equals(url)) {
                    return 1;
                }
            }
        }
        return 0;
    }

}
