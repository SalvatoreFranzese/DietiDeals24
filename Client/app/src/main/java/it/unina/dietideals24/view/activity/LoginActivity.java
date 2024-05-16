package it.unina.dietideals24.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.work.WorkManager;

import com.google.android.material.textfield.TextInputLayout;

import it.unina.dietideals24.R;
import it.unina.dietideals24.dto.LoginDto;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.response.LoginResponse;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DietiUserAPI;
import it.unina.dietideals24.retrofit.api.DietiUserAuthAPI;
import it.unina.dietideals24.utils.NetworkUtility;
import it.unina.dietideals24.utils.localstorage.LocalDietiUser;
import it.unina.dietideals24.utils.localstorage.TokenManagement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextInputLayout emailTextLayout;
    private TextInputLayout passwordTextLayout;
    private Button loginBtn;
    private TextView signInTextView;
    private ProgressBar loginProgressBar;
    private TokenManagement tokenManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenManagement = TokenManagement.getInstance(getApplicationContext());
        checkIfUserLogged();

        initializeViews();

        loginBtn.setOnClickListener(v -> {
            if (isNotEmptyFields()) {
                loginProgressBar.setVisibility(View.VISIBLE);
                login();
            }
        });

        signInTextView.setOnClickListener(v -> {
            Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(signInActivity);
        });
    }

    private void checkIfUserLogged() {
        if (!TokenManagement.getToken().isEmpty() && !TokenManagement.isExpired()) {
            checkUserStillExists(LocalDietiUser.getLocalDietiUser(getApplicationContext()).getEmail());
        } else {
            logoutUser();
        }
    }

    private void checkUserStillExists(String email) {
        DietiUserAPI dietiUserAPI = RetrofitService.getRetrofitInstance().create(DietiUserAPI.class);
        dietiUserAPI.getUserByEmail(email).enqueue(new Callback<DietiUser>() {
            @Override
            public void onResponse(Call<DietiUser> call, Response<DietiUser> response) {
                if (response.body() == null)
                    logoutUser();
                else
                    openMainActivity();
            }

            @Override
            public void onFailure(Call<DietiUser> call, Throwable t) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        TokenManagement.deleteTokenData();
        LocalDietiUser.deleteLocalDietiUser(getApplicationContext());
        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("pushNotificationWorker");
    }

    private boolean isNotEmptyFields() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean ret = true;

        if (email.isEmpty()) {
            emailTextLayout.setError("Inserire una email");
            ret = false;
        } else
            emailTextLayout.setErrorEnabled(false);

        if (password.isEmpty()) {
            passwordTextLayout.setError("Inserire una password");
            ret = false;
        } else
            passwordTextLayout.setErrorEnabled(false);

        return ret;
    }

    private void login() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        LoginDto loginDto = new LoginDto(email, password);
        DietiUserAuthAPI dietiUserAuthAPI = RetrofitService.getRetrofitInstance().create(DietiUserAuthAPI.class);

        dietiUserAuthAPI.login(loginDto).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() != 401) {
                    loginProgressBar.setVisibility(View.GONE);
                    saveToken(response);
                    saveCurrentUser(response);
                    openMainActivity();
                } else {
                    loginProgressBar.setVisibility(View.GONE);
                    showFailedLoginDialog();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }

    private void saveCurrentUser(Response<LoginResponse> response) {
        LocalDietiUser.setLocalDietiUser(getApplicationContext(), response.body().getDietiUser());
    }

    private void saveToken(Response<LoginResponse> response) {
        tokenManagement.setToken(response.body().getToken(), response.body().getExpiresIn());
    }

    private void showFailedLoginDialog() {
        ConstraintLayout failedLoginConstraintLayout = findViewById(R.id.failedLoginConstraintLayout);
        View viewFailedLoginDialog = LayoutInflater.from(LoginActivity.this).inflate(R.layout.failed_login_dialog, failedLoginConstraintLayout);

        Button tryAgainBtn = viewFailedLoginDialog.findViewById(R.id.tryAgainBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(viewFailedLoginDialog);
        final AlertDialog alertDialog = builder.create();

        tryAgainBtn.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private void openMainActivity() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.inputEmail);
        emailTextLayout = findViewById(R.id.emailTextLayout);
        passwordEditText = findViewById(R.id.inputPassword);
        passwordTextLayout = findViewById(R.id.passwordTextLayout);

        loginBtn = findViewById(R.id.loginBtn);
        signInTextView = findViewById(R.id.signInBtn);

        loginProgressBar = findViewById(R.id.loginProgressBar);
        loginProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        emailEditText.setText(null);
        passwordEditText.setText(null);
    }
}