package com.example.chillysoup.searchplace;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Favorite extends Fragment {
    private static final String TAG = "Tab2Fragment";
    private static final String FAVORITE_STORAGE = "favorite";

    private Map<String, NearbyModel> fav_map;
    private ArrayList<NearbyModel> fav_list;

    private TextView no_fav;
    private RecyclerView fav_rv;
    private FavoriteAdapter myAdapter;

    RequestOptions options ;
    private Gson gson;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_2, container, false);

        //TODO: SETUP FAVORITE
        no_fav = (TextView) view.findViewById(R.id.no_fav);
        fav_rv = view.findViewById(R.id.fav_rv);

        return view;
    }



    @Override
    public void onResume(){
        super.onResume();

        fav_list = new ArrayList<>();
        fav_map = new HashMap<>();
        gson = new GsonBuilder().setPrettyPrinting().create();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPrefs.edit();
        String json = mPrefs.getString(FAVORITE_STORAGE, "");
        Type type = new TypeToken<Map<String, NearbyModel>>() {
        }.getType();
        Map<String, NearbyModel> favResults = gson.fromJson(json, type);

        if (favResults.size() != 0) {
            no_fav.setVisibility(View.GONE);

            //convert map to list for adapter
            for (NearbyModel nm: favResults.values()){
                fav_list.add(nm);
            }
        } else {
            //no fav_data
            no_fav.setVisibility(View.VISIBLE);
        }

        myAdapter = new FavoriteAdapter(getActivity(), fav_list, Favorite.this) ;
        fav_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        fav_rv.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();



    }


}
