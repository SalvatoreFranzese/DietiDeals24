package it.unina.dietideals24.retrofit.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImageAPI {
    @GET("/images")
    Call<ResponseBody> getImageByUrl(@Query("imageUrl") String url);
}
