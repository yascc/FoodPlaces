package com.sp.foodplaces;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Directory_WebView extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.directory_webview);

        getSupportActionBar().setTitle(getIntentData().trim());

        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        String name = getIntentData().trim();
        String parseName = name.replaceAll(" ", "+");
        String mURL = "https://www.google.com/search?q=" +
                        parseName + "&sourceid=chrome&ie=UTF-8";
        webView.loadUrl(mURL);

    }

    private String getIntentData() {
        String name = "";
        if (getIntent().hasExtra("name")){
            name = getIntent().getStringExtra("name");
        }
        return name;
    }
}
