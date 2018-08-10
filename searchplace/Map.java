package com.example.chillysoup.searchplace;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Map extends Fragment {
    MapView mMapView;

    private GoogleMap googleMap;
    private String destination_name;
    private LatLng origin = new LatLng(37.7849569, -122.4068855);
    private Double des_lat = 37.7814432;
    private Double des_lng = -122.4460177;
    private LatLng destination = new LatLng(37.7814432, -122.4460177);
    private Spinner travel_mode;
    private AutoCompleteTextView from_auto;

    private static final LatLngBounds BOUNDS_ = new LatLngBounds(
            new LatLng(35.398160, -120.180831), new LatLng(35.430610, -119.972090));
    private PlaceAutocompleteAdapter map_Adapter;
    private GeoDataClient map_GeoDataClient;

    private RequestQueue requestQueue;
    private List<Polyline> mPolylines;

    private static final String TAG = "Map_TAB";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map_GeoDataClient = Places.getGeoDataClient(getActivity(), null);
        map_Adapter = new PlaceAutocompleteAdapter(getActivity(), map_GeoDataClient, BOUNDS_, null);

        destination_name = getActivity().getIntent().getExtras().getString("name");
        des_lat = getActivity().getIntent().getExtras().getDouble("lat");
        des_lng = getActivity().getIntent().getExtras().getDouble("lng");
        destination = new LatLng(des_lat,des_lng);
        mPolylines = new ArrayList<>();

        from_auto = (AutoCompleteTextView) view.findViewById(R.id.from_auto);
        travel_mode = (Spinner) view.findViewById(R.id.travel_mode);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                //TODO: change coordinate with input
                googleMap.addMarker(new MarkerOptions()
                        .position(destination)
                        .title(destination_name)).showInfoWindow();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(destination).zoom(12).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        from_auto.setAdapter(map_Adapter);
        from_auto.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendRequest(travel_mode.getSelectedItem().toString(), from_auto.getText().toString());
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.travel_mode_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travel_mode.setAdapter(adapter);
        travel_mode.setSelection(0);
        travel_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (from_auto.getText().length() != 0){
                    sendRequest(travel_mode.getSelectedItem().toString(), from_auto.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;

    }

    private void sendRequest(String mode, final String mOrigin) {
        for (Polyline line : mPolylines){
            line.remove();
        }
        mPolylines.clear();

        requestQueue = Volley.newRequestQueue(getActivity());
        String url = "http://place-201423.appspot.com/json/?origin=" + mOrigin
                + "&destination=" + des_lat + "," + des_lng + "&mode=" + mode.toLowerCase();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                            origin = new LatLng(jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lat"),
                                    jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lng"));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(origin)
                                    .title(mOrigin)).showInfoWindow();

                            //TODO: find middle point of two markers
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(destination).zoom(12).build();
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            String[] directionslist = getPaths(jsonArray.getJSONObject(0).getJSONArray("steps"));
                            display_direction(directionslist);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "No direction found!", Toast.LENGTH_SHORT).show();
                    }
                });

//        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    private void display_direction(String[] directionslist) {

        int count = directionslist.length;
        for (int i = 0; i < count; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionslist[i]));

            mPolylines.add(googleMap.addPolyline(options));

        }
    }

    private String[] getPaths(JSONArray googleStepsJson){
        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for (int i = 0; i < count; i++){
            try {
                JSONObject polyline = googleStepsJson.getJSONObject(i);
                try{
                    polylines[i] = polyline.getJSONObject("polyline").getString("points");
                } catch (JSONException e){
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
