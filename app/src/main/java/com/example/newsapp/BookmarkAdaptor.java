package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomnavigation.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookmarkAdaptor extends RecyclerView.Adapter<BookmarkAdaptor.MyViewHolder> {
    private ArrayList<BookmarkItem> mExampleList;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
    private Context mContext;
    private String imageUrl;
    private String title;
    private String section;
    OnBookmarkClickListener onBookMarkListener;
    private String time;
    BookmarkItem bk_delete;
    TextView no_bk;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new MyViewHolder(v);


    }
    public BookmarkAdaptor(Context context,ArrayList<BookmarkItem> exampleList, TextView no_bookmarks){

        mContext = context;
        mExampleList = exampleList;
        no_bk=no_bookmarks;

    }

    public interface OnItemClickListener {
        void onItemClick(int position);

    }
    public interface OnBookmarkClickListener {
        void onBookmarkClick(int position);

    }
    public void setOnBookMarkClickListener(OnBookmarkClickListener bookmarker){
        onBookMarkListener=bookmarker;
//        BookmarkItem currentItem;
//        bk_delete=new BookmarkItem(currentItem.getTitle(),currentItem.getSection(),currentItem.getImage(),currentItem.getDate(),currentItem.getDetailed_url());
//                removeAt(position);
//                remove_bookmark_item(currentItem.getTitle());
//
////                if (mExampleList.size() == 0) {
////
////                    holder.bookmarked.setVisibility(View.VISIBLE);
//////                    recyclerView.setVisibility(View.INVISIBLE);
////                }
//                Toast.makeText(v.getContext(),
//                        '"'+currentItem.getTitle()+'"'+ "was removed from bookmarks", Toast.LENGTH_SHORT).show();
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void onBindViewHolder(final MyViewHolder holder,final  int position) {

        final BookmarkItem currentItem = mExampleList.get(position);
        String mSection="";
        imageUrl = currentItem.getImage();
        title = currentItem.getTitle();
        section = currentItem.getSection();
        if(section.length()>=11)
            mSection=section.substring(0,5)+"...";
        else
            mSection=section;
        time= currentItem.getDate();

        holder.mTitle.setText(title);
        holder.mSection.setText(mSection);
        holder.mTime.setText(time);
        Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.mImageView);
        holder.bookmarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bk_delete=new BookmarkItem(currentItem.getTitle(),currentItem.getSection(),currentItem.getImage(),currentItem.getDate(),currentItem.getDetailed_url());
                removeAt(position);
                remove_bookmark_item(currentItem.getTitle());

//                if (mExampleList.size() == 0) {
//
//                    holder.bookmarked.setVisibility(View.VISIBLE);
////                    recyclerView.setVisibility(View.INVISIBLE);
//                }
                Toast.makeText(v.getContext(),
                        '"'+currentItem.getTitle()+'"'+ "was removed from bookmarks", Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void removeAt(int position) {
        mExampleList.remove(position);
        notifyDataSetChanged();
        if(mExampleList.size()==0)
            no_bk.setVisibility(View.VISIBLE);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mExampleList.size());

    }

    @Override
    public int getItemCount() {

        return mExampleList.size();
    }

    public void remove_bookmark_item(String Title){

        ArrayList<BookmarkItem> arr;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("bookmarks", null);
        Type type = new TypeToken<ArrayList<BookmarkItem>>() {}.getType();
        arr = gson.fromJson(json, type);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for(int i=0;i<arr.size();i++){
            if(arr.get(i).getTitle().equals(Title)) {
                arr.remove(i);
            }
        }




        for (int i = 0; i < arr.size(); i++) {
            Log.i("bookmarked item", arr.get(i).getTitle());
        }
        String bk_item = gson.toJson(arr);
        editor.putString("bookmarks", bk_item);
        editor.apply();
        if(arr.size()==0){mExampleList.clear();notifyDataSetChanged();
        }


    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTitle;
        public TextView mTime;
        public TextView mSection;
        ImageView bookmarked;
        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
            mTitle = itemView.findViewById(R.id.title);
            mTime = itemView.findViewById(R.id.time);
            mSection = itemView.findViewById(R.id.section);
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
            bookmarked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBookMarkListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onBookMarkListener.onBookmarkClick(position);


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
                            mLongListener.onItemLongClick(position);
                        }
                    }
                    return false;
                }
            });

        }
    }
}