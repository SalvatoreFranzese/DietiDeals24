package it.unina.dietideals24.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.unina.dietideals24.R;
import it.unina.dietideals24.adapter.AuctionAdapter;
import it.unina.dietideals24.model.Auction;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.model.EnglishAuction;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DownwardAuctionAPI;
import it.unina.dietideals24.retrofit.api.EnglishAuctionAPI;
import it.unina.dietideals24.retrofit.api.OfferAPI;
import it.unina.dietideals24.utils.NetworkUtility;
import it.unina.dietideals24.utils.localstorage.LocalDietiUser;
import it.unina.dietideals24.view.activity.AuctionsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionFragment extends Fragment {
    private RecyclerView yourAuctionsRecyclerView;
    private RecyclerView yourOffersRecyclerView;
    private Button yourAuctionsButton;
    private Button yourOffersButton;
    private ProgressBar yourAuctionsProgressBar;
    private ProgressBar yourOffersProgressBar;

    public AuctionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auction, container, false);

        initializeViews(view);
        initializeAuctions();

        yourAuctionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AuctionsActivity.class);
            intent.putExtra("typeOfAuction", "Yours");
            intent.putExtra("category", "none");
            startActivity(intent);
        });

        yourOffersButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AuctionsActivity.class);
            intent.putExtra("typeOfAuction", "YourOffers");
            intent.putExtra("category", "none");
            startActivity(intent);
        });

        return view;
    }

    private void initializeAuctions() {
        initializeYourAuctions();
        initializeYourOffers();
    }

    private void initializeYourOffers() {
        ArrayList<Auction> yourOffers = new ArrayList<>();

        OfferAPI offerAPI = RetrofitService.getRetrofitInstance().create(OfferAPI.class);
        offerAPI.getAuctionsByOffererId(LocalDietiUser.getLocalDietiUser(getContext()).getId()).enqueue(new Callback<ArrayList<EnglishAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<EnglishAuction>> call, Response<ArrayList<EnglishAuction>> response) {
                if (response.body() != null) {
                    yourOffers.addAll(response.body());
                    yourOffersProgressBar.setVisibility(View.GONE);
                    initializeYourOffersAdapter(yourOffers);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<EnglishAuction>> call, Throwable t) {
                yourOffersProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getContext());
                initializeYourOffersAdapter(yourOffers);
            }
        });
    }

    private void initializeYourOffersAdapter(ArrayList<Auction> yourOffers) {
        yourOffersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder> yourOffersAdapter = new AuctionAdapter(yourOffers, AuctionAdapter.OrientationEnum.VERTICAL);
        yourOffersRecyclerView.setAdapter(yourOffersAdapter);
    }

    private void initializeYourAuctions() {
        ArrayList<Auction> yourAuctions = new ArrayList<>();
        initializeYourEnglishAuctions(yourAuctions);
    }

    private void initializeYourEnglishAuctions(ArrayList<Auction> yourAuctions) {
        EnglishAuctionAPI englishAuctionAPI = RetrofitService.getRetrofitInstance().create(EnglishAuctionAPI.class);
        englishAuctionAPI.getEnglishAuctionsByOwnerId(LocalDietiUser.getLocalDietiUser(getContext()).getId()).enqueue(new Callback<ArrayList<EnglishAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<EnglishAuction>> call, Response<ArrayList<EnglishAuction>> response) {
                if (response.body() != null) {
                    yourAuctions.addAll(response.body());
                    initializeYourDownwardAuctions(yourAuctions);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EnglishAuction>> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getContext());
                initializeYourDownwardAuctions(yourAuctions);
            }
        });
    }

    private void initializeYourDownwardAuctions(ArrayList<Auction> yourAuctions) {
        DownwardAuctionAPI downwardAuctionAPI = RetrofitService.getRetrofitInstance().create(DownwardAuctionAPI.class);
        downwardAuctionAPI.getDownwardAuctionsByOwnerId(LocalDietiUser.getLocalDietiUser(getContext()).getId()).enqueue(new Callback<ArrayList<DownwardAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<DownwardAuction>> call, Response<ArrayList<DownwardAuction>> response) {
                if (response.body() != null) {
                    yourAuctions.addAll(response.body());
                    yourAuctionsProgressBar.setVisibility(View.GONE);
                    initializeYourAuctionsAdapter(yourAuctions);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DownwardAuction>> call, Throwable t) {
                yourAuctionsProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getContext());
                initializeYourAuctionsAdapter(yourAuctions);
            }
        });

    }

    private void initializeYourAuctionsAdapter(ArrayList<Auction> yourAuctions) {
        yourAuctionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder> auctionAdatper = new AuctionAdapter(yourAuctions, AuctionAdapter.OrientationEnum.VERTICAL);
        yourAuctionsRecyclerView.setAdapter(auctionAdatper);
    }

    private void initializeViews(View view) {
        yourAuctionsRecyclerView = view.findViewById(R.id.yourAuctionsList);
        yourOffersRecyclerView = view.findViewById(R.id.yourOffersList);
        yourAuctionsButton = view.findViewById(R.id.yourAuctionsBtn);
        yourOffersButton = view.findViewById(R.id.yourOffersBtn);
        yourAuctionsProgressBar = view.findViewById(R.id.yourAuctionsProgressBar);
        yourOffersProgressBar = view.findViewById(R.id.yourOffersProgressBar);

    }
}