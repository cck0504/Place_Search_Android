package com.example.chillysoup.searchplace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder> {

    RequestOptions options ;
    private Context mContext ;
    private List<NearbyModel> mData ;
    private Gson gson;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    private static final String FAVORITE_STORAGE = "favorite";
    //    private List<>
    private Map<String, NearbyModel> fav_map;

    private String name;
    private String icon;
    private String address;
    private Double lat;
    private Double lng;
    private String place_id;

    private NearbyModel temp_model;

    public RvAdapter(Context mContext, List lst) {

        this.mContext = mContext;
        this.mData = lst;
        options = new RequestOptions()
                .centerCrop();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.nearby_row_item, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        fav_map = new HashMap<>();
        gson = new GsonBuilder().setPrettyPrinting().create();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mPrefs.edit();
//        String json = mPrefs.getString(FAVORITE_STORAGE, "");



        // click listener here
        viewHolder.view_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                name =  mData.get(viewHolder.getAdapterPosition()).getName();
                icon = mData.get(viewHolder.getAdapterPosition()).getIcon();
                address = mData.get(viewHolder.getAdapterPosition()).getAddress();
                lat = mData.get(viewHolder.getAdapterPosition()).getLat();
                lng = mData.get(viewHolder.getAdapterPosition()).getLng();
                place_id = mData.get(viewHolder.getAdapterPosition()).getPlace_id();

                Intent intent = new Intent (mContext, Detail.class);
                intent.putExtra("name", name);
                intent.putExtra("icon", icon);
                intent.putExtra("address", address);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("place_id", place_id);

                mContext.startActivity(intent);
            }
        });

        final ImageView heart_fill_n = view.findViewById(R.id.heart_fill_n);
        heart_fill_n.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                name =  mData.get(viewHolder.getAdapterPosition()).getName();
                icon = mData.get(viewHolder.getAdapterPosition()).getIcon();
                address = mData.get(viewHolder.getAdapterPosition()).getAddress();
                lat = mData.get(viewHolder.getAdapterPosition()).getLat();
                lng = mData.get(viewHolder.getAdapterPosition()).getLng();
                place_id = mData.get(viewHolder.getAdapterPosition()).getPlace_id();
                temp_model = new NearbyModel(name, icon, address, place_id, lat, lng);

                if (heart_fill_n.getTag().toString().equals("outline_black")){
                    heart_fill_n.setImageResource(R.drawable.heart_fill_red);
                    heart_fill_n.setTag("fill_red");
                    String json = mPrefs.getString(FAVORITE_STORAGE, "");
                    fav_map.put(place_id, temp_model);
                    json = gson.toJson(fav_map);
                    mEditor.putString(FAVORITE_STORAGE, json);
                    mEditor.commit();
                    Toast.makeText(mContext, name+"was added to favorites", Toast.LENGTH_SHORT).show();


                }
                else{
                    heart_fill_n.setImageResource(R.drawable.heart_outline_black);
                    heart_fill_n.setTag("outline_black");
                    String json = mPrefs.getString(FAVORITE_STORAGE, "");
                    Type listType = new TypeToken<Map<String, NearbyModel>>() {
                    }.getType();
                    fav_map = gson.fromJson(json, listType);

                    Toast.makeText(mContext, name+"was removed fromfavorites", Toast.LENGTH_SHORT).show();

                    fav_map.remove(place_id);
                    json = gson.toJson(fav_map);
                    mEditor.putString(FAVORITE_STORAGE, json);
                    mEditor.commit();

                }
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String json = mPrefs.getString(FAVORITE_STORAGE, "");
        Type listType = new TypeToken<Map<String, NearbyModel>>() {
        }.getType();
        fav_map = gson.fromJson(json, listType);
        place_id = mData.get(position).getPlace_id();

        holder.tvname.setText(mData.get(position).getName());
        holder.tvaddr.setText(mData.get(position).getAddress());
        Glide.with(mContext).load(mData.get(position).getIcon()).apply(options).into(holder.Thumbnail);

        if (fav_map.containsKey(place_id)){
            holder.heart_fill_n.setImageResource(R.drawable.heart_fill_red);
            holder.heart_fill_n.setTag("fill_red");

        } else{
            holder.heart_fill_n.setImageResource(R.drawable.heart_outline_black);
            holder.heart_fill_n.setTag("outline_black");
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvname;
        TextView tvaddr;
        ImageView Thumbnail;
        RelativeLayout view_container;
        ImageView heart_fill_n;


        public MyViewHolder(View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.container_nearby);
            tvname = itemView.findViewById(R.id.rowname);
            tvaddr = itemView.findViewById(R.id.addr);
            Thumbnail = itemView.findViewById(R.id.thumbnail);
            heart_fill_n = itemView.findViewById(R.id.heart_fill_n);
        }
    }

}