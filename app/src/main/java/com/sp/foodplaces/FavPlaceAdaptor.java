package com.sp.foodplaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavPlaceAdaptor extends RecyclerView.Adapter<FavPlaceAdaptor.ViewHolder> {

    private ArrayList<ModelFavPlaceItem> modelFavPlaceItems;
    private Context context;
    private FavoriteDB favDB;

    public FavPlaceAdaptor(ArrayList<ModelFavPlaceItem> modelFavPlaceItems, Context context) {
        this.modelFavPlaceItems = modelFavPlaceItems;
        this.context = context;
    }

    @NonNull
    @Override
    public FavPlaceAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        favDB = new FavoriteDB(context);
        //create table on first
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if (firstStart) {
            createTableOnFirstStart();
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favorite,
                parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavPlaceAdaptor.ViewHolder holder, int position) {
        final ModelFavPlaceItem favItem = modelFavPlaceItems.get(position);

        readCursorData(favItem, holder);
        holder.imageView.setImageResource(favItem.getImageResourse());
        holder.titleTextView.setText(favItem.getTitle());
        holder.ratingTextView.setText(favItem.getRating());
        holder.addressTextView.setText(favItem.getAddress());
    }

    @Override
    public int getItemCount() {
        return modelFavPlaceItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView ratingTextView;
        TextView addressTextView;
        ImageButton favBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            favBtn = itemView.findViewById(R.id.favBtn);


            //add to fav btn
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ModelFavPlaceItem modelFavPlaceItem = modelFavPlaceItems.get(position);

                    if (modelFavPlaceItem.getFavStatus().equals("0")){
                        modelFavPlaceItem.setFavStatus("1");
                        favDB.insertIntoTheDatabase(
                                modelFavPlaceItem.getTitle(),
                                modelFavPlaceItem.getImageResourse(),
                                modelFavPlaceItem.getRating(),
                                modelFavPlaceItem.getAddress(),
                                modelFavPlaceItem.getKey_id(),
                                modelFavPlaceItem.getFavStatus());
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_red);
                    }
                    else {
                        modelFavPlaceItem.setFavStatus("0");
                        favDB.remove_fav(modelFavPlaceItem.getKey_id());
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_favorite_shadow);
                    }

                }
            });
        }
    }

    private void createTableOnFirstStart() {
        favDB.insertEmpty();

        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void readCursorData(ModelFavPlaceItem modelFavPlaceItem, ViewHolder viewHolder) {
        Cursor cursor = favDB.read_all_data(modelFavPlaceItem.getKey_id());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavoriteDB.FAVORITE_STATUS));
                modelFavPlaceItem.setFavStatus(item_fav_status);

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
