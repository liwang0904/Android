package com.example.teleprompter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity implements InternetDialogFragment.RequestInternetDialogFragmentCallbacks {
    private static final String REQUEST_INTERNET_DIALOG = "request_internet_dialog";
    private FirebaseAuth auth;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.signup_container)
    LinearLayout layoutContainer;
    @BindView(R.id.name)
    EditText nameInput;
    @BindView(R.id.email)
    EditText emailInput;
    @BindView(R.id.password)
    EditText passwordInput;
    @BindView(R.id.confirm_password)
    EditText confirmPasswordInput;
    @BindView(R.id.signup_button)
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.signup_button)
    protected void initiateSignup() {
        signupButton.setEnabled(false);
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (name.trim().isEmpty()) {
            nameInput.setError(getString(R.string.field_required));
            cancel = true;
            focusView = nameInput;
        }
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.field_required));
            cancel = true;
            focusView = emailInput;
        }
        if (TextUtils.isEmpty(password) && !Utils.isValidPassword(password)) {
            passwordInput.setError(getString(R.string.invalid_password));
            cancel = true;
            focusView = passwordInput;
        }
        if (TextUtils.isEmpty(confirmPassword) && !Utils.isValidPassword(confirmPassword)) {
            confirmPasswordInput.setError(getString(R.string.invalid_password));
            cancel = true;
            focusView = confirmPasswordInput;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError(getString(R.string.not_matching));
            passwordInput.setError(getString(R.string.not_matching));
            cancel = true;
            focusView = confirmPasswordInput;
        }
        if (name.length() > 60) {
            nameInput.setError(getString(R.string.long_name));
            cancel = true;
            focusView = nameInput;
        }
        if (!Utils.isValidEmail(email)) {
            emailInput.setError(getString(R.string.invalid_email));
            cancel = true;
            focusView = emailInput;
        }

        if (!cancel) {
            if (!Utils.isConnected(this)) {
                InternetDialogFragment dialogFragment = new InternetDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), REQUEST_INTERNET_DIALOG);
                signupButton.setEnabled(true);
            } else {
                showProgressBar(true);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                updateUI(user);
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                    Snackbar.make(findViewById(R.id.signup_container), getString(R.string.account_exists), Snackbar.LENGTH_LONG).show();
                                else
                                    Snackbar.make(findViewById(R.id.signup_container), getString(R.string.authentication_failed), Snackbar.LENGTH_LONG).show();
                                updateUI(null);
                            }
                        });
            }
        } else {
            focusView.requestFocus();
            signupButton.setEnabled(true);
        }
    }

    private void showProgressBar(boolean show) {
        layoutContainer.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            final String name = nameInput.getText().toString();
            UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            user.updateProfile(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    SharedPreferenceUtils.setUsername(SignupActivity.this, user.getDisplayName());
                    SharedPreferenceUtils.setUserId(SignupActivity.this, user.getUid());
                    SharedPreferenceUtils.setEmail(SignupActivity.this, user.getEmail());
                    DocumentService.syncStartup(SignupActivity.this, user.getUid());

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);
                }
            });
        } else {
            signupButton.setEnabled(true);
            showProgressBar(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAccept(DialogInterface dialogInterface) {
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
    }

    @Override
    public void onDeny(DialogInterface dialogInterface) {
    }
}