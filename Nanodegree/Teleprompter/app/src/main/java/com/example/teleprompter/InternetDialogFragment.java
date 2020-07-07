package com.example.teleprompter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InternetDialogFragment extends DialogFragment {
    private RequestInternetDialogFragmentCallbacks callbacks;

    public interface RequestInternetDialogFragmentCallbacks {
        void onAccept(DialogInterface dialogInterface);

        void onDeny(DialogInterface dialogInterface);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.request_internet_message))
                .setTitle(getString(R.string.request_internet))
                .setPositiveButton(R.string.dialog_positive, (dialog, id) -> callbacks.onAccept(dialog))
                .setNegativeButton(R.string.dialog_negative, (dialog, id) -> callbacks.onDeny(dialog));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginActivity) {
            LoginActivity activity = (LoginActivity) context;
            try {
                callbacks = activity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (context instanceof SignupActivity) {
            SignupActivity activity = (SignupActivity) context;
            try {
                callbacks = activity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            try {
                callbacks = activity;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}