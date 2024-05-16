package it.unina.dietideals24.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unina.dietideals24.R;
import it.unina.dietideals24.adapter.NotificationAdapter;
import it.unina.dietideals24.model.Notification;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.NotificationAPI;
import it.unina.dietideals24.utils.NetworkUtility;
import it.unina.dietideals24.utils.localstorage.LocalDietiUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerViewNotification;
    private ProgressBar notificationProgressBar;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        initializeViews(view);
        initializeNotification();

        return view;
    }

    private void initializeViews(View view) {
        recyclerViewNotification = view.findViewById(R.id.notificationList);
        notificationProgressBar = view.findViewById(R.id.notificationProgressBar);
        notificationProgressBar.setVisibility(View.VISIBLE);
    }

    private void initializeNotification() {
        NotificationAPI notificationAPI = RetrofitService.getRetrofitInstance().create(NotificationAPI.class);
        notificationAPI.getNotificationByReceiverId(LocalDietiUser.getLocalDietiUser(getContext()).getId()).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.body() != null) {
                    initializeNotificationAdapter(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getContext());
            }
        });
        notificationProgressBar.setVisibility(View.INVISIBLE);
    }

    private void initializeNotificationAdapter(List<Notification> notifications) {
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> adapterNotification = new NotificationAdapter(notifications);
        recyclerViewNotification.setAdapter(adapterNotification);
    }
}