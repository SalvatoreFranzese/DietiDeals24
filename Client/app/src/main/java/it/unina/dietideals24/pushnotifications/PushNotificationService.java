package it.unina.dietideals24.pushnotifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

import it.unina.dietideals24.R;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.NotificationAPI;
import it.unina.dietideals24.utils.localstorage.BadgeVisibilityStatus;
import it.unina.dietideals24.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationService extends Service {
    private static final long INTERVAL = 60000L;
    private static final int REQUEST_CODE = 100;
    private static final String CHANNEL_ID = "CHANNEL_ID_NOTIFICATION";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String NOTIFICATION = "Notification";

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private long receiverId = -1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null) {
            String action = intent.getAction();
             if (action.equals(ACTION_START_FOREGROUND_SERVICE)) {
                if (receiverId == -1)
                    receiverId = intent.getLongExtra("receiverId", -1);
                Log.i(NOTIFICATION, "fetching with id: " + receiverId);
                fetchNotification(receiverId);
             } else if (action.equals(ACTION_STOP_FOREGROUND_SERVICE)) {
                    Log.i(NOTIFICATION, "Service stopped");
                    stopForeground(STOP_FOREGROUND_REMOVE);
                    stopSelf();
             }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(NOTIFICATION, "Service created");

        createNotificationChannel();
        startServiceWithNotification();
        scheduleNotificationCheck();
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Auction push notification", importance);
        notificationChannel.setLightColor(Color.YELLOW);
        notificationChannel.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void scheduleNotificationCheck() {
        Intent intent = new Intent(this, PushNotificationService.class);
        intent.setAction(ACTION_START_FOREGROUND_SERVICE);
        pendingIntent = PendingIntent.getService(this, REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL, pendingIntent);
        }
    }

    private void startServiceWithNotification() {
        startForeground(1, buildNotification());
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("DietiDeals24")
                .setContentText("Riceverai aggiornamenti per le aste a cui parteciperai!")
                .setSmallIcon(R.drawable.ic_gavel_notification)
                .setContentIntent(pendingIntent2)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(NOTIFICATION, "Service was destroyed!");
        cancelNotificationCheck();
    }

    private void cancelNotificationCheck() {
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private void fetchNotification(long receiverId) {
        if (receiverId == -1)
            return;

        NotificationAPI notificationAPI = RetrofitService.getRetrofitInstance().create(NotificationAPI.class);
        notificationAPI.getPushNotificationByReceiverId(receiverId).enqueue(new Callback<List<it.unina.dietideals24.model.Notification>>() {
            @Override
            public void onResponse(Call<List<it.unina.dietideals24.model.Notification>> call, Response<List<it.unina.dietideals24.model.Notification>> response) {
                Log.i(NOTIFICATION, "id: " + receiverId + " body: " + response.body());

                if (response.body() != null) {
                    pushNotifications(response.body());
                } else {
                    BadgeVisibilityStatus.setBadgeVisibilityStatus(getApplicationContext(), false);
                    MainActivity.setBadgeNotificationVisibility(false);
                }
            }

            @Override
            public void onFailure(Call<List<it.unina.dietideals24.model.Notification>> call, Throwable t) {
                BadgeVisibilityStatus.setBadgeVisibilityStatus(getApplicationContext(), false);
                MainActivity.setBadgeNotificationVisibility(false);
            }
        });
    }

    private void pushNotifications(List<it.unina.dietideals24.model.Notification> notifications) {
        if (notifications.isEmpty()) {
            BadgeVisibilityStatus.setBadgeVisibilityStatus(getApplicationContext(), false);
            MainActivity.setBadgeNotificationVisibility(false);
            return;
        }

        if (notifications.size() > 3) {
            pushManyNotifications();
        } else {
            for (it.unina.dietideals24.model.Notification notification : notifications) {
                pushNotification(notification);
            }
        }
    }

    private void pushNotification(it.unina.dietideals24.model.Notification notification) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder
                .setSmallIcon(R.drawable.ic_gavel_notification)
                .setContentTitle(notification.getTitleOfTheAuction() + " terminata!")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        switch (notification.getState()) {
            case VINTA -> builder
                    .setContentText("Aggiudicato!")
                    .setColor(getApplicationContext().getResources().getColor(R.color.green_pistachio, getApplicationContext().getTheme()));
            case PERSA -> builder
                    .setContentText("Non ti sei aggiudicato l'asta")
                    .setColor(getApplicationContext().getResources().getColor(R.color.red_rose, getApplicationContext().getTheme()));
            case CONCLUSA -> builder
                    .setContentText("L'asta è terminata")
                    .setColor(getApplicationContext().getResources().getColor(R.color.yellow, getApplicationContext().getTheme()));
            case FALLITA -> builder
                    .setContentText("Asta fallita")
                    .setColor(getApplicationContext().getResources().getColor(R.color.red_error, getApplicationContext().getTheme()));
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("redirect", "notificationFragment");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent2);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationChannel = new NotificationChannel(CHANNEL_ID, "Auction push notification", importance);
            notificationChannel.setLightColor(Color.YELLOW);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

    private void pushManyNotifications() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder
                .setSmallIcon(R.drawable.ic_gavel_notification)
                .setContentTitle("Ci sono novità!")
                .setContentText("Varie aste sono terminate, controlla come sono andate")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("redirect", "notificationFragment");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent2);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationChannel = new NotificationChannel(CHANNEL_ID, "ManyNotifications", importance);
            notificationChannel.setLightColor(Color.YELLOW);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

}
