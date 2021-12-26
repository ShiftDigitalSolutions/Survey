package com.shifteg.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.shifteg.survey.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivityTag";
    private static final String BASE_URL = "https://us-central1-dynamic-geo-14ed0.cloudfunctions.net/";

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        //+ added
//        startActivity(new Intent(MainActivity.this,SurveyActivity.class));

        //! hashed
        binding.submit.setOnClickListener((v) -> {
            if (checkEditText(binding.id) && checkEditText(binding.region) && checkEditText(binding.category) && checkEditText(binding.mobile))
                init();
        });
    }


    private boolean checkEditText(EditText editText) {
        if (editText.getText() == null || editText.getText().toString().isEmpty()) {
            editText.setError("Input error");
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private void init() {
        if (String.valueOf(binding.category.getText()).contains(" ")) {
            binding.category.setError("Has a space !!!");
            return;
        }
        hideKeyboard();
        String mobile = String.valueOf(binding.mobile.getText());
        int projectId = 0;
        try {
            projectId = Integer.parseInt(String.valueOf(binding.id.getText()));
        } catch (NumberFormatException e) {
            binding.id.requestFocus();
            binding.id.setError("Malformed Number");
            return;
        }
        String category = String.valueOf(binding.category.getText());
        String region = String.valueOf(binding.region.getText());
        SurveyModel survey = new SurveyModel(mobile, projectId, category, region);
        checkForSurvey(survey);
    }

    private void startSurvey(final SurveyModel survey) {
        Intent intent = new Intent(MainActivity.this, SurveyActivity.class);
        Log.d("SurveyActivityTag", "startSurvey: " + survey.getCategory());
        intent.putExtra("SurveyModel", survey);
        startActivity(intent);
    }


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }


    private void checkForSurvey(final SurveyModel survey) {

        binding.progressBar.setVisibility(View.VISIBLE);
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
                else if (response.code() == 406) {
                    Snackbar.make(binding.getRoot(), "No surveys found to fulfill", Snackbar.LENGTH_LONG).show();
                } else
                    Snackbar.make(binding.getRoot(), "Response is: ", Snackbar.LENGTH_LONG).show();

                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<SurveyModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure " + t.getMessage());
                Snackbar.make(binding.getRoot(), "onFailure"
                        , Snackbar.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }
}