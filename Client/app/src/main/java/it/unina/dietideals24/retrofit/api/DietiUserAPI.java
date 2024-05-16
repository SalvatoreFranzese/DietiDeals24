package it.unina.dietideals24.retrofit.api;

import it.unina.dietideals24.dto.UpdatePasswordDto;
import it.unina.dietideals24.model.DietiUser;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DietiUserAPI {
    @GET("/users/email/{email}")
    Call<DietiUser> getUserByEmail(@Path("email") String email);

    @GET("/users/{id}")
    Call<DietiUser> getUserById(@Path("id") Long id);

    @POST("/users/{id}")
    Call<DietiUser> updateDietiUserDataById(@Path("id") Long id, @Body DietiUser dietiUser);

    @POST("/users/{id}/password")
    Call<DietiUser> updatePassword(@Path("id") Long id, @Body UpdatePasswordDto updatePasswordDto);

    @Multipart
    @POST("/users/{id}/profile-picture")
    Call<DietiUser> updateProfilePicture(@Path("id") Long id, @Part MultipartBody.Part image);
}
