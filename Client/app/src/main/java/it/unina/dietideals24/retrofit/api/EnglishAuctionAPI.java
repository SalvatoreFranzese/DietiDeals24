package it.unina.dietideals24.retrofit.api;

import java.util.ArrayList;

import it.unina.dietideals24.dto.EnglishAuctionDto;
import it.unina.dietideals24.enumerations.CategoryEnum;
import it.unina.dietideals24.model.EnglishAuction;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface EnglishAuctionAPI {
    @GET("/english-auctions")
    Call<ArrayList<EnglishAuction>> getEnglishAuctions();

    @GET("/english-auctions/first-six")
    Call<ArrayList<EnglishAuction>> getFirst6EnglishAuctions();

    @GET("/english-auctions/category/{category}")
    Call<ArrayList<EnglishAuction>> getEnglishAuctionsByCategory(@Path("category") CategoryEnum category);

    @GET("/english-auctions/{id}")
    Call<EnglishAuction> getEnglishAuctionById(@Path("id") Long idAuction);

    @GET("/english-auctions/search/{keyword}")
    Call<ArrayList<EnglishAuction>> getEnglishAuctionsByKeyword(@Path("keyword") String keyword);

    @GET("/english-auctions/owner/{id}")
    Call<ArrayList<EnglishAuction>> getEnglishAuctionsByOwnerId(@Path("id") Long id);

    @POST("/english-auctions/create")
    Call<EnglishAuction> createEnglishAuction(@Body EnglishAuctionDto englishAuctionDto);

    @Multipart
    @POST("/english-auctions/{id}/image")
    Call<Void> uploadEnglishAuctionImage(@Path("id") Long id, @Part MultipartBody.Part image);
}
