package it.unina.dietideals24.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import it.unina.dietideals24.R;
import it.unina.dietideals24.adapter.OfferAdapter;
import it.unina.dietideals24.dto.OfferDto;
import it.unina.dietideals24.model.Auction;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.model.EnglishAuction;
import it.unina.dietideals24.model.Offer;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DownwardAuctionAPI;
import it.unina.dietideals24.retrofit.api.EnglishAuctionAPI;
import it.unina.dietideals24.retrofit.api.ImageAPI;
import it.unina.dietideals24.retrofit.api.OfferAPI;
import it.unina.dietideals24.utils.NetworkUtility;
import it.unina.dietideals24.utils.TimeUtility;
import it.unina.dietideals24.utils.localstorage.LocalDietiUser;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionDetailsActivity extends AppCompatActivity {
    private ImageView backBtn;
    private ImageView imageAuction;
    private ImageView imageSellerProfile;
    private TextView title;
    private TextView categoryName;
    private TextView description;
    private TextView currentPrice;
    private TextView timer;
    private TextView sellerInfoText;
    private TextView messageNoOfferrers;
    private TextInputLayout offerTextLayout;
    private EditText offerEditText;
    private Button makeAnOfferBtn;
    private ConstraintLayout sellerInfoBtn;
    private RecyclerView recyclerViewOfferrers;
    private ConstraintLayout offerersConstraintLayout;
    private LinearLayout offerLinearLayout;
    private Auction auction;
    private ArrayList<Offer> offerrers;
    private CountDownTimer auctionCountDownTimer;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ProgressBar imageProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());

        initializeViews();
        offerrers = new ArrayList<>();

        Long idAuction = getIntent().getLongExtra("id", -1);
        String auctionType = getIntent().getStringExtra("type");

        if (auctionType != null && auctionType.equals("ENGLISH")) {
            makeAnOfferBtn.setText(R.string.make_an_offer_label);
            getEnglishAuction(idAuction);
            getOfferrersEnglishAuction(idAuction);
        } else if (auctionType != null && auctionType.equals("DOWNWARD")) {
            makeAnOfferBtn.setText(R.string.buy_label);
            getDownwardAuction(idAuction);
            hideOfferersSection();
        }

        backBtn.setOnClickListener(v -> finish());

        sellerInfoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SellerInfoActivity.class);
            intent.putExtra("id", auction.getOwner().getId());
            startActivity(intent);
        });

        makeAnOfferBtn.setOnClickListener(v -> {
            if (auction instanceof EnglishAuction)
                showEnglishAuctionConfirmOfferDialog();
            else if (auction instanceof DownwardAuction)
                showDownwardAuctionConfirmOfferDialog();
        });
    }

    private void initializeViews() {
        imageAuction = findViewById(R.id.imageAcution);
        imageProgressBar = findViewById(R.id.imageProgressBar);
        imageProgressBar.setVisibility(View.GONE);
        imageSellerProfile = findViewById(R.id.imageProfile);

        title = findViewById(R.id.titleAuction);
        categoryName = findViewById(R.id.categoryAuction);
        description = findViewById(R.id.descriptionText);
        currentPrice = findViewById(R.id.currentPriceAuction);
        timer = findViewById(R.id.timerAuction);
        offerEditText = findViewById(R.id.inputAnOffer);
        offerEditText.setFocusable(false);
        offerTextLayout = findViewById(R.id.offerTextLayout);
        makeAnOfferBtn = findViewById(R.id.makeAnOfferBtn);
        backBtn = findViewById(R.id.backBtn);
        sellerInfoBtn = findViewById(R.id.sellerInfo);
        sellerInfoText = findViewById(R.id.sellerInfoText);
        recyclerViewOfferrers = findViewById(R.id.offerrersList);
        offerersConstraintLayout = findViewById(R.id.offerrersConstraintLayout);
        offerLinearLayout = findViewById(R.id.offerLinearLayout);

        messageNoOfferrers = findViewById(R.id.messageNoOfferrers);
        messageNoOfferrers.setVisibility(View.GONE);
    }

    private void refreshActivity() {
        recreate();
    }

    private void getEnglishAuction(Long idAuction) {
        EnglishAuctionAPI englishAuctionAPI = RetrofitService.getRetrofitInstance().create(EnglishAuctionAPI.class);
        englishAuctionAPI.getEnglishAuctionById(idAuction).enqueue(new Callback<EnglishAuction>() {
            @Override
            public void onResponse(Call<EnglishAuction> call, Response<EnglishAuction> response) {
                if (response.code() == 200 && response.body() != null) {
                    auction = response.body();
                    getAuctionImage(auction.getImageURL());
                    requestProfilePicture(auction.getOwner().getProfilePictureUrl());
                } else {
                    Toast.makeText(AuctionDetailsActivity.this, "L'asta non è più disponibile!", Toast.LENGTH_SHORT).show();
                    openMainActivity();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<EnglishAuction> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
                openMainActivity();
                finish();
            }
        });
    }

    private void getDownwardAuction(Long idAuction) {
        DownwardAuctionAPI downwardAuctionAPI = RetrofitService.getRetrofitInstance().create(DownwardAuctionAPI.class);
        downwardAuctionAPI.getDownwardAuctionById(idAuction).enqueue(new Callback<DownwardAuction>() {
            @Override
            public void onResponse(Call<DownwardAuction> call, Response<DownwardAuction> response) {
                if (response.code() == 200 && response.body() != null) {
                    auction = response.body();
                    getAuctionImage(auction.getImageURL());
                    requestProfilePicture(auction.getOwner().getProfilePictureUrl());
                } else {
                    Toast.makeText(AuctionDetailsActivity.this, "L'asta non è più disponibile!", Toast.LENGTH_SHORT).show();
                    openMainActivity();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DownwardAuction> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
                openMainActivity();
                finish();
            }
        });
    }

    private void getAuctionImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            useDefaultImage();
        } else
            requestAuctionImage(imageUrl);
    }

    private void useDefaultImage() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        Glide.with(getApplicationContext())
                .load(R.drawable.no_image_auction)
                .apply(requestOptions)
                .into(imageAuction);

        initializeFields();
    }

    private void requestAuctionImage(String imageUrl) {
        imageProgressBar.setVisibility(View.VISIBLE);

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
                                .into(imageAuction);
                    }


                } catch (IOException e) {
                    useDefaultImage();
                    Toast.makeText(AuctionDetailsActivity.this, "Impossibile caricare l'immagine'!", Toast.LENGTH_SHORT).show();
                }

                imageProgressBar.setVisibility(View.GONE);
                initializeFields();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());

                imageProgressBar.setVisibility(View.GONE);
                initializeFields();
            }
        });
    }

    private void makeEnglishOffer() {
        OfferDto offerDto = new OfferDto(new BigDecimal(offerEditText.getText().toString()), LocalDietiUser.getLocalDietiUser(getApplicationContext()).getId(), auction.getId());

        OfferAPI offerAPI = RetrofitService.getRetrofitInstance().create(OfferAPI.class);
        offerAPI.makeEnglishOffer(offerDto).enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {
                if (response.body() != null) {
                    Toast.makeText(AuctionDetailsActivity.this, "Offerta fatta!", Toast.LENGTH_SHORT).show();
                    logOffer();
                    refreshActivity();
                } else {
                    logFailedOffer();
                    showFailedOfferDialog("Offerta non effettuata, qualcuno è arrivato prima di te!", false);
                }
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }

    private void logFailedOffer() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "offer_button");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Offer button");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Offerta fallita");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    private void logOffer() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "offer_button");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Offer button");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Offerta riuscita");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    private void makeDownwardOffer() {
        OfferDto offerDto = new OfferDto(auction.getCurrentPrice(), LocalDietiUser.getLocalDietiUser(getApplicationContext()).getId(), auction.getId());

        OfferAPI offerAPI = RetrofitService.getRetrofitInstance().create(OfferAPI.class);
        offerAPI.makeDownwardOffer(offerDto).enqueue(new Callback<DownwardAuction>() {
            @Override
            public void onResponse(Call<DownwardAuction> call, Response<DownwardAuction> response) {
                if (response.body() != null) {
                    logPurchase();
                    showPurchaseConfirm(response.body());
                } else {
                    logFailedPurchase();
                    showFailedOfferDialog("Acquisto non effettuato, qualcuno è arrivato prima di te!", true);
                }
            }

            @Override
            public void onFailure(Call<DownwardAuction> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }

    private void requestProfilePicture(String imageUrl) {
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
                                .into(imageSellerProfile);
                    }
                } catch (IOException e) {
                    useDefaultImage();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                useDefaultImage();
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }

    private void logFailedPurchase() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "purchase_button");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Purchase button");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Acquisto fallito");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);
    }

    private void logPurchase() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "purchase_button");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Purchase button");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Acquisto riuscito");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);
    }

    private void showPurchaseConfirm(DownwardAuction downwardAuction) {
        ConstraintLayout failedOfferConstraintLayout = findViewById(R.id.confirmPurchaseConstraintLayout);
        View viewFailedOfferDialog = LayoutInflater.from(AuctionDetailsActivity.this).inflate(R.layout.confirm_purchase_dialog, failedOfferConstraintLayout);

        Button backToHomeButton = viewFailedOfferDialog.findViewById(R.id.backToAuctionBtn);

        TextView confirmPurchaseText = viewFailedOfferDialog.findViewById(R.id.confirmPurchaseText);
        confirmPurchaseText.setText(downwardAuction.getTitle() + " aggiudicata!");

        AlertDialog.Builder builder = new AlertDialog.Builder(AuctionDetailsActivity.this);
        builder.setView(viewFailedOfferDialog);
        final AlertDialog alertDialog = builder.create();

        backToHomeButton.setText("Torna alla home");

        backToHomeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.setOnDismissListener(v -> openMainActivity());

        alertDialog.show();
    }

    private void getOfferrersEnglishAuction(Long idAuction) {
        OfferAPI offerAPI = RetrofitService.getRetrofitInstance().create(OfferAPI.class);
        offerAPI.getOffersByEnglishAuctionId(idAuction).enqueue(new Callback<ArrayList<Offer>>() {
            @Override
            public void onResponse(Call<ArrayList<Offer>> call, Response<ArrayList<Offer>> response) {
                if (response.code() == 200 && response.body() != null) {
                    offerrers.addAll(response.body());
                    initializeOfferrers();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Offer>> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }

    private void hideOfferersSection() {
        offerersConstraintLayout.setVisibility(View.GONE);
    }

    private void initializeFields() {
        title.setText(auction.getTitle());
        categoryName.setText(auction.getCategory().toString());

        description.setText(auction.getDescription());
        description.setOnClickListener(v -> showBottomSheetDescription());

        currentPrice.setText(String.format("€%s", auction.getCurrentPrice().toString()));
        auctionCountDownTimer = startTimer();
        String sellerInfo = auction.getOwner().getName() + " " + auction.getOwner().getSurname();
        sellerInfoText.setText(sellerInfo);

        if (auction instanceof EnglishAuction englishAuction) {
            offerTextLayout.setHint(auction.getCurrentPrice().toString() + " + " + englishAuction.getIncreaseAmount());
            BigDecimal newOffer = auction.getCurrentPrice().add(englishAuction.getIncreaseAmount());
            offerEditText.setText(String.format(newOffer.toString()));
        } else if (auction instanceof DownwardAuction downwardAuction) {
            offerEditText.setText(String.format(downwardAuction.getCurrentPrice().toString()));
            offerTextLayout.setHint("Prezzo");
        }

        if (auction.getOwner().equals(LocalDietiUser.getLocalDietiUser(getApplicationContext()))) {
            makeAnOfferBtn.setEnabled(false);
            offerLinearLayout.setVisibility(View.GONE);
        }
    }

    private CountDownTimer startTimer() {
        Timestamp creation = new Timestamp(auction.getCreatedAt().getTime());
        Timestamp deadline = new Timestamp(creation.getTime() + auction.getTimerInMilliseconds());
        return new CountDownTimer(deadline.getTime() - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(TimeUtility.formatSeconds(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                showEndedAuctionDialog();
            }
        }.start();
    }

    private void showEndedAuctionDialog() {
        ConstraintLayout failedOfferConstraintLayout = findViewById(R.id.failedOfferConstraintLayout);
        View viewFailedOfferDialog = LayoutInflater.from(AuctionDetailsActivity.this).inflate(R.layout.failed_offer_dialog, failedOfferConstraintLayout);

        Button backToHomeButton = viewFailedOfferDialog.findViewById(R.id.backToAuctionBtn);

        TextView errorText = viewFailedOfferDialog.findViewById(R.id.failedOfferText);
        errorText.setText("L'asta è terminata!");

        AlertDialog.Builder builder = new AlertDialog.Builder(AuctionDetailsActivity.this);
        builder.setView(viewFailedOfferDialog);
        final AlertDialog alertDialog = builder.create();

        backToHomeButton.setText("Torna alla home");

        backToHomeButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.setOnDismissListener(v -> openMainActivity());

        alertDialog.show();
    }

    private void initializeOfferrers() {
        if (offerrers.isEmpty()) {
            messageNoOfferrers.setVisibility(View.VISIBLE);
            return;
        }

        messageNoOfferrers.setVisibility(View.GONE);
        Collections.reverse(offerrers);
        recyclerViewOfferrers.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerView.Adapter<OfferAdapter.SellerViewHolder> sellerAdapter = new OfferAdapter(offerrers);
        recyclerViewOfferrers.setAdapter(sellerAdapter);
    }

    private void openMainActivity() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }

    private void showBottomSheetDescription() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        TextView descriptionText = dialog.findViewById(R.id.descriptionText);
        descriptionText.setText(auction.getDescription());

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        descriptionText.setText(auction.getDescription());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    }

    private void showFailedOfferDialog(String errorMessage, boolean forceClose) {
        ConstraintLayout failedOfferConstraintLayout = findViewById(R.id.failedOfferConstraintLayout);
        View viewFailedOfferDialog = LayoutInflater.from(AuctionDetailsActivity.this).inflate(R.layout.failed_offer_dialog, failedOfferConstraintLayout);

        Button backToAuctionButton = viewFailedOfferDialog.findViewById(R.id.backToAuctionBtn);

        TextView errorText = viewFailedOfferDialog.findViewById(R.id.failedOfferText);
        errorText.setText(errorMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(AuctionDetailsActivity.this);
        builder.setView(viewFailedOfferDialog);
        final AlertDialog alertDialog = builder.create();

        backToAuctionButton.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (forceClose)
            alertDialog.setOnDismissListener(v -> openMainActivity());
        else
            alertDialog.setOnDismissListener(v -> refreshActivity());

        alertDialog.show();
    }

    private void showDownwardAuctionConfirmOfferDialog() {
        ConstraintLayout confirmOfferConstraintLayout = findViewById(R.id.confirmOfferConstraintLayout);
        View viewConfirmOfferDialog = LayoutInflater.from(AuctionDetailsActivity.this).inflate(R.layout.offer_confirm_dialog, confirmOfferConstraintLayout);

        Button confirmOfferButton = viewConfirmOfferDialog.findViewById(R.id.confirmOfferButton);
        Button cancelOfferButton = viewConfirmOfferDialog.findViewById(R.id.cancelOfferButton);

        TextView confirmOfferTitleText = viewConfirmOfferDialog.findViewById(R.id.confirmOfferTitleText);
        confirmOfferTitleText.setText("Acquista");

        TextView confirmOfferText = viewConfirmOfferDialog.findViewById(R.id.confirmOfferText);
        String confirmOffer = "Sicuro di voler acquistare " + auction.getTitle() + " per " + auction.getCurrentPrice() + "€?";
        confirmOfferText.setText(confirmOffer);

        AlertDialog.Builder builder = new AlertDialog.Builder(AuctionDetailsActivity.this);
        builder.setView(viewConfirmOfferDialog);
        final AlertDialog alertDialog = builder.create();

        confirmOfferButton.setOnClickListener(v -> {
            makeDownwardOffer();
            alertDialog.dismiss();
        });

        cancelOfferButton.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private void showEnglishAuctionConfirmOfferDialog() {
        ConstraintLayout confirmOfferConstraintLayout = findViewById(R.id.confirmOfferConstraintLayout);
        View viewConfirmOfferDialog = LayoutInflater.from(AuctionDetailsActivity.this).inflate(R.layout.offer_confirm_dialog, confirmOfferConstraintLayout);

        Button confirmOfferButton = viewConfirmOfferDialog.findViewById(R.id.confirmOfferButton);
        Button cancelOfferButton = viewConfirmOfferDialog.findViewById(R.id.cancelOfferButton);

        TextView confirmOfferText = viewConfirmOfferDialog.findViewById(R.id.confirmOfferText);
        String confirmOffer = "Sicuro di voler offire " + offerEditText.getText().toString() + "€?";
        confirmOfferText.setText(confirmOffer);

        AlertDialog.Builder builder = new AlertDialog.Builder(AuctionDetailsActivity.this);
        builder.setView(viewConfirmOfferDialog);
        final AlertDialog alertDialog = builder.create();

        confirmOfferButton.setOnClickListener(v -> {
            makeEnglishOffer();
            alertDialog.dismiss();
        });

        cancelOfferButton.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (auctionCountDownTimer != null)
            auctionCountDownTimer.cancel();
    }
}