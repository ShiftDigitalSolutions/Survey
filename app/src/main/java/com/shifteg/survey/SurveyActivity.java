package com.shifteg.survey;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.shifteg.survey.databinding.ActivitySurveyBinding;

public class SurveyActivity extends AppCompatActivity implements PageLoadListener {

    //? Constants
    public static String URL = "https://survey-trial-34978.web.app/survey/";
    private static final String TAG = "SurveyActivityTag";

    //? Views
    ActivitySurveyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey);


        Intent intent = getIntent();
        SurveyModel surveyModel = intent.getParcelableExtra("SurveyModel");

        URL = initializeSurveyUrl(surveyModel);

        if (checkUrl(URL))
            initActions();
        else {
            Snackbar.make(binding.getRoot(), "Malformed URL !!", Snackbar.LENGTH_LONG).show();
            binding.swipeRefreshLayout.setOnRefreshListener(this::onPageLoaded);
        }

    }


    private void initActions() {
        loadSurvey(URL);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> loadSurvey(URL));
    }


    private boolean checkUrl(final String webUrl) {
        return !webUrl.isEmpty() && webUrl.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadSurvey(String webUrl) {

        binding.webView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.webView.getSettings().setForceDark(WebSettings.FORCE_DARK_AUTO);
        }


        binding.webView.loadUrl(webUrl);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onPageLoaded();
                //TODO receive new url to close current webView
            }
        });

    }

    @Override
    public void onPageLoaded() {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    private String initializeSurveyUrl(SurveyModel surveyModel) {
        return URL +
                surveyModel.getProjectId() + "/" +
                surveyModel.getCategory() + "/" +
                surveyModel.getRegion() + "/" +
                surveyModel.getMobile();
    }
}