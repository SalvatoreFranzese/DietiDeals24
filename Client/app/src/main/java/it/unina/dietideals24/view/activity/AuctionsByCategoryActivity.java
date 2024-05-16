package it.unina.dietideals24.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.unina.dietideals24.R;
import it.unina.dietideals24.adapter.AuctionAdapter;
import it.unina.dietideals24.enumerations.CategoryEnum;
import it.unina.dietideals24.model.Auction;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.model.EnglishAuction;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DownwardAuctionAPI;
import it.unina.dietideals24.retrofit.api.EnglishAuctionAPI;
import it.unina.dietideals24.utils.CategoryArrayListInitializer;
import it.unina.dietideals24.utils.NetworkUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionsByCategoryActivity extends AppCompatActivity {
    private static final String CATEGORY = "category";
    private ImageView backBtn;
    private Button englishAuctionsBtn;
    private Button downwardAuctionsBtn;
    private ProgressBar englishAuctionProgressBar;
    private ProgressBar downwardAuctionProgressBar;
    private RecyclerView recyclerViewEnglishAuction;
    private RecyclerView recyclerViewDownwardAuction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctions_by_category);

        CategoryEnum category = CategoryEnum.valueOf(getIntent().getExtras().getString(CATEGORY).toUpperCase());

        initializeViews(category);
        initializeAuctions(category);

        backBtn.setOnClickListener(v -> finish());
        englishAuctionsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AuctionsActivity.class);
            intent.putExtra("typeOfAuction", "English");
            intent.putExtra(CATEGORY, category.name());
            startActivity(intent);
        });
        downwardAuctionsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AuctionsActivity.class);
            intent.putExtra("typeOfAuction", "Downward");
            intent.putExtra(CATEGORY, category.name());
            startActivity(intent);
        });
    }

    private void initializeAuctions(CategoryEnum category) {
        initializeEnglishAuctionsByCategory(category);
        initializeDownwardAuctionsByCategory(category);
    }

    private void initializeDownwardAuctionsByCategory(CategoryEnum category) {
        DownwardAuctionAPI downwardAuctionAPI = RetrofitService.getRetrofitInstance().create(DownwardAuctionAPI.class);
        downwardAuctionAPI.getDownwardAuctionsByCategory(category).enqueue(new Callback<ArrayList<DownwardAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<DownwardAuction>> call, Response<ArrayList<DownwardAuction>> response) {
                ArrayList<Auction> auctions;

                if (response.body() == null)
                    auctions = new ArrayList<>();
                else
                    auctions = new ArrayList<>(response.body());

                downwardAuctionProgressBar.setVisibility(View.INVISIBLE);
                initializeAuctionAdapter(auctions, recyclerViewDownwardAuction);
            }

            @Override
            public void onFailure(Call<ArrayList<DownwardAuction>> call, Throwable t) {
                downwardAuctionProgressBar.setVisibility(View.INVISIBLE);
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
                initializeAuctionAdapter(new ArrayList<>(), recyclerViewDownwardAuction);
            }
        });

    }

    private void initializeEnglishAuctionsByCategory(CategoryEnum category) {
        EnglishAuctionAPI englishAuctionAPI = RetrofitService.getRetrofitInstance().create(EnglishAuctionAPI.class);
        englishAuctionAPI.getEnglishAuctionsByCategory(category).enqueue(new Callback<ArrayList<EnglishAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<EnglishAuction>> call, Response<ArrayList<EnglishAuction>> response) {
                ArrayList<Auction> auctions;

                if (response.body() == null)
                    auctions = new ArrayList<>();
                else
                    auctions = new ArrayList<>(response.body());

                englishAuctionProgressBar.setVisibility(View.INVISIBLE);
                initializeAuctionAdapter(auctions, recyclerViewEnglishAuction);
            }

            @Override
            public void onFailure(Call<ArrayList<EnglishAuction>> call, Throwable t) {
                englishAuctionProgressBar.setVisibility(View.INVISIBLE);
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
                initializeAuctionAdapter(new ArrayList<>(), recyclerViewEnglishAuction);
            }
        });

    }

    private void initializeAuctionAdapter(ArrayList<Auction> auctions, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder> adapterAuction = new AuctionAdapter(auctions, AuctionAdapter.OrientationEnum.VERTICAL);
        recyclerView.setAdapter(adapterAuction);
    }

    private void initializeViews(CategoryEnum category) {
        backBtn = findViewById(R.id.backBtn);
        TextView categoryTitleTextView = findViewById(R.id.categoryTitleTextView);
        categoryTitleTextView.setText(CategoryArrayListInitializer.capitalize(category.name()));

        englishAuctionsBtn = findViewById(R.id.englishAuctionsBtn);
        downwardAuctionsBtn = findViewById(R.id.downwardAuctionsBtn);

        recyclerViewEnglishAuction = findViewById(R.id.englishAuctionsList);
        recyclerViewDownwardAuction = findViewById(R.id.downwardAuctionsList);

        englishAuctionProgressBar = findViewById(R.id.englishAuctionProgressBar);
        englishAuctionProgressBar.setVisibility(View.VISIBLE);

        downwardAuctionProgressBar = findViewById(R.id.downwardAuctionProgressBar);
        downwardAuctionProgressBar.setVisibility(View.VISIBLE);
    }
}
