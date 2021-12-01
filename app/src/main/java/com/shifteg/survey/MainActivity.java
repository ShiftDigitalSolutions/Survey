package com.shifteg.survey;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.shifteg.survey.databinding.ActivityMainBinding;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivityTag";
    private static final String BASE_URL = "https://us-central1-dynamic-geo-14ed0.cloudfunctions.net/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurveyModel survey = new SurveyModel("0100", 2, "Electricians", "الفيوم");
        checkForSurvey(survey);

    }

    private void startSurvey(final SurveyModel survey) {
        Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
        intent.putExtra("SurveyModel", survey);
        startActivity(intent);
    }


    private void checkForSurvey(final SurveyModel survey) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API apiInterface = retrofit.create(API.class);

        Call<SurveyModel> call = apiInterface.storePost(survey);

        call.enqueue(new Callback<SurveyModel>() {
            @Override
            public void onResponse(@NonNull Call<SurveyModel> call, @NonNull Response<SurveyModel> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.code() == 200)
                    startSurvey(survey);
            }

            @Override
            public void onFailure(@NonNull Call<SurveyModel> call, Throwable t) {
                Log.d(TAG, "onFailure " + t.getMessage());
            }
        });

    }
}