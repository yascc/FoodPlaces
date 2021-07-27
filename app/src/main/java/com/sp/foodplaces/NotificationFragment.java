package com.sp.foodplaces;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {


    //RecyclerView
    static RecyclerView notificationsRV;

    private static ArrayList<ModelNotification> notificationsList = new ArrayList<>();

    public static ArrayList<ModelNotification> getNotificationsList() {
        return notificationsList;
    }
    public static ArrayList<ModelNotification> setNotificationsList(int image, String timestamp, String notification, String title) {
        //this.notificationsList = notificationsList;
        notificationsList.add(new ModelNotification(image, timestamp, notification, title));
        return notificationsList;
    }


    public NotificationFragment() {
    // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    /*public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        // Refresh tab data:

        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        //init recyclerview
        notificationsRV = view.findViewById(R.id.notificationsRV);

        notificationsRV.setLayoutManager(new LinearLayoutManager(getContext()));

        /* //notificationsList = new ArrayList<>();
       notificationsList.add(new ModelNotification(R.drawable.ic_baseline_notifications, "Iron Man1", "5 Star Restaurant is near you!","Enter Geofence"));
        notificationsList.add(new ModelNotification(R.drawable.ic_baseline_notifications,"Hulk2", "5 Star Restaurant is near you!","Enter Geofence"));
*/

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationsRV.setAdapter(new AdaptorNotification(notificationsList));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL);
        notificationsRV.addItemDecoration(dividerItemDecoration);
        return view;
    }

}
