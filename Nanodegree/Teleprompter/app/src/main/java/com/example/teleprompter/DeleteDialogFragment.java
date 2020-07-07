package com.example.teleprompter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DeleteDialogFragment extends DialogFragment {
    public static final String EXTRA_DOCUMENT_NAME = "document-name";
    private DeleteConfirmationDialogFragmentCallbacks callbacks;

    public interface DeleteConfirmationDialogFragmentCallbacks {
        void onConfirm(DialogInterface dialogInterface);

        void onCancel(DialogInterface dialogInterface);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        String name = "";
        if (bundle != null) name = bundle.getString(EXTRA_DOCUMENT_NAME);

        builder.setMessage(getString(R.string.delete_confirm_message, name)).setTitle(getString(R.string.delete_confirm))
                .setPositiveButton(R.string.dialog_positive, (dialog, id) -> callbacks.onConfirm(dialog))
                .setNegativeButton(R.string.dialog_negative, (dialog, id) -> callbacks.onCancel(dialog));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EditActivity) {
            EditActivity activity = (EditActivity) context;
            try {
                callbacks = (EditFragment) activity.getSupportFragmentManager().findFragmentByTag(EditActivity.EDIT_FRAGMENT_TAG);
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