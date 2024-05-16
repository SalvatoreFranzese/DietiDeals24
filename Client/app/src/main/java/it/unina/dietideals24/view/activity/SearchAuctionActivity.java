package it.unina.dietideals24.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unina.dietideals24.R;
import it.unina.dietideals24.adapter.AuctionAdapter;
import it.unina.dietideals24.model.Auction;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.model.EnglishAuction;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DownwardAuctionAPI;
import it.unina.dietideals24.retrofit.api.EnglishAuctionAPI;
import it.unina.dietideals24.utils.NetworkUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAuctionActivity extends AppCompatActivity {
    private EditText searchAuctionEditText;
    private ImageView backBtn;
    private RecyclerView auctionsRecyclerView;
    private List<Auction> foundAuctions;
    private ProgressBar searchProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_auction);

        initializeViews();

        backBtn.setOnClickListener(v -> finish());

        searchAuctionEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !isEmpty(searchAuctionEditText)) {
                performSearch();
                return true;
            } else
                return false;
        });
    }

    private void performSearch() {
        searchAuctionEditText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchAuctionEditText.getWindowToken(), 0);

        searchProgressBar.setVisibility(View.VISIBLE);

        if (foundAuctions != null)
            foundAuctions.clear();
        else
            foundAuctions = new ArrayList<>();
        searchAuctions(searchAuctionEditText.getText().toString());
    }

    private void searchAuctions(String keyword) {
        searchEnglishAuctions(keyword);
    }

    private void searchEnglishAuctions(String keyword) {
        EnglishAuctionAPI englishAuctionAPI = RetrofitService.getRetrofitInstance().create(EnglishAuctionAPI.class);
        englishAuctionAPI.getEnglishAuctionsByKeyword(keyword).enqueue(new Callback<ArrayList<EnglishAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<EnglishAuction>> call, Response<ArrayList<EnglishAuction>> response) {
                if (response.body() != null) {
                    foundAuctions.addAll(response.body());
                }
                searchDownwardAuctions(keyword);
            }

            @Override
            public void onFailure(Call<ArrayList<EnglishAuction>> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
                searchDownwardAuctions(keyword);
            }
        });
    }

    private void searchDownwardAuctions(String keyword) {
        DownwardAuctionAPI downwardAuctionAPI = RetrofitService.getRetrofitInstance().create(DownwardAuctionAPI.class);
        downwardAuctionAPI.getDownwardAuctionsByKeyword(keyword).enqueue(new Callback<ArrayList<DownwardAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<DownwardAuction>> call, Response<ArrayList<DownwardAuction>> response) {
                if (response.body() != null) {
                    foundAuctions.addAll(response.body());
                }
                finalizeSearch();
            }

            @Override
            public void onFailure(Call<ArrayList<DownwardAuction>> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
                finalizeSearch();
            }
        });
    }

    private void finalizeSearch() {
        Collections.sort(foundAuctions);
        initializeAuctionAdapter((ArrayList<Auction>) foundAuctions, auctionsRecyclerView);
        searchProgressBar.setVisibility(View.GONE);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private void initializeAuctionAdapter(ArrayList<Auction> auctions, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder> adapterAuction = new AuctionAdapter(auctions, AuctionAdapter.OrientationEnum.HORIZONTAL);
        recyclerView.setAdapter(adapterAuction);
    }

    private void initializeViews() {
        searchAuctionEditText = findViewById(R.id.inputSeachAuction);
        searchAuctionEditText.requestFocus();

        auctionsRecyclerView = findViewById(R.id.auctionsRecyclerView);

        searchProgressBar = findViewById(R.id.searchProgressBar);
        searchProgressBar.setVisibility(View.GONE);

        backBtn = findViewById(R.id.backBtn);
    }
}