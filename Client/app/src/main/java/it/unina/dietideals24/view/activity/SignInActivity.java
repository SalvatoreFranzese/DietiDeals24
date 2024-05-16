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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;

import it.unina.dietideals24.R;
import it.unina.dietideals24.dto.RegisterDto;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.retrofit.RetrofitService;
import it.unina.dietideals24.retrofit.api.DietiUserAuthAPI;
import it.unina.dietideals24.utils.NetworkUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextInputLayout nameTextLayout;
    private TextInputLayout surnameTextLayout;
    private TextInputLayout emailTextLayout;
    private TextInputLayout passwordTextLayout;
    private TextInputLayout confirmPasswordTextLayout;
    private Button signInBtn;
    private ImageView backBtn;
    private ProgressBar signInProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initializeViews();

        backBtn.setOnClickListener(v -> finish());

        signInBtn.setOnClickListener(v -> {
            if (isNotEmptyEditText() && matchesRegex() && passwordCorrespond()) {
                signInProgressBar.setVisibility(View.VISIBLE);
                register();
            }
        });
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.inputName);
        surnameEditText = findViewById(R.id.inputSurname);
        emailEditText = findViewById(R.id.inputEmail);
        passwordEditText = findViewById(R.id.inputPassword);
        confirmPasswordEditText = findViewById(R.id.inputConfirmPassword);
        nameTextLayout = findViewById(R.id.nameTextLayout);
        surnameTextLayout = findViewById(R.id.surnameTextLayout);
        emailTextLayout = findViewById(R.id.emailTextLayout);
        passwordTextLayout = findViewById(R.id.passwordTextLayout);
        confirmPasswordTextLayout = findViewById(R.id.confirmPasswordTextLayout);

        signInBtn = findViewById(R.id.signInBtn);
        backBtn = findViewById(R.id.backBtn);

        signInProgressBar = findViewById(R.id.signInProgressBar);
        signInProgressBar.setVisibility(View.GONE);
    }

    /**
     * checks if password and confirmPassword correspond
     *
     * @return true if they correspond, false otherwise
     */
    private boolean passwordCorrespond() {
        String password = passwordEditText.getText().toString();
        String confirmedPassword = confirmPasswordEditText.getText().toString();

        if (password.equals(confirmedPassword))
            return true;
        else {
            confirmPasswordTextLayout.setError("Le password non corrispondo");
            return false;
        }
    }

    /**
     * checks if all the EditText match the assigned regex
     *
     * @return true if all the EditText match, false otherwise
     */
    private boolean matchesRegex() {
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean ret = true;

        if (!name.matches("^([a-zA-Z]{2,})")) {
            nameTextLayout.setError("Nome non valido");
            ret = false;
        } else {
            nameTextLayout.setErrorEnabled(false);
        }
        if (!surname.matches("^([a-zA-Z]+'?-?\\s?[a-zA-Z]{2,}\\s?([a-zA-Z]+))")) {
            surnameTextLayout.setError("Cognome non valido");
            ret = false;
        } else {
            surnameTextLayout.setErrorEnabled(false);
        }
        if (!email.matches("^[\\w\\-\\.]*[\\w\\.]\\@[\\w\\.]*[\\w\\-\\.]+[\\w\\-]+[\\w]\\.+[\\w]+[\\w $]")) {
            emailTextLayout.setError("Non è una mail valida");
            ret = false;
        } else {
            emailTextLayout.setErrorEnabled(false);
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            passwordTextLayout.setError("Deve avere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale");
            ret = false;
        } else {
            passwordTextLayout.setErrorEnabled(false);
        }

        return ret;
    }

    /**
     * checks if the all the fields have been field
     *
     * @return true if all fields are filled, false otherwise
     */
    private boolean isNotEmptyEditText() {
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean ret = true;

        String obligatoryFieldLabel = getResources().getString(R.string.obligatory_field_label);

        if (name.isEmpty()) {
            nameTextLayout.setError(obligatoryFieldLabel);
            ret = false;
        } else
            nameTextLayout.setErrorEnabled(false);

        if (surname.isEmpty()) {
            surnameTextLayout.setError(obligatoryFieldLabel);
            ret = false;
        } else
            surnameTextLayout.setErrorEnabled(false);

        if (email.isEmpty()) {
            emailTextLayout.setError(obligatoryFieldLabel);
            ret = false;
        } else
            emailTextLayout.setErrorEnabled(false);

        if (password.isEmpty()) {
            passwordTextLayout.setError(obligatoryFieldLabel);
            ret = false;
        } else
            passwordTextLayout.setErrorEnabled(false);

        if (confirmPassword.isEmpty()) {
            confirmPasswordTextLayout.setError(obligatoryFieldLabel);
            ret = false;
        } else
            confirmPasswordTextLayout.setErrorEnabled(false);

        return ret;
    }

    private void register() {
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        RegisterDto registerDto = new RegisterDto(name, surname, email, password);
        DietiUserAuthAPI dietiUserAuthAPI = RetrofitService.getRetrofitInstance().create(DietiUserAuthAPI.class);

        dietiUserAuthAPI.register(registerDto).enqueue(new Callback<DietiUser>() {
            @Override
            public void onResponse(Call<DietiUser> call, Response<DietiUser> response) {
                signInProgressBar.setVisibility(View.GONE);
                if (response.code() != 400) {
                    showSuccessSignInDialog();
                } else {
                    showFailedSignInEmailAlreadyRegisteredDialog(registerDto.getEmail());
                }
            }

            @Override
            public void onFailure(Call<DietiUser> call, Throwable t) {
                signInProgressBar.setVisibility(View.GONE);
                NetworkUtility.showNetworkErrorToast(getApplicationContext());
            }
        });
    }

    private void showSuccessSignInDialog() {
        ConstraintLayout successLoginConstraintLayout = findViewById(R.id.successLoginConstraintLayout);
        View viewSuccessSignInDialog = LayoutInflater.from(SignInActivity.this).inflate(R.layout.success_sign_in_dialog, successLoginConstraintLayout);

        Button backToLoginBtn = viewSuccessSignInDialog.findViewById(R.id.backToLoginBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setView(viewSuccessSignInDialog);
        final AlertDialog alertDialog = builder.create();

        backToLoginBtn.setOnClickListener(v -> {
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private void showFailedSignInEmailAlreadyRegisteredDialog(String emailAlreadyRegistered) {
        ConstraintLayout failedSignInEmailAlreadyRegisteredConstraintLayout = findViewById(R.id.failedSignInEmailAlreadyRegisteredConstraintLayout);
        View viewFailedSignInEmailAlreadyRegisteredDialog = LayoutInflater.from(SignInActivity.this).inflate(R.layout.failed_sign_in_email_already_registered_dialog, failedSignInEmailAlreadyRegisteredConstraintLayout);

        TextView messageTextView = viewFailedSignInEmailAlreadyRegisteredDialog.findViewById(R.id.messageText);

        String errorMessage = "L'email " + emailAlreadyRegistered + " è già registrata!\n Riprova con un'altra email.";
        messageTextView.setText(errorMessage);

        Button tryAgainBtn = viewFailedSignInEmailAlreadyRegisteredDialog.findViewById(R.id.tryAgainBtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        builder.setView(viewFailedSignInEmailAlreadyRegisteredDialog);
        final AlertDialog alertDialog = builder.create();

        tryAgainBtn.setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    private void clearEditText() {
        nameEditText.setText(null);
        surnameEditText.setText(null);
        emailEditText.setText(null);
        passwordEditText.setText(null);
        confirmPasswordEditText.setText(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        clearEditText();
    }
}