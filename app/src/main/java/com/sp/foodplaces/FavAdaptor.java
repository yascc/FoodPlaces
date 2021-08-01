package com.sp.foodplaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavAdaptor extends RecyclerView.Adapter<FavAdaptor.ViewHolder>{

    private Context context;
    private List<ModelFavItem> modelFavItemList;
    private FavoriteDB favDB;
    ///private DatabaseReference refLike;

    public FavAdaptor(Context context, List<ModelFavItem> modelFavItemList) {
        this.context = context;
        this.modelFavItemList = modelFavItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item,
                parent, false);
        favDB = new FavoriteDB(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.favTextView.setText(modelFavItemList.get(position).getItem_title());
        holder.favImageView.setImageResource(modelFavItemList.get(position).getItem_image());
        holder.favAddressTextView.setText(modelFavItemList.get(position).getItem_address());
        holder.favRatingTextView.setText(modelFavItemList.get(position).getItem_rating());
    }

    @Override
    public int getItemCount() {
        return modelFavItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView favTextView;
        TextView favAddressTextView;
        TextView favRatingTextView;
        ImageButton favBtn;
        ImageView favImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favTextView = itemView.findViewById(R.id.favTextView);
            favAddressTextView = itemView.findViewById(R.id.favAddressTextView);
            favRatingTextView = itemView.findViewById(R.id.favRatingTextView);
            favBtn = itemView.findViewById(R.id.favBtn);
            favImageView = itemView.findViewById(R.id.favImageView);


            //remove from fav after click
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final ModelFavItem modelFavItem = modelFavItemList.get(position);

                    favDB.remove_fav(modelFavItem.getKey_id());
                    removeItem(position);
                }
            });
        }
    }

    private void removeItem(int position) {
        modelFavItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, modelFavItemList.size());
    }
}
