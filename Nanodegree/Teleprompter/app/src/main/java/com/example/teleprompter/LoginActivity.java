package com.example.teleprompter;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, InternetDialogFragment.RequestInternetDialogFragmentCallbacks, ResetPasswordDialogFragment.ForgotPasswordDialogCallbacks {
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int SIGN_IN = 101;
    private static final String FORGOT_PASSWORD = "forgot-password";
    private static final String REQUEST_INTERNET_DIALOG = "request_internet_dialog";

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @BindView(R.id.email)
    AutoCompleteTextView email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.progress)
    View progressView;

    @BindView(R.id.login_form)
    View loginForm;

    @BindView(R.id.login_container)
    LinearLayout loginContainer;

    @BindView(R.id.sign_in_button)
    Button mEmailSignInButton;

    @BindView(R.id.google_sign_in_button)
    SignInButton googleSignInButton;

    @BindView(R.id.forgot_password_text)
    TextView forgotPasswordText;

    @BindView(R.id.sign_up_button)
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().requestId().requestIdToken(getString(R.string.webclient_server_id)).build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        password.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_NULL) {
                sign_in();
                return true;
            }
            return false;
        });
    }

    @OnClick(R.id.sign_in_button)
    protected void sign_in() {
        email.setError(null);
        password.setError(null);

        if (!Utils.isConnected(this)) {
            showRequestInternetDialog();
            return;
        }

        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(emailString)) {
            email.setError(getString(R.string.field_required));
            cancel = true;
            focusView = email;
        } else if (!Utils.isValidEmail(emailString)) {
            email.setError(getString(R.string.invalid_email));
            cancel = true;
            focusView = email;
        }

        if (TextUtils.isEmpty(passwordString) && Utils.isValidPassword(passwordString)) {
            password.setError(getString(R.string.invalid_password));
            cancel = true;
            focusView = password;
        }

        if (cancel) focusView.requestFocus();
        else {
            showProgress(true);
            auth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, task -> {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseAuthInvalidUserException)
                        updateUI(null, getString(R.string.account_not_exists));
                    else if (e instanceof FirebaseAuthInvalidCredentialsException)
                        updateUI(null, getString(R.string.incorrect_password));
                    else updateUI(null, getString(R.string.failed));
                } else {
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user, null);
                }
            });
        }
    }

    private void showRequestInternetDialog() {
        InternetDialogFragment fragment = new InternetDialogFragment();
        fragment.show(getSupportFragmentManager(), REQUEST_INTERNET_DIALOG);
    }

    private void showProgress(final boolean show) {
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateUI(FirebaseUser user, String message) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String userId = user.getUid();
            SharedPreferenceUtils.setUsername(this, name);
            SharedPreferenceUtils.setEmail(this, email);
            SharedPreferenceUtils.setUserId(this, userId);

            DocumentService.syncStartup(this, userId);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (message != null) {
            showProgress(false);
            Snackbar.make(loginContainer, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.google_sign_in_button)
    protected void handleGoogleSignInButtonClick() {
        if (!Utils.isConnected(this)) showRequestInternetDialog();
        else startActivityForResult(googleSignInClient.getSignInIntent(), SIGN_IN);
    }

    @OnClick(R.id.sign_up_button)
    protected void handleSignUpButtonClick() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.forgot_password_text)
    protected void handleForgotPasswordClick() {
        if (!Utils.isConnected(this)) showRequestInternetDialog();
        ResetPasswordDialogFragment fragment = new ResetPasswordDialogFragment();
        fragment.show(getSupportFragmentManager(), FORGOT_PASSWORD);
    }

    @Override
    public void onSendForgotPasswordEmailClicked(DialogInterface dialogInterface, String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        if (!(task.getException() instanceof FirebaseAuthInvalidUserException))
                            updateUI(null, getString(R.string.failed));
                        else updateUI(null, getString(R.string.email_not_found));
                    } else updateUI(null, getString(R.string.email_sent));
                });
    }

    private interface ProfileQuery {
        int ADDRESS = 0;
        String[] PROJECTION = {ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY,};
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emails);
        email.setAdapter(adapter);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onAccept(DialogInterface dialogInterface) {
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
    }

    @Override
    public void onDeny(DialogInterface dialogInterface) {

    }

    private boolean requestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) return true;
        if (!shouldShowRequestPermissionRationale(READ_CONTACTS))
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        else
            Snackbar.make(email, R.string.permission_message, Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!requestContacts()) return;
                getLoaderManager().initLoader(0, null, this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                googleSignInResult(task);
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void googleSignInResult(Task<GoogleSignInAccount> task) {
        showProgress(true);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task1 -> {
                        if (!task1.isSuccessful())
                            updateUI(null, getString(R.string.authentication_failed));
                        else {
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user, null);
                        }
                    });
        } catch (ApiException e) {
            updateUI(null, getString(R.string.check_google_account));
        }
    }
}