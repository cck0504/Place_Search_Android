package com.example.chillysoup.searchplace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;

public class Search extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = "Tab1Fragment";

    private Button nearby_search;
    private Button clear_button;
    private TextView key_warn;
    private TextView loc_warn;
    private AutoCompleteTextView location;
    private RadioGroup radio_group;
    private RadioButton radio_curr;
    private RadioButton radio_other;
    private EditText keyword;
    private Spinner category;
    private EditText distance;

    private String curr_loc;
    private FusedLocationProviderClient client;

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private PlaceAutocompleteAdapter mAdapter;
    protected GeoDataClient mGeoDataClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_1, container, false);

        keyword = (EditText) view.findViewById(R.id.keyword);
        category = (Spinner) view.findViewById(R.id.category);
        distance = (EditText) view.findViewById(R.id.distance);
        radio_group = (RadioGroup) view.findViewById(R.id.radioGroup);
        radio_curr = (RadioButton) view.findViewById(R.id.current);
        radio_other = (RadioButton) view.findViewById(R.id.other);
        location = (AutoCompleteTextView) view.findViewById(R.id.location);
        key_warn = (TextView) view.findViewById(R.id.key_warn);
        loc_warn = (TextView) view.findViewById(R.id.loc_warn);
        location.setEnabled(false);

        //TODO: lets get curr_loc
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(getActivity());
//
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return view;
        }
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null){
                    String tmp = location.toString();
                    curr_loc = tmp.split(" ")[1];
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.category_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setSelection(0);

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (radio_curr.getId() == checkedId){
                    loc_warn.setVisibility(View.GONE);
                    location.setEnabled(false);
                    location.setFocusableInTouchMode(false);
                } else{
                    location.setEnabled(true);
                    location.setFocusableInTouchMode(true);
                }
            }
        });

        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, BOUNDS_MOUNTAIN_VIEW, null);
        location.setAdapter(mAdapter);

        nearby_search = (Button) view.findViewById(R.id.submit);
        nearby_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input_keyword = keyword.getText().toString();
                String input_category = category.getSelectedItem().toString();
                String input_distance = distance.getText().toString();
                Boolean input_current = radio_curr.isChecked();
                Boolean input_other = radio_other.isChecked();
                String input_location = location.getText().toString();

                Boolean key_valid = false;
                Boolean loc_valid = false;

                //Validation
                if (input_keyword.trim().length() == 0) {
                    key_warn.setVisibility(View.VISIBLE);
                } else {
                    key_warn.setVisibility(View.GONE);
                    key_valid = true;
                }
                if (input_other && input_location.trim().length() == 0) {
                    loc_warn.setVisibility(View.VISIBLE);
                } else {
                    loc_warn.setVisibility(View.GONE);
                    loc_valid = true;
                }

                if (key_valid && loc_valid) {

                    Intent intent = new Intent(getActivity(), NearbySearch.class);
                    intent.putExtra("input_keyword", input_keyword);
                    intent.putExtra("input_category", input_category);
                    intent.putExtra("input_distance", input_distance);
                    intent.putExtra("input_location", input_location);
                    intent.putExtra("input_curr_loc", curr_loc);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }
            }
        });


        clear_button = (Button) view.findViewById(R.id.clear);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                keyword.setText("");
                category.setSelection(0);
                distance.setText("");
                radio_curr.setChecked(true);
                location.setText("");
                key_warn.setVisibility(View.GONE);
                loc_warn.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_NETWORK_STATE}, 10);
    }



}
