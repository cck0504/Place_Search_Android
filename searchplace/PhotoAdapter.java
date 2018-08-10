package com.example.chillysoup.searchplace;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder>{

    RequestOptions options;
    private Context mContext ;
    private List<PlacePhotoMetadata> photoList;
    private GeoDataClient mGeoDataClient;


    public PhotoAdapter(Context mContext, List lst) {

        this.mContext = mContext;
        this.photoList = lst;
        options = new RequestOptions()
                .centerCrop();

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.photo_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        mGeoDataClient = Places.getGeoDataClient(mContext, null);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoList.get(position));
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                holder.google_photo.setImageBitmap(photoBitmap);
            }
        });

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView google_photo;
        RelativeLayout container_photo;

        public MyViewHolder(View view) {
            super(view);

            container_photo = itemView.findViewById(R.id.container_photo);
            google_photo = view.findViewById(R.id.google_photo);
        }
    }
}
