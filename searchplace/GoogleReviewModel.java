package com.example.chillysoup.searchplace;

import java.util.Comparator;

public class GoogleReviewModel {
    private String authur_url;
    private String profile_url;
    private String author_name;
    private String rating;
    private String time;
    private String time_convert;
    private String text;

    public GoogleReviewModel() {
    }

    public GoogleReviewModel(String authur_url, String profile_url, String author_name, String rating, String time, String time_convert, String text) {
        this.authur_url = authur_url;
        this.profile_url = profile_url;
        this.author_name = author_name;
        this.rating = rating;
        this.time = time;
        this.time_convert = time_convert;
        this.text = text;
    }

    public String getAuthur_url() {
        return authur_url;
    }

    public void setAuthur_url(String authur_url) {
        this.authur_url = authur_url;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime_convert() {
        return time_convert;
    }

    public void setTime_convert(String time_convert) {
        this.time_convert = time_convert;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Comparator<GoogleReviewModel> HighRateSort = new Comparator<GoogleReviewModel>() {

        public int compare(GoogleReviewModel r1, GoogleReviewModel r2) {

            return r2.getRating().compareTo(r1.getRating());
        }
    };

    public static Comparator<GoogleReviewModel> LowRateSort = new Comparator<GoogleReviewModel>() {

        public int compare(GoogleReviewModel r1, GoogleReviewModel r2) {

            return r1.getRating().compareTo(r2.getRating());
        }
    };

    public static Comparator<GoogleReviewModel> MostRecentSort = new Comparator<GoogleReviewModel>() {

        public int compare(GoogleReviewModel r1, GoogleReviewModel r2) {

            return r2.getTime().compareTo(r1.getTime());
        }
    };

    public static Comparator<GoogleReviewModel> LeastRecentSort = new Comparator<GoogleReviewModel>() {

        public int compare(GoogleReviewModel r1, GoogleReviewModel r2) {

            return r1.getTime().compareTo(r2.getTime());
        }
    };
}
