package it.unina.dietideals24.retrofit.api;

import java.util.List;

import it.unina.dietideals24.model.Notification;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NotificationAPI {
    @GET("/notifications/receiver/{id}")
    Call<List<Notification>> getNotificationByReceiverId(@Path("id") Long receiverId);

    @GET("/notifications/receiver/{id}/push")
    Call<List<Notification>> getPushNotificationByReceiverId(@Path("id") Long receiverId);

    @DELETE("/notifications/{id}")
    Call<Void> deleteNotificationById(@Path("id") Long id);
}
