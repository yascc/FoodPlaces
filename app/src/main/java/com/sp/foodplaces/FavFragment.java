package com.sp.foodplaces;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavFragment extends Fragment {
    static RecyclerView favDirectoryRV;

    private ArrayList<ModelFavPlaceItem> modelFavPlaceItems = new ArrayList<>();

    public FavFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_fav_directory, container, false);

        //init recyclerview
        favDirectoryRV = root.findViewById(R.id.favDirectoryRV);

        favDirectoryRV.setHasFixedSize(true);
        favDirectoryRV.setAdapter(new FavPlaceAdaptor(modelFavPlaceItems, getActivity()));
        favDirectoryRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        //TODO: UPDATE WITH WONG's ARRAY
        /*
        modelFavPlaceItems.add(new ModelFavPlaceItem(R.drawable.thumbnail_place_01, "Genki Sushi","0","181 Orchard Rd, #04-30 Central, Orchard, Singapore 238896","4.2 Stars", "0"));
        modelFavPlaceItems.add(new ModelFavPlaceItem(R.drawable.thumbnail_place_02, "Blue Label","1","333A Orchard Road, #03-02 Mandarin Gallery, 238897", "4.3 Stars", "0"));
        modelFavPlaceItems.add(new ModelFavPlaceItem(R.drawable.thumbnail_place_03, "Al Capone's Ristorante & Bar","2","2 Orchard Link, #02-38 Scape Building, Singapore 237978", "3.2 Stars", "0"));
        */

        return root;
    }
    
}
