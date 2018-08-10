package com.example.chillysoup.searchplace;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Info extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "REVIEWS_TAB";
    private String place_id;
    private RequestQueue requestQueue;

    private TextView address_input;
    private TextView phone_input;
    private TextView price_input;
    private RatingBar rating_input;
    private TextView google_input;
    private TextView website_input;
    private TableRow address_row;
    private TableRow phone_row;
    private TableRow price_row;
    private TableRow rating_row;
    private TableRow google_row;
    private TableRow website_row;
    private TableLayout info_table;
    private TextView no_detail;

    static public List<GoogleReviewModel> reviewList = new ArrayList<>();
    static public YelpData yelpData;
    static public String google_site_url;
    static public String website_url;
    static public String name_tweet;
    static public String addr_tweet;

    ViewGroup progressView;
    protected boolean progress_flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        showProgressingView();

        place_id = getActivity().getIntent().getExtras().getString("place_id");

        address_input = (TextView) view.findViewById(R.id.address_input);
        phone_input = (TextView) view.findViewById(R.id.phone_input);
        price_input = (TextView) view.findViewById(R.id.price_input);
        rating_input = (RatingBar) view.findViewById(R.id.rating_input);
        google_input = (TextView) view.findViewById(R.id.google_input);
        website_input = (TextView) view.findViewById(R.id.website_input);
        address_row = (TableRow) view.findViewById(R.id.address_row);
        phone_row = (TableRow) view.findViewById(R.id.phone_row);
        price_row = (TableRow) view.findViewById(R.id.price_row);
        rating_row = (TableRow) view.findViewById(R.id.rating_row);
        google_row = (TableRow) view.findViewById(R.id.google_row);
        website_row = (TableRow) view.findViewById(R.id.website_row);
        info_table = (TableLayout) view.findViewById(R.id.info_table);
        no_detail = (TextView) view.findViewById(R.id.no_details) ;
        requestQueue = Volley.newRequestQueue(getActivity());
        reviewList = new ArrayList<>();

        String url = "http://place-201423.appspot.com/json/?place_id="+place_id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject result = response.getJSONObject("result");

                            try {
                                String address = result.getString("formatted_address");
                                address_input.setText(address);
                            } catch(JSONException e){
                                address_row.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            try{
                                final String phone = result.getString("international_phone_number");
//                                phone_input.setText(phone);
                                phone_input.setMovementMethod(LinkMovementMethod.getInstance());
                                String tmp = "<a href=''>" + phone + "</a>";
                                phone_input.setText(Html.fromHtml(tmp));
                                phone_input.setOnClickListener(new View.OnClickListener(){
                                    @Override
                                    public void onClick(View v){
                                        try{
                                            Intent call_intent = new Intent(Intent.ACTION_DIAL);
                                            String tel = "tel:" + phone;
                                            call_intent.setData(Uri.parse(tel));
                                            startActivity(call_intent);
                                        } catch (ActivityNotFoundException e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            } catch(JSONException e){
                                phone_row.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            try{
                                String price_level_tmp = result.getString("price_level");
                                StringBuilder sb= new StringBuilder();
                                for(int i=0;i<Integer.parseInt(price_level_tmp);i++){
                                    sb.append("$");
                                }
                                String price_level = sb.toString();
                                price_input.setText(price_level);

                            } catch(JSONException e){
                                price_row.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            try{
                                String rating = result.getString("rating");
                                rating_input.setNumStars(5);

                                rating_input.setRating(Float.valueOf(rating));
                            } catch(JSONException e){
                                rating_row.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            try{
                                String google_url = result.getString("url");
                                google_site_url = google_url;
                                google_input.setMovementMethod(LinkMovementMethod.getInstance());
                                String tmp =  "<a href='" + google_url +"'>" + google_url + "</a>";
                                google_input.setText(Html.fromHtml(tmp));
                            } catch(JSONException e){
                                google_row.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            try{
                                String website = result.getString("website");
                                website_url = website;
                                website_input.setMovementMethod(LinkMovementMethod.getInstance());
                                String tmp =  "<a href='" + website+ "'>" + website + "</a>";
                                website_input.setText(Html.fromHtml(tmp));
                            } catch(JSONException e){
                                website_row.setVisibility(View.GONE);
                                e.printStackTrace();
                            }

                            try {
                                JSONArray reviews = result.getJSONArray("reviews");

                                for (int i = 0; i < reviews.length(); i++) {
                                    JSONObject review = reviews.getJSONObject(i);
                                    String temp_convert = "";
                                    int temp_time = Integer.parseInt(review.getString("time"));
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date d = new Date(1000L * temp_time);
                                    temp_convert  = sdf.format(d);

                                    reviewList.add(new GoogleReviewModel(
                                            review.getString("author_url"),
                                            review.getString("profile_photo_url"),
                                            review.getString("author_name"),
                                            review.getString("rating"),
                                            review.getString("time"),
                                            temp_convert,
                                            review.getString("text")
                                    ));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String name_y = "";
                            String lat_y_st = "";
                            String lng_y_st = "";
                            String city_y = "";
                            String state_y = "";
                            String country_y = "";
                            String postal_code_y = "";
                            String address1_y = "";
                            String phone_y = "";
                            try{
                                name_y = result.getString("name");

                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            try{
                                JSONObject geomet = result.getJSONObject("geometry");
                                JSONObject locat = geomet.getJSONObject("location");
                                lat_y_st = locat.getString("lat");
                                lng_y_st = locat.getString("lng");
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            try {
                                String addr1_raw = result.getString("vicinity");
                                String[] addr1_cut = addr1_raw.split(",");
                                address1_y = addr1_cut[0];

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONArray addr_comps = result.getJSONArray("address_components");

                                for (int i = 0; i < addr_comps.length(); i++) {
                                    JSONObject comps = addr_comps.getJSONObject(i);
                                    JSONArray types = comps.getJSONArray("types");
                                    String type = types.get(0).toString();
                                    if (type.equals("locality")) {
                                        city_y = comps.getString("short_name");
                                    } else if (type.equals("administrative_area_level_1")) {
                                        state_y = comps.getString("short_name");
                                    } else if (type.equals("country")) {
                                        country_y = comps.getString("short_name");
                                    } else if (type.equals("postal_code")) {
                                        postal_code_y = comps.getString("short_name");
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                phone_y = result.getString("international_phone_number").replaceAll("[()\\s-]+", "");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            yelpData = new YelpData(name_y, lat_y_st, lng_y_st, city_y, state_y, country_y, postal_code_y, address1_y, phone_y);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideProgressingView();
                        info_table.setVisibility(View.VISIBLE);
                        no_detail.setVisibility(View.GONE);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgressingView();
                        info_table.setVisibility(View.GONE);
                        no_detail.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity().getApplicationContext(), "No detail info found!", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);


        return view;
    }

    public void showProgressingView() {

        if (!progress_flag) {
            progress_flag = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.progressbar_layout, null);
            View v = getActivity().findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    public void hideProgressingView() {
        View v = getActivity().findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        progress_flag = false;
    }
}
