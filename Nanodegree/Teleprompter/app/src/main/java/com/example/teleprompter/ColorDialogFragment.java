package com.example.teleprompter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ColorDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
    public static final String RED_VALUE = "red";
    public static final String GREEN_VALUE = "green";
    public static final String BLUE_VALUE = "blue";
    public static final int BACKGROUND_COLOR_PICKER = 100;
    public static final int TEXT_COLOR_PICKER = 200;
    public static final String ARGUMENTS_KEY = "arguments-key";
    private ColorDialogCallbacks callbacks;

    private int red;
    private int green;
    private int blue;
    private int colorId;
    private int pickerType;

    @BindView(R.id.color)
    ColorPicker colorPicker;

    @BindView(R.id.red)
    SeekBar redBar;

    @BindView(R.id.green)
    SeekBar greenBar;

    @BindView(R.id.blue)
    SeekBar blueBar;

    public interface ColorDialogCallbacks {
        void onPositiveButtonClick(DialogInterface dialogInterface, int colorId, int pickerType);

        void onNeutralButtonClick(DialogInterface dialogInterface, int colorId, int pickerType);

        void onNegativeButtonClick(DialogInterface dialogInterface);
    }

    private void colorToView() {
        int red = redBar.getProgress();
        int green = greenBar.getProgress();
        int blue = blueBar.getProgress();
        colorId = Color.rgb(red, green, blue);
        colorPicker.setColor(colorId);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        colorToView();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.color_picker, null);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARGUMENTS_KEY)) {
            red = bundle.getInt(RED_VALUE);
            green = bundle.getInt(GREEN_VALUE);
            blue = bundle.getInt(BLUE_VALUE);
            pickerType = bundle.getInt(ARGUMENTS_KEY);
        }

        redBar.setOnSeekBarChangeListener(this);
        greenBar.setOnSeekBarChangeListener(this);
        blueBar.setOnSeekBarChangeListener(this);
        redBar.setProgress(red);
        greenBar.setProgress(green);
        blueBar.setProgress(blue);

        if (savedInstanceState == null) colorToView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.select, (dialogInterface, i) ->
                        callbacks.onPositiveButtonClick(dialogInterface, colorId, pickerType))
                .setNeutralButton(R.string.set_default, (dialogInterface, i) ->
                        callbacks.onNeutralButtonClick(dialogInterface, colorId, pickerType))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                        callbacks.onNegativeButtonClick(dialogInterface));
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = null;
        if (context instanceof Activity) activity = (Activity) context;
        try {
            EditActivity editActivity = (EditActivity) activity;
            callbacks = (EditFragment) editActivity.getSupportFragmentManager().findFragmentByTag(EditActivity.EDIT_FRAGMENT_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}