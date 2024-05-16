package it.unina.dietideals24.view.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

import it.unina.dietideals24.R;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DietiUserAPI;
import it.unina.dietideals24.retrofit.api.ImageAPI;
import it.unina.dietideals24.utils.NetworkUtility;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerInfoActivity extends AppCompatActivity {
    private TextView sellerFullNameText;
    private TextView geographicalAreaText;
    private TextView biographyText;
    private TextView linksText;
    private TextView titleSectionBiography;
    private TextView titleSectionLinks;
    private TextView messageNoInformation;
    private ImageView profilePicture;
    private ProgressBar profilePictureProgressBar;
    private ImageView backBtn;
    private DietiUser seller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_info);

        initializeViews();

        Long idSeller = getIntent().getLongExtra("id", -1);
        getSeller(idSeller);

        backBtn.setOnClickListener(v -> finish());
    }

    private void getSeller(Long id) {
        DietiUserAPI dietiUserAPI = RetrofitService.getRetrofitInstance().create(DietiUserAPI.class);
        dietiUserAPI.getUserById(id).enqueue(new Callback<DietiUser>() {
            @Override
            public void onResponse(Call<DietiUser> call, Response<DietiUser> response) {
                if (response.code() == 200) {
                    seller = response.body();
                    initializeFields();
                }
            }

            @Override
            public void onFailure(Call<DietiUser> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }

    private void initializeFields() {
        sellerFullNameText.setText(String.format("%s %s", seller.getName(), seller.getSurname()));

        if (seller.getProfilePictureUrl() != null && !seller.getProfilePictureUrl().isEmpty())
            requestProfilePicture(seller.getProfilePictureUrl());

        if (seller.getGeographicalArea() == null || seller.getBiography() == null || seller.getLinks() == null) {
            messageNoInformation.setVisibility(View.VISIBLE);

            titleSectionBiography.setVisibility(View.GONE);
            titleSectionLinks.setVisibility(View.GONE);

            geographicalAreaText.setVisibility(View.GONE);
            biographyText.setVisibility(View.GONE);
            linksText.setVisibility(View.GONE);
            return;
        }

        if (seller.getGeographicalArea().isEmpty() || seller.getBiography().isEmpty() || seller.getLinks().isEmpty()) {
            messageNoInformation.setVisibility(View.VISIBLE);

            titleSectionBiography.setVisibility(View.GONE);
            titleSectionLinks.setVisibility(View.GONE);

            geographicalAreaText.setVisibility(View.GONE);
            biographyText.setVisibility(View.GONE);
            linksText.setVisibility(View.GONE);
        } else {
            messageNoInformation.setVisibility(View.GONE);

            geographicalAreaText.setText(seller.getGeographicalArea());
            biographyText.setText(seller.getBiography());

            StringBuilder links = new StringBuilder();
            for (String link : seller.getLinks()) {
                links.append(link);
            }
            linksText.setText(links);
        }
    }

    private void initializeViews() {
        profilePictureProgressBar = findViewById(R.id.imageProgressBar);
        profilePicture = findViewById(R.id.profilePicture);
        sellerFullNameText = findViewById(R.id.sellerFullNameText);
        geographicalAreaText = findViewById(R.id.geographicalAreaSellerText);
        biographyText = findViewById(R.id.biographySellerText);
        linksText = findViewById(R.id.linksSellerText);

        titleSectionBiography = findViewById(R.id.titleSectionBiographySeller);
        titleSectionLinks = findViewById(R.id.titleSectionLinksSeller);
        messageNoInformation = findViewById(R.id.messageNoInformation);
        messageNoInformation.setVisibility(View.GONE);

        backBtn = findViewById(R.id.backBtn);
    }

    private void requestProfilePicture(String imageUrl) {
        profilePictureProgressBar.setVisibility(View.VISIBLE);

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

                        Glide.with(getApplicationContext())
                                .load(bitmap)
                                .apply(requestOptions)
                                .into(profilePicture);

                        profilePictureProgressBar.setVisibility(View.GONE);
                    }
                } catch (IOException e) {
                    profilePictureProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Impossibile caricare l'immagine'!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                profilePictureProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }
}