package com.shifteg.survey;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements PageLoadListener {

    public static final String URL = "https://stackoverflow.com/questions/57449900/letting-webview-on-android-work-with-prefers-color-scheme-dark";
    //    public static final String URL = "fmdskhfks";
    SwipeRefreshLayout refreshLayout = null;
    WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View parentLayout = findViewById(android.R.id.content);

        initView();

        if (checkUrl(URL))
            initActions();
        else {
            Snackbar.make(parentLayout, "Malformed URL !!", Snackbar.LENGTH_LONG).show();
            refreshLayout.setOnRefreshListener(() -> onPageLoaded());
        }

    }

    private void initView() {
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);
    }

    private void initActions() {
        loadSurvey(URL);
        refreshLayout.setOnRefreshListener(() -> loadSurvey(URL));
    }


    private boolean checkUrl(final String webUrl) {
        if (webUrl.isEmpty() || !webUrl.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
            return false;
        return true;

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadSurvey(final String webUrl) {

        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webView.getSettings().setForceDark(WebSettings.FORCE_DARK_AUTO);
        }
        webView.loadUrl(webUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onPageLoaded();
            }
        });
    }

    @Override
    public void onPageLoaded() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }
}