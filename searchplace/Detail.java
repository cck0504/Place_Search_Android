package com.example.chillysoup.searchplace;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Detail extends AppCompatActivity {

    private static final String TAG = "Detail";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private ImageView heart_fill_d;

    private String name;
    private String icon;
    private String address;
    private String place_id;
    private Double lat;
    private Double lng;

    private Gson gson;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    private static final String FAVORITE_STORAGE = "favorite";
    private java.util.Map<String, NearbyModel> fav_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = (Toolbar) findViewById(R.id.t_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        heart_fill_d = (ImageView) findViewById(R.id.fav_heart);

        fav_map = new HashMap<>();
        gson = new GsonBuilder().setPrettyPrinting().create();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPrefs.edit();

        name = getIntent().getExtras().getString("name");
        icon = getIntent().getExtras().getString("icon");
        address = getIntent().getExtras().getString("address");
        place_id = getIntent().getExtras().getString("place_id");
        lat = getIntent().getExtras().getDouble("lat");
        lng = getIntent().getExtras().getDouble("lng");
        final NearbyModel detail_model = new NearbyModel(name, icon, address, place_id, lat, lng);

        toolbar.setTitle(name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String json = mPrefs.getString(FAVORITE_STORAGE, "");
        Type listType = new TypeToken<java.util.Map<String, NearbyModel>>() {
        }.getType();
        fav_map = gson.fromJson(json, listType);


        //TODO: go to on resume
        if (fav_map.containsKey(place_id)){
            heart_fill_d.setImageResource(R.drawable.heart_fill_red);
            heart_fill_d.setTag("fill_red");

        } else{
            heart_fill_d.setImageResource(R.drawable.heart_fill_white);
            heart_fill_d.setTag("fill_heart_d");
        }





        final ImageView twitt_share = (ImageView) findViewById(R.id.twitt_share);
        twitt_share.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String twitt_url = "";
                if (Info.website_url.length() != 0) {
                    twitt_url = "https://twitter.com/intent/tweet?text=Check out " + name + " located at " + address + ". Website: &url=" +
                            Info.website_url + "&hashtags=TravelAndEntertainmentSearch";
                } else{
                    twitt_url = "https://twitter.com/intent/tweet?text=Check out " + name + " located at " + address + ". Website: &url=" +
                            Info.google_site_url + "&hashtags=TravelAndEntertainmentSearch";
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(twitt_url));
                startActivity(intent);
            }
        });

        heart_fill_d.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if (heart_fill_d.getTag().toString().equals("fill_heart_d")){
                    heart_fill_d.setImageResource(R.drawable.heart_fill_red);
                    heart_fill_d.setTag("fill_red");
                    String json = mPrefs.getString(FAVORITE_STORAGE, "");
                    fav_map.put(place_id, detail_model);
                    json = gson.toJson(fav_map);
                    mEditor.putString(FAVORITE_STORAGE, json);
                    mEditor.commit();
                    Toast.makeText(getApplicationContext(), name+"was added to favorites", Toast.LENGTH_SHORT).show();


                }
                else {
                    heart_fill_d.setImageResource(R.drawable.heart_fill_white);
                    heart_fill_d.setTag("fill_heart_d");
                    String json = mPrefs.getString(FAVORITE_STORAGE, "");
                    Type listType = new TypeToken<java.util.Map<String, NearbyModel>>() {
                    }.getType();
                    java.util.Map<String, NearbyModel> fav_map = gson.fromJson(json, listType);

                    Toast.makeText(getApplicationContext(), name+"was removed from favorites", Toast.LENGTH_SHORT).show();

                    fav_map.remove(place_id);
                    json = gson.toJson(fav_map);
                    mEditor.putString(FAVORITE_STORAGE, json);
                    mEditor.commit();
                }
            }
        });



        Log.d(TAG, "onCreate: Detail Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container_detail);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_detail);
        tabLayout.setupWithViewPager(mViewPager);

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("INFO");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_outline, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("PHOTOS");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photos, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("MAP");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maps, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("REVIEWS");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.review, 0, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Info(), "INFO");
        adapter.addFragment(new Photos(), "PHOTOS");
        adapter.addFragment(new Map(), "MAP");
        adapter.addFragment(new Reviews(), "REVIEWS");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onResume(){
        super.onResume();


    }


}
