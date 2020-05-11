package com.example.newsapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bottomnavigation.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressLint("ValidFragment")
public class Trending extends Fragment {
    private RecyclerView mRecyclerView;
    View view;
    public static  String article_url = "article_url";


    private RequestQueue mRequestQueue;
    EditText text;
    private String section_name;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView progress_t;
    private ProgressBar spinner;
    LineChart chart;
    Trending(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_trending, container, false);

         final EditText edittext = view.findViewById(R.id.query);
         //edittext.setImeOptions(EditorInfo.IME_ACTION_SEND);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String term = edittext.getText().toString();

                    display_chart(term);
                    return true;
                }
                return false;
            }
        });
        display_chart("Coronavirus");

        return view;
    }
    public void display_chart(final String term){

        String url="https://h9-backend.wl.r.appspot.com/trends";
        try {

            Map<String,String> jsonBody = new HashMap<>();
            jsonBody.put("search_key", term);

            JSONObject jsonBody1 = new JSONObject(jsonBody);
            JsonObjectRequest response1 = new JsonObjectRequest(Request.Method.POST, url, jsonBody1, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        List<Entry> entries = new ArrayList<>();
                        JSONObject obj=response.getJSONObject("default");
                        JSONArray results=obj.getJSONArray("timelineData");
                        for(int i=0;i<results.length();i++){
                            JSONObject index = results.getJSONObject(i);
                            String value=index.getString("value");
                            value=value.substring(1);
                            value=value.substring(0,value.length()-1);
                            int val= Integer.parseInt(value);
                            entries.add(new Entry(i, val));


                        }
                        String label="Trending Chart for "+term;
                        LineDataSet dataset = new LineDataSet(entries, label);
                        dataset.setColor(Color.parseColor("#BB86FC"));
                        List<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(dataset);

                        LineData data = new LineData(dataSets);
                        chart = view.findViewById(R.id.chart);
                        Legend legend = chart.getLegend();
                        legend.setTextSize(17f);
                        chart.setData(data);
                        dataset.setCircleRadius(3f);
                        dataset.setDrawIcons(false);
                        dataset.setLineWidth(1f);
                        dataset.setFormSize(14f);
                        dataset.setValueTextColor(Color.parseColor("#6f30bc"));
                        chart.getXAxis().setDrawGridLines(false);
                        chart.getAxisLeft().setDrawGridLines(false);
                        chart.getAxisRight().setDrawGridLines(false);

                        YAxis leftAxis = chart.getAxisLeft();
                        leftAxis.setEnabled(true);
                        dataset.setDrawHighlightIndicators(false);

                        leftAxis.setDrawAxisLine(false);
                        dataset.setCircleColor(Color.parseColor("#6f30bc"));
                        dataset.setFillColor(Color.parseColor("#6f30bc"));
                        dataset.setDrawCircleHole(false);
                       // dataSet1.setCircleColorHole(Color.BLUE);
                        data.notifyDataChanged();
                        chart.notifyDataSetChanged();

                        chart.invalidate();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("errr", String.valueOf(error));


                }
            }) ;
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()).getApplicationContext());
            queue.add(response1);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}