package com.example.chillysoup.searchplace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reviews extends Fragment {

    private static final String TAG = "REVIEWS_TAB";

    private TextView no_reviews;
    private LinearLayout review_wrap;

    private String name_yelp;
    private String lat_yelp;
    private String lng_yelp;
    private String city_yelp;
    private String state_yelp;
    private String country_yelp;
    private String postal_code_yelp;
    private String address1_yelp;
    private String phone_yelp;

    private Spinner site_drop;
    private Spinner order_drop;

    private RequestQueue requestQueue;
    private List<GoogleReviewModel> reviewList;
    private List<GoogleReviewModel> yelpList;
    private YelpData yelpData;
    private RecyclerView review_rv ;

    ViewGroup progressView;
    protected boolean progress_flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        no_reviews = (TextView) view.findViewById(R.id.no_reviews);
        review_wrap = (LinearLayout) view.findViewById(R.id.review_wrap);
        site_drop = (Spinner) view.findViewById(R.id.site_drop);
        order_drop = (Spinner) view.findViewById(R.id.order_drop);

        ArrayAdapter<CharSequence> adapter_site = ArrayAdapter.createFromResource(getActivity(), R.array.site_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_site.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        site_drop.setAdapter(adapter_site);
        site_drop.setSelection(0);

        ArrayAdapter<CharSequence> adapter_order = ArrayAdapter.createFromResource(getActivity(), R.array.order_array, android.R.layout.simple_spinner_dropdown_item);
        adapter_order.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order_drop.setAdapter(adapter_order);
        order_drop.setSelection(0);

        review_rv = view.findViewById(R.id.review_rv);
        reviewList = Info.reviewList;
        if (reviewList.size() != 0){
            setRvadapter(reviewList);
            no_reviews.setVisibility(View.GONE);
        }
        else{
            no_reviews.setVisibility(View.VISIBLE);
        }

        //TODO: yelp data not coming in
        yelpData = Info.yelpData;
        //yelpData.get~
        site_drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (site_drop.getSelectedItem().toString().equals("Google reviews")){
                    if (reviewList.size() != 0){
                        setRvadapter(reviewList);
                        no_reviews.setVisibility(View.GONE);
                        review_wrap.setVisibility(View.VISIBLE);
                    }
                    else{
                        review_wrap.setVisibility(View.GONE);
                        no_reviews.setVisibility(View.VISIBLE);
                    }
                } else{
                    showProgressingView();
                    order_drop.setSelection(0);
                    yelpList = new ArrayList<>();
                    name_yelp = yelpData.getName_yelp();
                    lat_yelp = yelpData.getLat_yelp();
                    lng_yelp = yelpData.getLng_yelp();
                    city_yelp = yelpData.getCity_yelp();
                    state_yelp = yelpData.getState_yelp();
                    country_yelp = yelpData.getCountry_yelp();
                    postal_code_yelp = yelpData.getPostal_code_yelp();
                    address1_yelp = yelpData.getAddress1_yelp();
                    phone_yelp = yelpData.getPhone_yelp();

                    requestQueue = Volley.newRequestQueue(getActivity());
                    String url = "http://place-201423.appspot.com/json/?name="+name_yelp+"&city="+city_yelp+"&state="+state_yelp+"&country="+country_yelp+
                                    "&postal_code="+postal_code_yelp+"&lat="+lat_yelp+"&lon="+lng_yelp+"&address1="+address1_yelp+"&phone="+phone_yelp;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray jsonArray = response.getJSONArray("reviews");
                                        for (int i=0; i < jsonArray.length(); i++){
                                            JSONObject best_match = jsonArray.getJSONObject(i);

                                            String user_url_y = "";
                                            String user_name_y = "";
                                            String yelp_url_y = "";
                                            String rating_y = "";
                                            String time_y = "";
                                            String time_to_int = "";
                                            String text_y = "";
                                            try {
                                                yelp_url_y = best_match.getString("url");
                                            } catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                            try {
                                                JSONObject user = best_match.getJSONObject("user");
                                                user_name_y = user.getString("name");
                                                user_url_y = user.getString("image_url");
                                            } catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                            try {
                                                rating_y = best_match.getString("rating");
                                            } catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                            try {
                                                time_y = best_match.getString("time_created");
                                                String temp_time = time_y.replaceAll("-", "");
                                                String temp_time_ = temp_time.replaceAll(" ", "");
                                                time_to_int = temp_time_.replaceAll(":", "");

                                            } catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                            try {
                                                text_y = best_match.getString("text");
                                            } catch (JSONException e){
                                                e.printStackTrace();
                                            }

                                            yelpList.add(new GoogleReviewModel(yelp_url_y, user_url_y, user_name_y, rating_y, time_to_int, time_y, text_y));
                                        }
                                        if (yelpList.size() != 0){
                                            no_reviews.setVisibility(View.GONE);
                                            review_wrap.setVisibility(View.VISIBLE);
                                            setRvadapter(yelpList);
                                        }
                                        else{
                                            review_wrap.setVisibility(View.GONE);
                                            no_reviews.setVisibility(View.VISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    hideProgressingView();
                                }

                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    review_wrap.setVisibility(View.GONE);
                                    no_reviews.setVisibility(View.VISIBLE);
                                    Toast.makeText(getActivity().getApplicationContext(), "Yelp data not found!", Toast.LENGTH_SHORT).show();
                                    hideProgressingView();
                                }
                    });

                    requestQueue.add(jsonObjectRequest);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        order_drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String orderType = order_drop.getSelectedItem().toString();
                if (site_drop.getSelectedItem().toString().equals("Google reviews")){
                    sortList(reviewList, orderType);
                }
                else{
                    sortList(yelpList, orderType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void sortList(List<GoogleReviewModel> list, String order_type) {

        ArrayList<GoogleReviewModel> changed_list = new ArrayList<>();
        for (GoogleReviewModel item: list){
            changed_list.add(item);
        }
        if (order_type.equals("Default order")){
            setRvadapter(list);
        } else if (order_type.equals("Highest rating")){
            Collections.sort(changed_list, GoogleReviewModel.HighRateSort);
        } else if (order_type.equals("Lowest rating")){
            Collections.sort(changed_list, GoogleReviewModel.LowRateSort);
        } else if (order_type.equals("Most recent")){
            Collections.sort(changed_list, GoogleReviewModel.MostRecentSort);
        } else if (order_type.equals("Least recent")){
            Collections.sort(changed_list, GoogleReviewModel.LeastRecentSort);
        }
        setRvadapter(changed_list);
    }


    public void setRvadapter (List<GoogleReviewModel> lst) {

        ReviewAdapter myAdapter = new ReviewAdapter(getActivity(), lst) ;
        review_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        review_rv.setAdapter(myAdapter);

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
