package com.sp.foodplaces.Wong;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.sp.foodplaces.R;

public class DirectoryLoading {

    private Activity activity;
    private AlertDialog dialog;

    public DirectoryLoading(Activity activity){
        this.activity = activity;
    }

    public void startLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.directory_loading,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public  void endLoading(){
        dialog.dismiss();
    }
}
