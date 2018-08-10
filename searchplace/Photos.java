package com.example.chillysoup.searchplace;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class Photos extends Fragment {

    private static final String TAG = "PHOTOS_TAB";

    private String place_id;
    private GeoDataClient mGeoDataClient;
    private List<PlacePhotoMetadata> photosDataList;
    private TextView no_photo;
    private RecyclerView myrv ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        place_id = getActivity().getIntent().getExtras().getString("place_id");
        myrv = (RecyclerView) view.findViewById(R.id.photo_rv);
        no_photo = (TextView) view.findViewById(R.id.no_photo);

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(place_id);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                photosDataList = new ArrayList<>();
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                int temp_i = 0;
                for (PlacePhotoMetadata photoMetadata: photoMetadataBuffer){
                    photoMetadata = photoMetadataBuffer.get(temp_i).freeze();

                    photosDataList.add(photoMetadata);
                    temp_i++;

                }

                photoMetadataBuffer.release();


                if (photosDataList.size() != 0){
                    setPhotoadapter();
                    no_photo.setVisibility(View.GONE);
                }
                else{
                    no_photo.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    public void setPhotoadapter () {

        PhotoAdapter myAdapter = new PhotoAdapter(getActivity(), photosDataList) ;
        myrv.setLayoutManager(new LinearLayoutManager(getActivity()));
        myrv.setAdapter(myAdapter);

    }

}
