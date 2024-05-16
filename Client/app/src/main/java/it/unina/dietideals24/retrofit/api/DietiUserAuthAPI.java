package it.unina.dietideals24.retrofit.api;

import it.unina.dietideals24.dto.LoginDto;
import it.unina.dietideals24.dto.RegisterDto;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.response.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DietiUserAuthAPI {
    @POST("/auth/register")
    Call<DietiUser> register(@Body RegisterDto registerDto);

    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginDto loginDto);
}
