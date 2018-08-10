package com.example.chillysoup.searchplace;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private Favorite mfragment;
    RequestOptions options ;
    private Context mContext ;
    private List<NearbyModel> mData ;
    private Gson gson;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    private static final String FAVORITE_STORAGE = "favorite";
    //    private List<>
    private ArrayList<NearbyModel> fav_list;
    private Map<String, NearbyModel> fav_map;

    public FavoriteAdapter(Context mContext, List lst, Favorite mfragment) {

        this.mfragment = mfragment;
        this.mContext = mContext;
        this.mData = lst;
        options = new RequestOptions()
                .centerCrop();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.fav_item, parent, false);

        fav_map = new HashMap<>();
        gson = new GsonBuilder().setPrettyPrinting().create();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mPrefs.edit();
//        String json = mPrefs.getString(FAVORITE_STORAGE, "");

        // click listener here
        final FavoriteAdapter.MyViewHolder viewHolder = new FavoriteAdapter.MyViewHolder(view);

        viewHolder.view_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent (mContext, Detail.class);
                intent.putExtra("name", mData.get(viewHolder.getAdapterPosition()).getName());
                intent.putExtra("address", mData.get(viewHolder.getAdapterPosition()).getAddress());
                intent.putExtra("lat", mData.get(viewHolder.getAdapterPosition()).getLat());
                intent.putExtra("lng", mData.get(viewHolder.getAdapterPosition()).getLng());
                intent.putExtra("place_id", mData.get(viewHolder.getAdapterPosition()).getPlace_id());

                mContext.startActivity(intent);
            }
        });

        final ImageView heart_fill_n = view.findViewById(R.id.heart_fill_f);
        heart_fill_n.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String name =  mData.get(viewHolder.getAdapterPosition()).getName();
                String address = mData.get(viewHolder.getAdapterPosition()).getAddress();
                Double lat = mData.get(viewHolder.getAdapterPosition()).getLat();
                Double lng = mData.get(viewHolder.getAdapterPosition()).getLng();
                String place_id = mData.get(viewHolder.getAdapterPosition()).getPlace_id();
                String icon = mData.get(viewHolder.getAdapterPosition()).getIcon();
                NearbyModel temp_model = new NearbyModel(name, icon, address, place_id, lat, lng);

                heart_fill_n.setTag("fill_red_fav");
                String json = mPrefs.getString(FAVORITE_STORAGE, "");
                Type listType = new TypeToken<Map<String, NearbyModel>>() {
                }.getType();
                fav_map = gson.fromJson(json, listType);

                fav_map.remove(place_id);
                Toast.makeText(mContext, name+"was removed from favorites", Toast.LENGTH_SHORT).show();

                json = gson.toJson(fav_map);
                mEditor.putString(FAVORITE_STORAGE, json);
                mEditor.commit();
                notifyDataSetChanged();
                mfragment.onResume();


            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvname.setText(mData.get(position).getName());
        holder.tvaddr.setText(mData.get(position).getAddress());
        Glide.with(mContext).load(mData.get(position).getIcon()).apply(options).into(holder.Thumbnail);


        holder.heart_fill_n.setImageResource(R.drawable.heart_fill_red);

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

            view_container = itemView.findViewById(R.id.container_fav);
            tvname = itemView.findViewById(R.id.fav_name);
            tvaddr = itemView.findViewById(R.id.fav_addr);
            Thumbnail = itemView.findViewById(R.id.fav_icon);
            heart_fill_n = itemView.findViewById(R.id.heart_fill_f);
        }
    }
}
