package com.example.teleprompter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordDialogFragment extends DialogFragment {
    private ForgotPasswordDialogCallbacks callbacks;

    @BindView(R.id.email_edit)
    EditText forgotPasswordEmail;

    public interface ForgotPasswordDialogCallbacks {
        void onSendForgotPasswordEmailClicked(DialogInterface dialogInterface, String email);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LoginActivity activity = null;
        if (context instanceof LoginActivity) activity = (LoginActivity) context;
        try {
            callbacks = activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.reset_password_dialog, null);
        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view).setTitle(getString(R.string.forgot_password)).setPositiveButton(R.string.send, null).setNegativeButton(R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            Button button = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                String email = forgotPasswordEmail.getText().toString();
                if (!Utils.isValidEmail(email)) {
                    forgotPasswordEmail.setError(getString(R.string.invalid_email));
                    forgotPasswordEmail.requestFocus();
                } else {
                    if (!Utils.isConnected(getContext())) {
                        forgotPasswordEmail.setError(getString(R.string.request_internet));
                        forgotPasswordEmail.requestFocus();
                        return;
                    }
                    callbacks.onSendForgotPasswordEmailClicked(alertDialog, email);
                    alertDialog.dismiss();
                }
            });
        });
        return alertDialog;
    }
}