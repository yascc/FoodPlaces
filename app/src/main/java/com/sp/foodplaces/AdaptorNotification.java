package com.sp.foodplaces;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdaptorNotification extends RecyclerView.Adapter<AdaptorNotification.HolderNotification>{

    //private Context context;
    private ArrayList<ModelNotification> notificationsList;

    public AdaptorNotification(ArrayList<ModelNotification> notificationsList) {
        //this.context = context;
        this.notificationsList = notificationsList;
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view row_notification

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_notification,
                parent, false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder,
                                 int position) {
        holder.notificationAvatarIV.setImageResource(notificationsList.get(position).getImage());
        holder.notificationTitleTV.setText(notificationsList.get(position).getTitle());
        holder.notificationBodyTV.setText(notificationsList.get(position).getNotification());
        holder.notificationTimeTV.setText(notificationsList.get(position).getTimestamp());

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    //holder for views of row_notifications.xml
    class HolderNotification extends RecyclerView.ViewHolder implements View.OnClickListener {

        //declare views
        ImageView notificationAvatarIV;
        TextView notificationTitleTV, notificationBodyTV, notificationTimeTV;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            //init views
            notificationAvatarIV = itemView.findViewById(R.id.notificationAvatarIV);
            notificationTitleTV = itemView.findViewById(R.id.notificationTitleTV);
            notificationBodyTV = itemView.findViewById(R.id.notificationBodyTV);
            notificationTimeTV = itemView.findViewById(R.id.notificationTimeTV);

            itemView.setOnClickListener(this);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    notificationsList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    return true;
                }
            });
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), notificationsList.get(getAdapterPosition()).toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
