package it.unina.dietideals24.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

import it.unina.dietideals24.R;
import it.unina.dietideals24.model.Offer;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.ImageAPI;
import it.unina.dietideals24.utils.localstorage.LocalDietiUser;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.SellerViewHolder> {
    ArrayList<Offer> offerrers;
    Context context;

    public OfferAdapter(ArrayList<Offer> offerrers) {
        this.offerrers = offerrers;
    }

    @NonNull
    @Override
    public OfferAdapter.SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.offer_item, parent, false);
        return new OfferAdapter.SellerViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.SellerViewHolder holder, int position) {
        retrieveImage(holder);

        String emailOffer = offerrers.get(holder.getAdapterPosition()).getOfferer().getEmail();
        if (!emailOffer.equals(LocalDietiUser.getLocalDietiUser(context).getEmail()))
            holder.email.setText(String.format(replacingCharactersWithAsterisks(emailOffer)));
        else
            holder.email.setText(String.format(emailOffer));

        if (position == 0)
            holder.email.setTextColor(context.getResources().getColor(R.color.green_pistachio, context.getTheme()));
        else
            holder.email.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));

        holder.amount.setText(String.format("€%s", offerrers.get(holder.getAdapterPosition()).getAmount().toString()));
    }

    @Override
    public int getItemCount() {
        return offerrers.size();
    }

    private void retrieveImage(OfferAdapter.SellerViewHolder holder) {
        String imageUrl = offerrers.get(holder.getAdapterPosition()).getOfferer().getProfilePictureUrl();

        if (imageUrl == null)
            retrieveDefaultImage(holder);
        else
            retrieveOffererImage(holder, imageUrl);
    }

    private void retrieveOffererImage(OfferAdapter.SellerViewHolder holder, String imageUrl) {
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
                    }
                } catch (IOException e) {
                    retrieveDefaultImage(holder);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                retrieveDefaultImage(holder);
            }
        });
    }

    private void retrieveDefaultImage(OfferAdapter.SellerViewHolder holder) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        Glide.with(context)
                .load(R.drawable.round_person_24)
                .apply(requestOptions)
                .into(holder.image);
    }

    private String replacingCharactersWithAsterisks(String str) {
        return str.replaceFirst("\\b(\\w)\\S*?(\\S@)(\\S)\\S*(\\S\\.\\S*)\\b", "$1****$2$3****$4");
    }

    public class SellerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView email;
        TextView amount;

        public SellerViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageOffer);
            email = itemView.findViewById(R.id.emailOffer);
            amount = itemView.findViewById(R.id.amountOffer);
        }
    }
}
