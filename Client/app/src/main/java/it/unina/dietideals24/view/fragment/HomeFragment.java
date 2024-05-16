package it.unina.dietideals24.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import it.unina.dietideals24.R;
import it.unina.dietideals24.adapter.AuctionAdapter;
import it.unina.dietideals24.adapter.CategoryAdapter;
import it.unina.dietideals24.adapter.entity.CategoryItem;
import it.unina.dietideals24.enumerations.FragmentTagEnum;
import it.unina.dietideals24.model.Auction;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.model.EnglishAuction;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DownwardAuctionAPI;
import it.unina.dietideals24.retrofit.api.EnglishAuctionAPI;
import it.unina.dietideals24.utils.CategoryArrayListInitializer;
import it.unina.dietideals24.utils.NetworkUtility;
import it.unina.dietideals24.view.activity.AuctionsActivity;
import it.unina.dietideals24.view.activity.CategoriesActivity;
import it.unina.dietideals24.view.activity.SearchAuctionActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchAuctionEditText;
    private Button categoryBtn;
    private Button englishAuctionsBtn;
    private Button downwardAuctionsBtn;
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewEnglishAuction;
    private RecyclerView recyclerViewDownwardAuction;
    private ProgressBar englishAuctionProgressBar;
    private ProgressBar downwardAuctionProgressBar;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        initializeViews(view);

        initializeCategories();
        initializeEnglishAuction();
        initializeDownwardAuction();

        searchAuctionEditText.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SEARCH_BAR");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Search bar");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);

            Intent intent = new Intent(getContext(), SearchAuctionActivity.class);
            startActivity(intent);
        });

        categoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CategoriesActivity.class);
            startActivity(intent);
        });

        englishAuctionsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AuctionsActivity.class);
            intent.putExtra("typeOfAuction", "English");
            intent.putExtra("category", "none");
            startActivity(intent);
        });

        downwardAuctionsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AuctionsActivity.class);
            intent.putExtra("typeOfAuction", "Downward");
            intent.putExtra("category", "none");
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener((this::refreshFragment));

        return view;
    }

    private void refreshFragment() {
        getParentFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment(), FragmentTagEnum.HOME.toString()).addToBackStack(FragmentTagEnum.HOME.toString()).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeEnglishAuction();
        initializeDownwardAuction();
    }

    private void initializeViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        searchAuctionEditText = view.findViewById(R.id.inputSeachAuction);
        categoryBtn = view.findViewById(R.id.categoriesBtn);
        englishAuctionsBtn = view.findViewById(R.id.englishAuctionsBtn);
        downwardAuctionsBtn = view.findViewById(R.id.downwardAuctionsBtn);

        englishAuctionProgressBar = view.findViewById(R.id.englishAuctionProgressBar);
        englishAuctionProgressBar.setVisibility(View.GONE);

        downwardAuctionProgressBar = view.findViewById(R.id.downwardAuctionProgressBar);
        downwardAuctionProgressBar.setVisibility(View.GONE);

        recyclerViewCategories = view.findViewById(R.id.categoryList);
        recyclerViewEnglishAuction = view.findViewById(R.id.englishAuctionsList);
        recyclerViewDownwardAuction = view.findViewById(R.id.downwardAuctionsList);
    }

    private void initializeCategories() {
        ArrayList<CategoryItem> categories = CategoryArrayListInitializer.getFirstSixCategoryItems(getContext(), getActivity());

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> categoryAdapter = new CategoryAdapter(categories, CategoryAdapter.ShapeEnum.ROUND);
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void initializeEnglishAuction() {
        englishAuctionProgressBar.setVisibility(View.VISIBLE);

        EnglishAuctionAPI englishAuctionAPI = RetrofitService.getRetrofitInstance().create(EnglishAuctionAPI.class);
        englishAuctionAPI.getFirst6EnglishAuctions().enqueue(new Callback<ArrayList<EnglishAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<EnglishAuction>> call, Response<ArrayList<EnglishAuction>> response) {
                ArrayList<Auction> auctions;

                if (response.body() == null)
                    auctions = new ArrayList<>();
                else
                    auctions = new ArrayList<>(response.body());

                englishAuctionProgressBar.setVisibility(View.GONE);
                initializeAuctionAdapter(auctions, recyclerViewEnglishAuction);
            }

            @Override
            public void onFailure(Call<ArrayList<EnglishAuction>> call, Throwable t) {
                englishAuctionProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getContext());
                initializeAuctionAdapter(new ArrayList<>(), recyclerViewEnglishAuction);
            }
        });
    }

    private void initializeDownwardAuction() {
        downwardAuctionProgressBar.setVisibility(View.VISIBLE);

        DownwardAuctionAPI downwardAuctionAPI = RetrofitService.getRetrofitInstance().create(DownwardAuctionAPI.class);
        downwardAuctionAPI.getFirst6DownwardAuctions().enqueue(new Callback<ArrayList<DownwardAuction>>() {
            @Override
            public void onResponse(Call<ArrayList<DownwardAuction>> call, Response<ArrayList<DownwardAuction>> response) {
                ArrayList<Auction> auctions;

                if (response.body() == null)
                    auctions = new ArrayList<>();
                else
                    auctions = new ArrayList<>(response.body());

                downwardAuctionProgressBar.setVisibility(View.GONE);
                initializeAuctionAdapter(auctions, recyclerViewDownwardAuction);
            }

            @Override
            public void onFailure(Call<ArrayList<DownwardAuction>> call, Throwable t) {
                downwardAuctionProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getContext());
                initializeAuctionAdapter(new ArrayList<>(), recyclerViewDownwardAuction);
            }
        });
    }

    private void initializeAuctionAdapter(ArrayList<Auction> auctions, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder> adapterAuction = new AuctionAdapter(auctions, AuctionAdapter.OrientationEnum.VERTICAL);
        recyclerView.setAdapter(adapterAuction);
    }
}