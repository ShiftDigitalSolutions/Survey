package com.shifteg.survey;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("survey")
    Call<SurveyModel> storePost(@Body SurveyModel post);
}
