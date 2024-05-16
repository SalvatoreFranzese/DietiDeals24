package it.unina.dietideals24.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.List;

import it.unina.dietideals24.R;
import it.unina.dietideals24.enumerations.StateEnum;
import it.unina.dietideals24.model.Notification;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.ImageAPI;
import it.unina.dietideals24.retrofit.api.NotificationAPI;
import it.unina.dietideals24.utils.localstorage.BadgeVisibilityStatus;
import it.unina.dietideals24.view.activity.MainActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    List<Notification> notifications;
    Context context;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.NotificationViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {

        holder.deleteBtn.setOnClickListener(v -> deleteNotification(holder.getAdapterPosition()));

        switch (notifications.get(holder.getAdapterPosition()).getState()) {
            case VINTA -> {
                holder.stateTextView.setText(StateEnum.VINTA.toString());
                holder.stateTextView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_pistachio, context.getTheme())));
            }
            case PERSA -> {
                holder.stateTextView.setText(StateEnum.PERSA.toString());
                holder.stateTextView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red_rose, context.getTheme())));
            }
            case FALLITA -> {
                holder.stateTextView.setText(StateEnum.FALLITA.toString());
                holder.stateTextView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red_rose, context.getTheme())));
            }
            case CONCLUSA -> {
                holder.stateTextView.setText(StateEnum.CONCLUSA.toString());
                holder.stateTextView.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.yellow, context.getTheme())));
            }
        }

        holder.titleTextView.setText(notifications.get(holder.getAdapterPosition()).getTitleOfTheAuction());
        holder.priceTextView.setText(String.format("€%s", notifications.get(holder.getAdapterPosition()).getFinalPrice().toString()));
        retrieveImage(holder);
    }

    private void retrieveImage(NotificationAdapter.NotificationViewHolder holder) {
        String imageUrl = notifications.get(holder.getAdapterPosition()).getImageUrlOfTheAuction();

        if (imageUrl == null) {
            retrieveDefaultImage(holder);
        } else {
            retrieveNotificationImage(holder, imageUrl);
        }
    }

    private void retrieveNotificationImage(NotificationAdapter.NotificationViewHolder holder, String imageUrl) {
        holder.imageProgressBar.setVisibility(View.VISIBLE);

        ImageAPI imageAPI = RetrofitService.getRetrofitInstance().create(ImageAPI.class);
        imageAPI.getImageByUrl(imageUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        byte[] imageData = response.body().bytes();

                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions = requestOptions.transform(new CenterCrop());

                        Glide.with(context)
                                .load(bitmap)
                                .apply(requestOptions)
                                .into(holder.image);

                        holder.imageProgressBar.setVisibility(View.GONE);
                    }
                } catch (IOException e) {
                    retrieveDefaultImage(holder);
                    holder.imageProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                holder.imageProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void retrieveDefaultImage(NotificationAdapter.NotificationViewHolder holder) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        Glide.with(context)
                .load(R.drawable.no_image_auction)
                .apply(requestOptions)
                .into(holder.image);

        holder.imageProgressBar.setVisibility(View.GONE);
    }


    private void deleteNotification(int adapterPosition) {
        NotificationAPI notificationAPI = RetrofitService.getRetrofitInstance().create(NotificationAPI.class);
        notificationAPI.deleteNotificationById(notifications.get(adapterPosition).getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                notifications.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);

                BadgeVisibilityStatus.setBadgeVisibilityStatus(context, false);
                MainActivity.setBadgeNotificationVisibility(!notifications.isEmpty());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                MainActivity.setBadgeNotificationVisibility(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView deleteBtn;
        TextView titleTextView;
        TextView priceTextView;
        TextView stateTextView;
        ProgressBar imageProgressBar;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageItemNotification);
            titleTextView = itemView.findViewById(R.id.titleItemNotification);
            priceTextView = itemView.findViewById(R.id.priceItemNotification);
            deleteBtn = itemView.findViewById(R.id.deleteNotificationBtn);
            stateTextView = itemView.findViewById(R.id.stateText);
            imageProgressBar = itemView.findViewById(R.id.imageProgressBar);
        }
    }
}