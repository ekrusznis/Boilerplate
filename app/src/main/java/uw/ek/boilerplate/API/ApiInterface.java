package uw.ek.boilerplate.API;

import retrofit2.Call;
import retrofit2.http.GET;
import uw.ek.boilerplate.Models.JsonData;

public interface ApiInterface {

    @GET("data.json")
    Call<JsonData> apiCall();

}
