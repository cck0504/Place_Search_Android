package com.example.chillysoup.searchplace;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Rating;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.places.GeoDataClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{

    private Context mContext ;
    private List<GoogleReviewModel> reviewList;

    public ReviewAdapter(Context mContext, List reviewList) {
        this.mContext = mContext;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.review_item, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.view_container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String url = reviewList.get(viewHolder.getAdapterPosition()).getAuthur_url();
                Intent intent = new Intent (Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvname.setText(reviewList.get(position).getAuthor_name());
        holder.tvtimeconvert.setText(reviewList.get(position).getTime_convert());
        holder.tvrating.setRating(Float.parseFloat(reviewList.get(position).getRating()));
        holder.tvtext.setText(reviewList.get(position).getText());
        Glide.with(mContext).load(reviewList.get(position).getProfile_url()).into(holder.tvicon);
    }

    @Override
    public int getItemCount() {

        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvname;
        TextView tvtimeconvert;
        TextView tvtext;
        RatingBar tvrating;
        ImageView tvicon;
        RelativeLayout view_container;

        public MyViewHolder(View itemView) {
            super(itemView);

            view_container = itemView.findViewById(R.id.container_review);
            tvname = itemView.findViewById(R.id.rname);
            tvtimeconvert = itemView.findViewById(R.id.rtime_convert);
            tvtext = itemView.findViewById(R.id.rtext);
            tvrating = itemView.findViewById(R.id.rrating);
            tvicon = itemView.findViewById(R.id.ricon);
        }
    }
}
