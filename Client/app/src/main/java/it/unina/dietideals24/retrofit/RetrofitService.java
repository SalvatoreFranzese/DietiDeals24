package it.unina.dietideals24.retrofit;

import com.google.gson.Gson;

import it.unina.dietideals24.utils.localstorage.TokenManagement;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String BASE_URL = "<your_server_ip_address>";
    private static Retrofit retrofit = null;

    private RetrofitService() {
    }

    public static Retrofit getRetrofitInstance() {
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer " + TokenManagement.getToken())
                    .build();
            return chain.proceed(newRequest);
        }).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .client(httpClient)
                    .build();
        }

        return retrofit;
    }
}
