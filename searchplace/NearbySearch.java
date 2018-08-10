package com.example.chillysoup.searchplace;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chillysoup.searchplace.NearbyModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NearbySearch extends AppCompatActivity {
    private RequestQueue requestQueue;

    private String key;
    private String cat;
    private String dis;
    private Float radius;
    private String loc;
    private String curr_loc;
    private String url;

    private String next_token_n ="";
    private Button prev_n;
    private Button next_n;
    private int page_num = 0;

    private TextView no_results;
    private List<List<NearbyModel>> token_list = new ArrayList<>();
    private List<NearbyModel> lstNearby = new ArrayList<>();
    private RecyclerView myrv;
    private RvAdapter myAdapter;

    ViewGroup progressView;
    protected boolean progress_flag = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showProgressingView();
        setContentView(R.layout.activity_nearby_search);

        setTitle("Search Results");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        no_results = (TextView) findViewById(R.id.no_results);
        prev_n = (Button) findViewById(R.id.prev_n);
        prev_n.setEnabled(false);
        next_n = (Button) findViewById(R.id.next_n);
        myrv = findViewById(R.id.rv);


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            key = extras.getString("input_keyword");
            cat = extras.getString("input_category");
            dis = extras.getString("input_distance");
            loc = extras.getString("input_location");
            curr_loc = extras.getString("input_curr_loc");
        }
        if (dis == null || dis == "" || dis.length() < 1)
            radius = (float) 16090;
        else
            radius = Float.valueOf(dis)*1609;

        if (loc == null || loc == "" || loc.length() < 1){
            url = "http://place-201423.appspot.com/json/?keyword="+key+"&type="+cat
                    +"&radius="+radius+"&location="+curr_loc;
        }
        else{
            url = "http://place-201423.appspot.com/json/?keyword="+key+"&type="+cat
                    +"&radius="+radius+"&loc_enter="+loc;
        }
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    no_results.setVisibility(View.GONE);

                    try {
                        String next_token = response.getString("next_page_token").toString();
                        next_token_n = next_token;
                        next_n.setEnabled(true);

                    } catch (JSONException e){
                        next_n.setEnabled(false);
                        e.printStackTrace();
                    }
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject result = jsonArray.getJSONObject(i);

                            NearbyModel nearbyModel = new NearbyModel(
                                    result.getString("name"),
                                    result.getString("icon"),
                                    result.getString("vicinity"),
                                    result.getString("place_id"),
                                    result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                    result.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                            );

                            lstNearby.add(nearbyModel);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    token_list.add(lstNearby);
                    if (lstNearby.size() != 0){
                        setRvadapter(lstNearby);

                    }
                    else{
                        no_results.setVisibility(View.VISIBLE);
                    }

                    hideProgressingView();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "No results found!", Toast.LENGTH_SHORT).show();
                    hideProgressingView();
                }
            });

        requestQueue.add(jsonObjectRequest);

        prev_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_num--;
                buttonCheck();
                setRvadapter(token_list.get(page_num));


            }
        });

        next_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_num++;
                buttonCheck();
                if (page_num <= token_list.size()-1){
                    setRvadapter(token_list.get(page_num));
                }
                else{
                    saveNextToken(next_token_n);
                }
            }
        });

    }

    private void saveNextToken(String next_tok){

        showProgressingView();

        String url_next = "http://place-201423.appspot.com/json/?nextToken=" + next_tok;
        requestQueue = Volley.newRequestQueue(this); // 'this' is the Context

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_next, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<NearbyModel> lstToken = new ArrayList<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            for (int i=0; i < jsonArray.length(); i++){
                                JSONObject result = jsonArray.getJSONObject(i);

                                NearbyModel nearbyModel = new NearbyModel(
                                        result.getString("name"),
                                        result.getString("icon"),
                                        result.getString("vicinity"),
                                        result.getString("place_id"),
                                        result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                        result.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                                );

                                lstToken.add(nearbyModel);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//
                        token_list.add(lstToken);
                        setRvadapter(token_list.get(page_num));

                        try {
                            String next_token_get = response.getString("next_page_token").toString();
                            next_token_n = next_token_get;
                            next_n.setEnabled(true);

                        } catch (JSONException e){
                            next_n.setEnabled(false);
                            e.printStackTrace();
                        }
                        hideProgressingView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No next page found!", Toast.LENGTH_SHORT).show();
                        hideProgressingView();
                    }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void buttonCheck() {
        if (page_num == 0) {
            prev_n.setEnabled(false);
        } else{
            prev_n.setEnabled(true);
        }
        if (page_num >= token_list.size()-1){
            next_n.setEnabled(false);
        }
        else{
            next_n.setEnabled(true);
        }
    }

    public void setRvadapter (List<NearbyModel> lst) {
        myAdapter = new RvAdapter(this, lst) ;
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);

    }

    public void showProgressingView() {

        if (!progress_flag) {
            progress_flag = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.progressbar_layout, null);
            View v = this.findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    public void hideProgressingView() {
        View v = this.findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        progress_flag = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (token_list.size() != 0){
            myAdapter.notifyDataSetChanged();
        }
    }
}

