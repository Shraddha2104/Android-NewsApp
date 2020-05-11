package com.example.newsapp;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomnavigation.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Bookmarks extends Fragment implements BookmarkAdaptor.OnItemClickListener, BookmarkAdaptor.OnItemLongClickListener, BookmarkAdaptor.OnBookmarkClickListener{


    public Bookmarks() {
        // Required empty public constructor
    }
    RecyclerView recyclerView;
    View view;
    Dialog dialog;
    BookmarkItem bk;
    BookmarkItem bk_delete;
    TextView no_bookmarks;
    RecyclerView.LayoutManager layoutManager;
    BookmarkAdaptor mExampleAdapter;
    ArrayList<BookmarkItem> mExampleList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_bookmarks, container, false);
        mExampleList = new ArrayList<>();
        display_bookmarks();
        return view;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        display_bookmarks();

    }
    public void display_bookmarks(){
        recyclerView=view.findViewById(R.id.recycler_view);
        no_bookmarks=view.findViewById(R.id.no_bookmark);

        layoutManager=new GridLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        final ArrayList<BookmarkItem>  mExampleList1 = new ArrayList<>();
        mExampleList1.clear();
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        //sharedPreferences.edit().clear().commit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        Log.i("bb", String.valueOf(arr));
        if(arr==null || arr.size()==0 ){

            no_bookmarks.setVisibility(View.VISIBLE);
            //recyclerView.setVisibility(View.INVISIBLE);

        }
        else {
            no_bookmarks.setVisibility(View.GONE);
            for (int i = 0; i < arr.size(); i++) {
                Log.i("bookmarking item", arr.get(i).getTitle());
                mExampleList1.add(new BookmarkItem(arr.get(i).getTitle(), arr.get(i).getSection(), arr.get(i).getImage(), arr.get(i).getDate(), arr.get(i).getDetailed_url()));
            }
        }



            if(mExampleAdapter==null) {
                Log.i("adapter empty","empty");
                mExampleList.clear();
                mExampleList.addAll(mExampleList1);
                mExampleAdapter = new BookmarkAdaptor(getActivity().getApplicationContext(), mExampleList,no_bookmarks);

                mExampleAdapter.setOnItemClickListener(Bookmarks.this);
                mExampleAdapter.setOnItemLongClickListener(Bookmarks.this);
                mExampleAdapter.setOnBookMarkClickListener(Bookmarks.this);
                recyclerView.setAdapter(mExampleAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()),
                        DividerItemDecoration.VERTICAL));
               // recyclerView.setVisibility(View.VISIBLE);
            //    no_bookmarks.setVisibility(View.INVISIBLE);
            }
            else{
                Log.i("adapter empty","not null");
                mExampleList.clear();
                mExampleList.addAll(mExampleList1);
                mExampleAdapter.notifyDataSetChanged();

            }


    }

    public void remove_bookmark_item(){

        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()).getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(bk_delete.getTitle()) )
                arr.remove(i);

        }
        if(arr.size()==0)no_bookmarks.setVisibility(View.VISIBLE);
        else no_bookmarks.setVisibility(View.GONE);

        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();
        dialog.dismiss();

    }
    public void removeAt(int position) {
        mExampleList.remove(position);
        Log.i("mxample list", String.valueOf(mExampleList.size()));
        mExampleAdapter.notifyDataSetChanged();
        mExampleAdapter.notifyItemRemoved(position);
        mExampleAdapter.notifyItemRangeChanged(position, mExampleList.size());

    }

    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        BookmarkItem clickedItem = mExampleList.get(position);
        detailIntent.putExtra("article_url", clickedItem.getDetailed_url());
        startActivity(detailIntent);
    }
    public void onBookmarkClick(int position) {
        removeAt(position);
        if(mExampleList.isEmpty())
            no_bookmarks.setVisibility(View.VISIBLE);
        else no_bookmarks.setVisibility(View.GONE);
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
    public void onItemLongClick(final int position) {
        final BookmarkItem clickedItem = mExampleList.get(position);

        dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog);

        TextView text = dialog.findViewById(R.id.textDialog);
        text.setText(clickedItem.getTitle());
        ImageView img = dialog.findViewById(R.id.imageDialog);
        Picasso.with(getContext()).load(clickedItem.getImage()).fit().centerInside().into(img);

        ImageView twitter = dialog.findViewById(R.id.twitter);
        final ImageView bookmarks = dialog.findViewById(R.id.bookmark);
        bookmarks.setVisibility(View.GONE);

        final ImageView bookmarked=dialog.findViewById(R.id.bookmarked);
        bookmarked.setVisibility(View.VISIBLE);
        bookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("in dialog remove","rem");
                bk_delete=new BookmarkItem(clickedItem.getTitle(),clickedItem.getSection(),clickedItem.getImage(),clickedItem.getDate(),clickedItem.getDetailed_url());

                remove_bookmark_item();
                Toast.makeText(v.getContext(),
                        '"'+clickedItem.getTitle()+'"'+ "was removed from bookmarks", Toast.LENGTH_SHORT).show();

                removeAt(position);



            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                        urlEncode("Check out this Link:"),
                        urlEncode(clickedItem.getDetailed_url()));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                startActivity(intent);
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }


}
