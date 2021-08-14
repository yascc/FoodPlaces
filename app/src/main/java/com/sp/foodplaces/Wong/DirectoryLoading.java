package com.sp.foodplaces.Wong;

import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sp.foodplaces.R;

public class DirectoryLoading {

    private AppCompatActivity activity;
    private AlertDialog dialog;

    public DirectoryLoading(AppCompatActivity activity){
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
