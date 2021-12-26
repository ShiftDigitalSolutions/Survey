package com.shifteg.survey;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.shifteg.survey.databinding.ActivitySurveyBinding;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SurveyActivity extends AppCompatActivity implements PageLoadListener {

    //? Constants
    public String URL = "https://survey-trial-34978.web.app/survey/";
    private static final String TAG = "SurveyActivityTag";

    //? Views
    ActivitySurveyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey);


        //! hashed
        Intent intent = getIntent();
        SurveyModel surveyModel = intent.getParcelableExtra("SurveyModel");

        this.URL = initializeSurveyUrl(surveyModel);

        if (checkUrl(this.URL)) {
            initActions();
            binding.webView.setVisibility(View.VISIBLE);
        } else {
            binding.webView.setVisibility(View.GONE);
            binding.backgroundText.setText(URL);
            Snackbar.make(binding.getRoot(), "Malformed URL !!", Snackbar.LENGTH_LONG).show();
            binding.swipeRefreshLayout.setOnRefreshListener(this::onPageLoaded);
        }

        //+ new feature
//        loadSurvey2();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        URL = "";
        finish();
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


        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onPageLoaded();
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);

                if (!url.equalsIgnoreCase(webUrl)) {
                    binding.swipeRefreshLayout.setVisibility(View.GONE);
                }
            }

        });

        binding.webView.loadUrl(webUrl);
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

    private ValueCallback<Uri[]> filePathUploadCallback;

    @SuppressLint("SetJavaScriptEnabled")
    private void loadSurvey2() {

        final String webUrl = "https://cgi-lib.berkeley.edu/ex/fup.html";

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.webView.getSettings().setForceDark(WebSettings.FORCE_DARK_AUTO);
        }


        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: ");
                onPageLoaded();
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

        });

        binding.webView.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {

                if (filePathUploadCallback != null) {
                    filePathUploadCallback.onReceiveValue(null);
                }

                filePathUploadCallback = filePathCallback;

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");


                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
        });

        binding.webView.loadUrl(webUrl);
    }

    private final static int FCR = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Uri[] results = null;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FCR) {
                String dataString = intent.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                    filePathUploadCallback.onReceiveValue(results);
                    filePathUploadCallback = null;
                }
            }
        }

    }

}