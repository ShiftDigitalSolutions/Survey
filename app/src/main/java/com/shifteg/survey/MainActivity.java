package com.shifteg.survey;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.io.WriteAbortedException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements PageLoadListener {

    public static final String URL = "https://surveyprivatemodule.azurewebsites.net/survey/";
    SwipeRefreshLayout refreshLayout = null;
    WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View parentLayout = findViewById(android.R.id.content);

        initView();

        SurveyModel surveyModel = new SurveyModel("1", "Electrician", "الفيوم", "010");
        String surveyUrl = initializeSurveyUrl(surveyModel);

        if (checkUrl(surveyUrl))
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
        if (webUrl.isEmpty())
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

    private String initializeSurveyUrl(SurveyModel surveyModel) {
        StringBuilder builder = new StringBuilder();
        builder.append(URL);
        builder.append(surveyModel.getProjectId()).append("/");
        builder.append(surveyModel.getCategory()).append("/");
        builder.append(surveyModel.getRegion()).append("/");
        builder.append(surveyModel.getMobile()).append("/");

        return builder.toString();
    }
}