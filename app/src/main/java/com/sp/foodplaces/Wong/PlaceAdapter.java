package com.sp.foodplaces.Wong;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sp.foodplaces.FavAdaptor;
import com.sp.foodplaces.FavoriteDB;
import com.sp.foodplaces.R;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<PlaceModel> placeModelList;
    private FavoriteDB favDB;

    public PlaceAdapter(Context context, ArrayList<PlaceModel> data) {
        this.context = context;
        this.placeModelList = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favDB = new FavoriteDB(context);
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if(firstStart){
            createTableOnFirstStart();
        }

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.directory_placelist, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.MyViewHolder holder, int position) {

        final PlaceModel placeModel = placeModelList.get(position);
        readCursorData(placeModel, holder);

        holder.name.setText(placeModelList.get(position).getName());
        holder.rating.setText(placeModelList.get(position).getRating());
        holder.price.setText(placeModelList.get(position).getPrice());
        holder.address.setText(placeModelList.get(position).getAddress());

        if (placeModelList.get(position).getPhoto() != null){
            Glide.with(context).load(placeModelList.get(position).getPhoto()).into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return placeModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name, rating, price, address;
        ImageView photo;
        ImageButton favBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.place_name);
            rating = itemView.findViewById(R.id.place_rating);
            price = itemView.findViewById(R.id.place_price);
            address = itemView.findViewById(R.id.place_address);
            photo = itemView.findViewById(R.id.place_photo);
            favBtn = itemView.findViewById(R.id.place_favBtn);


            //add to fav btn
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer position = getAdapterPosition();
                    PlaceModel placeModel = placeModelList.get(position);

                    if(placeModel.getFav().equals("0")){
                        placeModel.setFav("1");
                        favDB.insertIntoTheDatabase(
                                placeModel.getName(),
                                placeModel.getPhoto(),
                                placeModel.getRating(),
                                placeModel.getAddress(),
                                //key_id now become integer primary key
                                "",
                                placeModel.getFav(),
                                placeModel.getPlaceID());
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_red);
                    }else {
                        placeModel.setFav("0");
                        favDB.remove_fav(placeModel.getPlaceID());
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow);
                    }
                }
            });
        }
    }

    private void createTableOnFirstStart() {
        //favDB.insertEmpty();
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void readCursorData(PlaceModel placeModel, MyViewHolder viewHolder){
        Cursor cursor = favDB.read_all_data(placeModel.getPlaceID());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavoriteDB.FAVORITE_STATUS));
                placeModel.setFav(item_fav_status);

                //check fav status
                if (item_fav_status != null && item_fav_status.equals("1")) {
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_red);
                } else if (item_fav_status != null && item_fav_status.equals("0")) {
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow);
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }
    }
}
