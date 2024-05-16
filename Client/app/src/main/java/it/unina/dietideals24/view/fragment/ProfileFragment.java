package it.unina.dietideals24.view.fragment;

import static androidx.core.content.ContextCompat.startForegroundService;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import it.unina.dietideals24.R;
import it.unina.dietideals24.dto.UpdatePasswordDto;
import it.unina.dietideals24.enumerations.FragmentTagEnum;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.pushnotifications.PushNotificationService;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DietiUserAPI;
import it.unina.dietideals24.retrofit.api.ImageAPI;
import it.unina.dietideals24.utils.MyFileUtils;
import it.unina.dietideals24.utils.NetworkUtility;
import it.unina.dietideals24.utils.localstorage.LocalDietiUser;
import it.unina.dietideals24.utils.localstorage.TokenManagement;
import it.unina.dietideals24.view.activity.LoginActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView userFullNameText;
    private TextView userEmailText;
    private TextView geographicalAreaText;
    private TextView biographyText;
    private TextView linksText;
    private TextView titleSectionBiography;
    private TextView titleSectionLinks;
    private TextView messageCompleteYourProfileText;
    private EditText inputNameEditText;
    private EditText inputGeographicalAreaEditText;
    private EditText inputSurnameEditText;
    private EditText inputBiographyEditText;
    private EditText inputLinksEditText;
    private ImageView imageProfile;
    private ImageView profilePicture;
    private ProgressBar profilePictureProgressBar;
    private ActivityResultLauncher<PickVisualMediaRequest> singlePhotoPickerLauncher;
    private Button changePasswordBtn;
    private Button editProfileBtn;
    private Button logOutBtn;
    private DietiUser localDietiUser;
    private Uri imageUri = null;
    private Bitmap bitmap;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        localDietiUser = LocalDietiUser.getLocalDietiUser(getActivity());

        initializeViews(view);
        setTextViewWithLocalDietiUserData();
        initializeSinglePhotoPickerLauncher();

        messageCompleteYourProfileText.setOnClickListener(v -> showEditProfileDialog(view));
        editProfileBtn.setOnClickListener(v -> showEditProfileDialog(view));
        changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog(view));

        logOutBtn.setOnClickListener(v -> {
            TokenManagement.deleteTokenData();
            LocalDietiUser.deleteLocalDietiUser(getActivity());
            stopNotificationService();

            Intent loginActivity = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginActivity);
        });

        return view;
    }

    private void stopNotificationService() {
        if (isServiceRunning(getContext(), PushNotificationService.class)) {
            Intent intent = new Intent(getContext(), PushNotificationService.class);
            intent.setAction(PushNotificationService.ACTION_STOP_FOREGROUND_SERVICE);
            getContext().startForegroundService(intent);
        }
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName()) && service.pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initializeViews(View view) {
        profilePictureProgressBar = view.findViewById(R.id.imageProgressBar);
        profilePicture = view.findViewById(R.id.profilePicture);
        userFullNameText = view.findViewById(R.id.userFullNameText);
        userEmailText = view.findViewById(R.id.userEmailText);
        geographicalAreaText = view.findViewById(R.id.geographicalAreaText);
        biographyText = view.findViewById(R.id.biographyText);
        linksText = view.findViewById(R.id.linksText);

        titleSectionBiography = view.findViewById(R.id.titleSectionBiography);
        titleSectionLinks = view.findViewById(R.id.titleSectionLinks);
        messageCompleteYourProfileText = view.findViewById(R.id.messageCompleteYourProfileText);
        messageCompleteYourProfileText.setVisibility(View.GONE);

        changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        logOutBtn = view.findViewById(R.id.logOutBtn);
    }

    private void initializeSinglePhotoPickerLauncher() {
        singlePhotoPickerLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri == null) {
                Toast.makeText(getContext(), "Seleziona immagine!", Toast.LENGTH_SHORT).show();
            } else {
                Glide.with(getActivity()).load(uri).into(imageProfile);
                imageUri = uri;
            }
        });
    }

    private void showChangePasswordDialog(View view) {
        ConstraintLayout changePasswordConstraintLayout = view.findViewById(R.id.changePasswordConstraintLayout);
        View viewChangePasswordDialog = LayoutInflater.from(getContext()).inflate(R.layout.change_password_dialog, changePasswordConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(viewChangePasswordDialog);
        final AlertDialog alertDialog = builder.create();

        TextInputLayout oldPasswordLayout = viewChangePasswordDialog.findViewById(R.id.oldPasswordLayout);
        TextView oldPassword = viewChangePasswordDialog.findViewById(R.id.inputOldPassword);

        TextInputLayout newPasswordLayout = viewChangePasswordDialog.findViewById(R.id.newPasswordLayout);
        TextView newPassword = viewChangePasswordDialog.findViewById(R.id.inputNewPassword);

        TextInputLayout confirmNewPasswordLayout = viewChangePasswordDialog.findViewById(R.id.confirmNewPasswordLayout);
        TextView confirmNewPassword = viewChangePasswordDialog.findViewById(R.id.inputConfirmNewPassword);

        Button changeOldPasswordBtn = viewChangePasswordDialog.findViewById(R.id.changePasswordBtn);
        changeOldPasswordBtn.setOnClickListener(v -> {

            boolean oldPasswordCorrect = checkOldPassword(oldPassword, oldPasswordLayout);
            boolean passwordCorrespond = checkPasswordsCorrespond(newPassword, confirmNewPassword, confirmNewPasswordLayout);
            boolean newPasswordMatchesRegex = checkNewPasswordMatchesRegex(newPassword, newPasswordLayout);

            if (!oldPasswordCorrect || !passwordCorrespond || !newPasswordMatchesRegex)
                return;

            UpdatePasswordDto updatePasswordDto = new UpdatePasswordDto(
                    oldPassword.getText().toString(),
                    newPassword.getText().toString()
            );

            DietiUserAPI dietiUserAPI = RetrofitService.getRetrofitInstance().create(DietiUserAPI.class);
            dietiUserAPI.updatePassword(LocalDietiUser.getLocalDietiUser(getContext()).getId(), updatePasswordDto).enqueue(new Callback<DietiUser>() {
                @Override
                public void onResponse(Call<DietiUser> call, Response<DietiUser> response) {
                    if (response.body() != null) {
                        oldPasswordLayout.setErrorEnabled(false);
                        updateLocalDietiUserPassword(response.body());
                        alertDialog.dismiss();
                        Toast.makeText(getContext(), "Password aggiornata!", Toast.LENGTH_SHORT).show();
                    } else {
                        showFailedUpdateDialog(view, "Password corrente errata!");
                        oldPasswordLayout.setError("Password errata!");
                    }
                }

                @Override
                public void onFailure(Call<DietiUser> call, Throwable t) {
                    alertDialog.dismiss();
                    NetworkUtility.showNetworkErrorToast(getContext());
                }
            });
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private boolean checkNewPasswordMatchesRegex(TextView newPassword, TextInputLayout newPasswordLayout) {

        if (!newPassword.getText().toString().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            newPasswordLayout.setError("Deve avere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale");
            return false;
        } else {
            newPasswordLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean checkPasswordsCorrespond(TextView newPassword, TextView confirmNewPassword, TextInputLayout confirmNewPasswordLayout) {
        if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
            confirmNewPasswordLayout.setError("Le password non corrispondono!");
            return false;
        } else {
            confirmNewPasswordLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean checkOldPassword(TextView oldPassword, TextInputLayout oldPasswordLayout) {
        if (oldPassword.getText().toString().trim().isEmpty()) {
            oldPasswordLayout.setError("Inserire la password corrente!");
            return false;
        } else {
            oldPasswordLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void updateLocalDietiUserPassword(DietiUser dietiUser) {
        LocalDietiUser.setLocalDietiUser(getActivity(), dietiUser);
    }

    private void showEditProfileDialog(View view) {
        ConstraintLayout editProfileConstraintLayout = view.findViewById(R.id.editProfileConstraintLayout);
        View viewEditProfileDialog = LayoutInflater.from(getContext()).inflate(R.layout.edit_profile_dialog, editProfileConstraintLayout);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(viewEditProfileDialog);
        final AlertDialog alertDialog = builder.create();

        Button changeImgBtn = viewEditProfileDialog.findViewById(R.id.changeImgBtn);
        imageProfile = viewEditProfileDialog.findViewById(R.id.imageProfile);

        TextInputLayout nameTextLayout = viewEditProfileDialog.findViewById(R.id.nameTextLayout);
        TextView name = viewEditProfileDialog.findViewById(R.id.inputName);

        TextInputLayout surnameTextLayout = viewEditProfileDialog.findViewById(R.id.surnameTextLayout);
        TextView surname = viewEditProfileDialog.findViewById(R.id.inputSurname);

        changeImgBtn.setOnClickListener(v ->
                singlePhotoPickerLauncher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );

        initializeEditTextEditProfileDialog(viewEditProfileDialog);

        Button editOldProfileBtn = viewEditProfileDialog.findViewById(R.id.editProfileBtn);
        editOldProfileBtn.setOnClickListener(v -> {
            if (!name.getText().toString().matches("^([a-zA-Z]{2,})")) {
                nameTextLayout.setError("Nome non valido");
                return;
            }
            nameTextLayout.setErrorEnabled(false);

            if (!surname.getText().toString().matches("^([a-zA-Z]+'?-?\\s?[a-zA-Z]{2,}\\s?([a-zA-Z]+))")) {
                surnameTextLayout.setError("Cognome non valido");
                return;
            }
            surnameTextLayout.setErrorEnabled(false);

            updateDietiUserData(alertDialog, view);
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private void updateDietiUserData(AlertDialog alertDialog, View view) {
        DietiUserAPI dietiUserAPI = RetrofitService.getRetrofitInstance().create(DietiUserAPI.class);
        dietiUserAPI.updateDietiUserDataById(LocalDietiUser.getLocalDietiUser(getContext()).getId(), getNewDietiUser()).enqueue(new Callback<DietiUser>() {
            @Override
            public void onResponse(Call<DietiUser> call, Response<DietiUser> response) {
                if (response.body() != null) {
                    if (imageUri != null)
                        uploadProfilePicture(response.body().getId());

                    updateLocalDietiUser();
                    setTextViewWithLocalDietiUserData();
                    alertDialog.dismiss();
                    refreshData();
                } else {
                    alertDialog.dismiss();
                    showFailedUpdateDialog(view, "Impossibile modficare i tuoi dati al momento, riprova più tardi.");
                }
            }

            @Override
            public void onFailure(Call<DietiUser> call, Throwable t) {
                alertDialog.dismiss();
                NetworkUtility.showNetworkErrorToast(getContext());
            }
        });
    }

    private void uploadProfilePicture(Long id) {
        File imageToBeUploaded = MyFileUtils.uriToFile(imageUri, getActivity());

        imageToBeUploaded = MyFileUtils.compressImage(imageToBeUploaded, getActivity());

        if (imageToBeUploaded == null)
            return;

        RequestBody requestBody = RequestBody.create(imageToBeUploaded, MediaType.parse("image/*"));
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", id + ".jpeg", requestBody);

        DietiUserAPI dietiUserAPI = RetrofitService.getRetrofitInstance().create(DietiUserAPI.class);
        dietiUserAPI.updateProfilePicture(id, imagePart).enqueue(new Callback<DietiUser>() {
            @Override
            public void onResponse(Call<DietiUser> call, Response<DietiUser> response) {
                Toast.makeText(getActivity(), "Utente aggiornato!", Toast.LENGTH_SHORT).show();
                if (response.body() != null)
                    LocalDietiUser.setLocalDietiUser(getActivity(), response.body());
            }

            @Override
            public void onFailure(Call<DietiUser> call, Throwable t) {
                NetworkUtility.showNetworkErrorToast(getContext());
            }
        });
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

                        bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions = requestOptions.transform(new CenterCrop());

                        Glide.with(getActivity())
                                .load(bitmap)
                                .apply(requestOptions)
                                .into(profilePicture);

                        profilePictureProgressBar.setVisibility(View.GONE);
                    }
                } catch (IOException e) {
                    profilePictureProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Impossibile caricare l'immagine'!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                profilePictureProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getContext());
            }
        });
    }

    private void refreshData() {
        getParentFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment(), FragmentTagEnum.PROFILE.toString()).addToBackStack(FragmentTagEnum.PROFILE.toString()).commit();
    }

    private void showFailedUpdateDialog(View view, String message) {
        ConstraintLayout failedDataUpdateConstraintLayout = view.findViewById(R.id.failedUpdateConstraintLayout);
        View viewFailedDataUpdate = LayoutInflater.from(getContext()).inflate(R.layout.failed_update_dialog, failedDataUpdateConstraintLayout);

        Button closePopupBtn = viewFailedDataUpdate.findViewById(R.id.closePopupBtn);

        TextView errorText = viewFailedDataUpdate.findViewById(R.id.failedUpdateText);
        errorText.setText(message);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setView(viewFailedDataUpdate);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        closePopupBtn.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    /**
     * This method sets the various TextViews in the fragment with the user's local data. If some data is missing, a message "Complete your profile!" is displayed.
     */
    private void setTextViewWithLocalDietiUserData() {
        userFullNameText.setText(String.format("%s %s", localDietiUser.getName(), localDietiUser.getSurname()));
        userEmailText.setText(localDietiUser.getEmail());

        if (!localDietiUser.getProfilePictureUrl().isEmpty()) {
            Log.i("IMAGE", localDietiUser.getProfilePictureUrl());
            requestProfilePicture(localDietiUser.getProfilePictureUrl());
        }

        if (localDietiUser.getGeographicalArea().isEmpty() || localDietiUser.getBiography().isEmpty() || localDietiUser.getLinks().isEmpty()) {
            messageCompleteYourProfileText.setVisibility(View.VISIBLE);

            titleSectionBiography.setVisibility(View.GONE);
            titleSectionLinks.setVisibility(View.GONE);

            geographicalAreaText.setVisibility(View.GONE);
            biographyText.setVisibility(View.GONE);
            linksText.setVisibility(View.GONE);
        } else {
            messageCompleteYourProfileText.setVisibility(View.GONE);

            geographicalAreaText.setText(localDietiUser.getGeographicalArea());
            biographyText.setText(localDietiUser.getBiography());

            StringBuilder links = new StringBuilder();
            for (String link : localDietiUser.getLinks()) {
                links.append(link);
            }
            linksText.setText(removeSquareBrackets(links.toString()));
        }
    }

    /**
     * This method fills the EditTexts for profile editing with the user's local data
     *
     * @param viewEditProfileDialog reference to the EditProfileDialog
     */
    private void initializeEditTextEditProfileDialog(View viewEditProfileDialog) {
        requestProfilePicture(localDietiUser.getProfilePictureUrl());

        if (bitmap != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop());

            Glide.with(getActivity())
                    .load(bitmap)
                    .apply(requestOptions)
                    .into(imageProfile);
        }

        inputNameEditText = viewEditProfileDialog.findViewById(R.id.inputName);
        inputNameEditText.setText(localDietiUser.getName());

        inputSurnameEditText = viewEditProfileDialog.findViewById(R.id.inputSurname);
        inputSurnameEditText.setText(localDietiUser.getSurname());

        inputGeographicalAreaEditText = viewEditProfileDialog.findViewById(R.id.inputGeographicalArea);
        if (!localDietiUser.getGeographicalArea().isEmpty())
            inputGeographicalAreaEditText.setText(localDietiUser.getGeographicalArea());

        inputBiographyEditText = viewEditProfileDialog.findViewById(R.id.inputBiography);
        if (!localDietiUser.getBiography().isEmpty())
            inputBiographyEditText.setText(localDietiUser.getBiography());

        inputLinksEditText = viewEditProfileDialog.findViewById(R.id.inputLinks);
        if (!localDietiUser.getLinks().isEmpty())
            inputLinksEditText.setText(removeSquareBrackets(localDietiUser.getLinks().toString()));
    }

    /**
     * This method returns a DietiUser object with the updated data
     */
    private DietiUser getNewDietiUser() {
        String name = inputNameEditText.getText().toString();
        String surname = inputSurnameEditText.getText().toString();
        String biography = inputBiographyEditText.getText().toString();
        String linksStr = inputLinksEditText.getText().toString();
        List<String> links = Arrays.asList(linksStr.split(","));
        String geographicalArea = inputGeographicalAreaEditText.getText().toString();
        String profilePictureUrl = localDietiUser.getProfilePictureUrl();

        return new DietiUser(localDietiUser.getId(), name, surname, localDietiUser.getEmail(), biography, links, geographicalArea, profilePictureUrl);
    }

    /**
     * This method updates the local Dieti User
     */
    private void updateLocalDietiUser() {
        LocalDietiUser.setLocalDietiUser(getActivity(), getNewDietiUser());
    }

    /**
     * This method remove square brackets when printing an array
     */
    private String removeSquareBrackets(String str) {
        return str.replaceAll("[\\[\\]]", "");
    }
}