package com.sp.foodplaces.Wong;

import android.content.Context;
import android.icu.number.Precision;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sp.foodplaces.R;

import java.text.DecimalFormat;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {
    private Context mContext;
    private List<PlaceModel> mData;

    public PlaceAdapter(Context mContext, List<PlaceModel> data) {
        this.mContext = mContext;
        this.mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.directory_placelist, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.MyViewHolder holder, int position) {

        holder.name.setText(mData.get(position).getName());
        holder.rating.setText(mData.get(position).getRating());
        holder.price.setText(mData.get(position).getPrice());

        if (mData.get(position).getPhoto() != null){
            Glide.with(mContext).load(mData.get(position).getPhoto()).into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, rating, price;
        ImageView photo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.place_name);
            rating = itemView.findViewById(R.id.place_rating);
            price = itemView.findViewById(R.id.place_price);
            photo = itemView.findViewById(R.id.place_photo);
        }
    }
}
