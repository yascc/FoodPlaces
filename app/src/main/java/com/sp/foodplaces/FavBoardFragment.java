package com.sp.foodplaces;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavBoardFragment extends Fragment {

    static RecyclerView favRV;

    private FavoriteDB favDB;
    private List<ModelFavItem> modelFavItemList = new ArrayList<>();
    private FavAdaptor favAdaptor;

    public FavBoardFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fav, container, false);

        favDB = new FavoriteDB(getActivity());

        favRV = root.findViewById(R.id.favRV);

        favRV.setHasFixedSize(true);
        favRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadData();

        return root;
    }

    private void loadData() {
        if (modelFavItemList != null) {
            modelFavItemList.clear();
        }
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavoriteDB.ITEM_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavoriteDB.KEY_ID));
                String rating = cursor.getString(cursor.getColumnIndex(FavoriteDB.ITEM_RATING));
                String address = cursor.getString(cursor.getColumnIndex(FavoriteDB.ITEM_ADDRESS));
                int image = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteDB.ITEM_IMAGE)));
                ModelFavItem modelFavItem = new ModelFavItem(title, id, rating, address, image);
                modelFavItemList.add(modelFavItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        favAdaptor = new FavAdaptor(getActivity(), modelFavItemList);

        favRV.setAdapter(favAdaptor);

    }

}

