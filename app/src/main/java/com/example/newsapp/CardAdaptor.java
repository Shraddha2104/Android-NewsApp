package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bottomnavigation.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class CardAdaptor extends RecyclerView.Adapter<CardAdaptor.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<CardItem> mExampleList;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
    private String imageUrl;
    private String title;
    private String section;
    private String time;
    private BookmarkItem bk;
    private BookmarkItem bk_delete;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position, ImageView bookmarks, ImageView bookmarked);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public CardAdaptor(Context context, ArrayList<CardItem> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }


    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_item, parent, false);
        return new ExampleViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, int position) {
        final CardItem currentItem = mExampleList.get(position);

         imageUrl = currentItem.getImageUrl();
         title = currentItem.getTitle();
         section = currentItem.getSection();
         time= currentItem.getTime();

        holder.mTitle.setText(title);
        holder.mSection.setText(section);
        holder.mTime.setText(time);
        if(check_bookmarked(title,currentItem.getArticle_url())==1){
            holder.bookmarks.setVisibility(View.INVISIBLE);
            holder.bookmarked.setVisibility(View.VISIBLE);
        }
        else{
            holder.bookmarks.setVisibility(View.VISIBLE);
            holder.bookmarked.setVisibility(View.INVISIBLE);
        }
       // Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.mImageView);
        Glide.with(mContext)
                .load(imageUrl)
                .into(holder.mImageView);
        holder.bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bookmarks.setVisibility(View.INVISIBLE);
                holder.bookmarked.setVisibility(View.VISIBLE);
                try {
                    bk=new BookmarkItem(currentItem.getTitle(),currentItem.getSection(),currentItem.getImageUrl(),currentItem.getBookmark_date(),currentItem.getArticle_url());
                    bookmark_item();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(v.getContext(),
                        '"'+currentItem.getTitle()+'"'+ " was added to bookmarks", Toast.LENGTH_LONG).show();
            }
        });
        holder.bookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bookmarked.setVisibility(View.INVISIBLE);
                holder.bookmarks.setVisibility(View.VISIBLE);

                bk_delete=new BookmarkItem(currentItem.getTitle(),currentItem.getSection(),currentItem.getImageUrl(),currentItem.getBookmark_date(),currentItem.getArticle_url());
                remove_bookmark_item();
                Toast.makeText(v.getContext(),
                        '"'+currentItem.getTitle()+'"'+ "was removed from bookmarks", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void bookmark_item() throws JSONException {

        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
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

    }

    public void remove_bookmark_item(){
        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(bk_delete.getTitle()) )
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);

        if(arr!=null) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getTitle().equals(title))
                    return 1;
            }
        }
        return 0;

    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }



    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTitle;
        public TextView mTime;
        public TextView mSection;
        ImageView bookmarks;
        ImageView bookmarked;

        ArrayList<CardItem> arr;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
            mTitle = itemView.findViewById(R.id.title);
            mTime = itemView.findViewById(R.id.time);
            mSection = itemView.findViewById(R.id.section);
            bookmarks=itemView.findViewById(R.id.bookmarks);
            bookmarked=itemView.findViewById(R.id.bookmarked);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mLongListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mLongListener.onItemLongClick(position,bookmarks,bookmarked);
                        }
//                        Toast.makeText(v.getContext(),
//                                '"'+String.valueOf(check_bookmarked(mExampleList.get(position).getTitle()))+'"'+ "was removed from bookmarks", Toast.LENGTH_LONG).show();
                        if(check_bookmarked(mExampleList.get(position).getTitle(),mExampleList.get(position).getTitle())==1){
                            bookmarks.setVisibility(View.INVISIBLE);
                            bookmarked.setVisibility(View.VISIBLE);
                        }
                        else{
                            bookmarks.setVisibility(View.VISIBLE);
                            bookmarked.setVisibility(View.INVISIBLE);
                        }
                    }
                    return false;
                }
            });



        }

    }

}