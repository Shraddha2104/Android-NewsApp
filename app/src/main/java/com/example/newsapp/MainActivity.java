package com.example.newsapp;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bottomnavigation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    SearchView searchView;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

       removeShiftMode(bottomNavigationView);
        Toolbar myToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
       // checkLocationPermission();


    }

    @SuppressLint("RestrictedApi")
    public static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BottomNav", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BottomNav", "Unable to change value of shift mode", e);
        }
    }

    public void auto_populate(String keyword){
        final ArrayList ar=new ArrayList<String>();
        String url="https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q="+keyword;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.wtf("The Response ",response.toString());
                try {
                    JSONArray suggestions=response.getJSONArray("suggestionGroups");
                    JSONObject index= (JSONObject) suggestions.get(0);
                    JSONArray search_suggestions=index.getJSONArray("searchSuggestions");
                    for(int i=0;i<Math.min(search_suggestions.length(),5);i++){
                        JSONObject suggest = search_suggestions.getJSONObject(i);
                            String displayText=suggest.getString("displayText");
                            ar.add(displayText);
                    }
                    autoSuggestAdapter.setData(ar);
                    autoSuggestAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", "c3410c5aab854b82ae9349b871d2f654");
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }
    private void makeApiCall(String text) {
        ApiCall.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("trackName"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_toolbar, menu);
        MenuItem search_item=menu.findItem(R.id.searchBar);
        searchView= (SearchView) MenuItemCompat.getActionView(search_item);

        SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName=new ComponentName(this,SearchActivity.class);
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        //edittext.setImeOptions(EditorInfo.IME_ACTION_SEND);
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.parseColor("#000000"));
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);

        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setThreshold(2);

        searchAutoComplete.setAdapter(autoSuggestAdapter);
        autoSuggestAdapter.getFilter().filter(searchAutoComplete.getText(), null);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //searchView.setIconifiedByDefault(true);
       // searchView.setQueryHint("");


        searchAutoComplete.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        searchAutoComplete.setText(autoSuggestAdapter.getObject(position));
                    }
                });
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });
        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchIntent.putExtra("query",query);
                startActivity(searchIntent);
//                SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                ComponentName componentName=new ComponentName(getApplicationContext(),SearchActivity.class);
//                assert searchManager != null;
//                searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText!="") {
                    auto_populate(newText);
                }
                return true;


            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_home:
                Home homeFragment = new Home();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                return true;

            case R.id.navigation_headlines:
                Headlines favouriteFragment = new Headlines();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, favouriteFragment).commit();
                return true;

            case R.id.navigation_trending:
                Trending trendingFragment = new Trending();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, trendingFragment).commit();
                return true;

            case R.id.navigation_bookmark:
                Bookmarks cartFragment = new Bookmarks();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, cartFragment).commit();
                return true;

        }

        return false;
    }



}
