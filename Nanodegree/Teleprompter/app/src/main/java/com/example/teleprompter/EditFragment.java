package com.example.teleprompter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, DeleteDialogFragment.DeleteConfirmationDialogFragmentCallbacks, ColorDialogFragment.ColorDialogCallbacks {
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String TEXT_COLOR = "text-color";
    private static final String BACKGROUND_COLOR = "background-color";
    private static final String DELETE_CONFIRMATION_DIALOG = "tag-delete-dialog";

    private String title;
    private String text;
    private int textColor;
    private int backgroundColor;
    private int scrollSpeed;
    private int fontSize;
    String userId;
    private boolean orientationChanged = false;
    private Document document = null;

    @BindView(R.id.doc_detail_title)
    EditText titleText;
    @BindView(R.id.doc_detail_text)
    EditText textBody;
    @BindView(R.id.text_color_picker_view)
    ColorPicker textColorPicker;
    @BindView(R.id.background_color_picker_view)
    ColorPicker backgroundColorPicker;
    @BindView(R.id.seekBar_speed_display)
    TextView speedBarTextView;
    @BindView(R.id.seekBar_speed)
    SeekBar scrollSpeedBar;
    @BindView(R.id.font_size_display)
    TextView fontSizeTextView;
    @BindView(R.id.seekBar_font_size)
    SeekBar fontSizeBar;
    @BindView(R.id.play_button)
    ImageButton playButton;

    public EditFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle.containsKey(EditActivity.PARCEL_KEY)) {
            document = bundle.getParcelable(EditActivity.PARCEL_KEY);
            title = document.getTitle();
            text = document.getText();
        }

        textColor = SharedPreferenceUtils.getDefaultTextColor(getContext());
        backgroundColor = SharedPreferenceUtils.getDefaultBackgroundColor(getContext());
        scrollSpeed = SharedPreferenceUtils.getDefaultScrollSpeed(getContext());
        fontSize = SharedPreferenceUtils.getDefaultFontSize(getContext());
        userId = SharedPreferenceUtils.getUserId(getContext());

        if (savedInstanceState != null) {
            orientationChanged = true;
            title = savedInstanceState.getString(TITLE);
            text = savedInstanceState.getString(TEXT);
            textColor = savedInstanceState.getInt(TEXT_COLOR);
            backgroundColor = savedInstanceState.getInt(BACKGROUND_COLOR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_fragment, container, false);
        ButterKnife.bind(this, view);

        titleText.setText(document.getTitle());
        if (savedInstanceState == null)
            if (document.isNew()) titleText.requestFocus();

        textBody.setText(document.getText());
        textColorPicker.setBackgroundColor(textColor);
        backgroundColorPicker.setBackgroundColor(backgroundColor);
        scrollSpeedBar.setProgress(scrollSpeed);
        fontSizeBar.setProgress(fontSize);
        speedBarTextView.setText(Integer.toString(scrollSpeed));
        fontSizeTextView.setText(getFontSizeFromProgress(fontSize));
        scrollSpeedBar.setOnSeekBarChangeListener(this);
        fontSizeBar.setOnSeekBarChangeListener(this);

        setScrollSpeed();
        setFontSize();
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }
        });
        return view;
    }

    private void displayDialog(int color, int pickerType) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = (color) & 0xFF;

        Bundle bundle = new Bundle();
        bundle.putInt(ColorDialogFragment.RED_VALUE, red);
        bundle.putInt(ColorDialogFragment.GREEN_VALUE, green);
        bundle.putInt(ColorDialogFragment.BLUE_VALUE, blue);
        bundle.putInt(ColorDialogFragment.ARGUMENTS_KEY, pickerType);
        ColorDialogFragment fragment = new ColorDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), "ColorPicker");
    }

    @OnClick(R.id.background_color_picker_view)
    protected void onBackgroundColorPickerClicked() {
        displayDialog(backgroundColor, ColorDialogFragment.BACKGROUND_COLOR_PICKER);
    }

    @OnClick(R.id.text_color_picker_view)
    protected void onTextColorPickerClicked() {
        displayDialog(textColor, ColorDialogFragment.TEXT_COLOR_PICKER);
    }

    @OnClick(R.id.play_button)
    public void onPlayButtonClick() {
        Spec spec = new Spec();
        spec.setTitle(titleText.getText().toString());
        spec.setContent(textBody.getText().toString());
        spec.setBackgroundColor(backgroundColor);
        spec.setFontColor(textColor);
        spec.setFontSize(fontSize);
        spec.setScrollSpeed(scrollSpeed);

        Intent intent = new Intent(getContext(), PlayDocumentActivity.class);
        intent.putExtra(PlayDocumentActivity.PARCELABLE_KEY, spec);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE, titleText.getText().toString());
        outState.putString(TEXT, textBody.getText().toString());
        outState.putInt(TEXT_COLOR, textColor);
        outState.putInt(BACKGROUND_COLOR, backgroundColor);
    }

    @Override
    public void onPositiveButtonClick(DialogInterface dialogInterface, int colorId, int pickerType) {
        if (pickerType != ColorDialogFragment.BACKGROUND_COLOR_PICKER) {
            textColor = colorId;
            textColorPicker.setBackgroundColor(textColor);
        } else {
            backgroundColor = colorId;
            backgroundColorPicker.setBackgroundColor(backgroundColor);
        }
    }

    @Override
    public void onNeutralButtonClick(DialogInterface dialogInterface, int colorId, int pickerType) {
        onPositiveButtonClick(dialogInterface, colorId, pickerType);
        if (pickerType != ColorDialogFragment.BACKGROUND_COLOR_PICKER)
            SharedPreferenceUtils.setDefaultTextColor(getContext(), colorId);
        else SharedPreferenceUtils.setDefaultBackgroundColor(getContext(), colorId);
    }

    @Override
    public void onNegativeButtonClick(DialogInterface dialogInterface) {
    }

    private String getFontSizeFromProgress(int fontSize) {
        if (fontSize == 1) return "M";
        else if (fontSize == 2) return "L";
        else if (fontSize == 3) return "XL";
        return "S";
    }

    private void setFontSize() {
        fontSizeBar.setProgress(fontSize);
        fontSizeTextView.setText(getFontSizeFromProgress(fontSize));
    }

    private void setScrollSpeed() {
        scrollSpeedBar.setProgress(scrollSpeed);
        speedBarTextView.setText(Integer.toString(scrollSpeed + 1));
    }

    private void updateSeekbarValues() {
        fontSize = fontSizeBar.getProgress();
        setFontSize();
        scrollSpeed = scrollSpeedBar.getProgress();
        setScrollSpeed();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        updateSeekbarValues();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!document.isNew()) persistOldDocument();
        else persistNewDocument();
    }

    private void persistNewDocument() {
        String titleString = titleText.getText().toString();
        String textString = textBody.getText().toString();
        if (title.equals(titleString) && text.equals(textString)) return;

        if (!titleString.trim().isEmpty() || !textString.trim().isEmpty()) {
            if (!orientationChanged) {
                document.setTitle(titleString);
                document.setText(textString);
                document.setUserId(SharedPreferenceUtils.getUserId(getContext()));
                DocumentService.insert(getContext(), document);
            } else {
                document.setId(SharedPreferenceUtils.getLastStoredId(getContext()));
                document.setCloudId(SharedPreferenceUtils.getLastStoredCloudId(getContext()));
                DocumentService.update(getContext(), document);
            }
        }

        if (titleString.trim().isEmpty() && textString.trim().isEmpty() && orientationChanged) {
            document.setId(SharedPreferenceUtils.getLastStoredId(getContext()));
            document.setCloudId(SharedPreferenceUtils.getLastStoredCloudId(getContext()));
            DocumentService.delete(getContext(), document);
            return;
        }

        text = textString;
        title = titleString;
    }

    private void persistOldDocument() {
        String titleString = titleText.getText().toString();
        String textString = textBody.getText().toString();

        if (!title.equals(titleString) || !text.equals(textString)) {
            document.setText(textString);
            document.setTitle(titleString);
            DocumentService.update(getContext(), document);
        }
        title = titleString;
        text = textString;
    }

    public void delete() {
        Bundle bundle = new Bundle();
        DeleteDialogFragment fragment = new DeleteDialogFragment();
        bundle.putString(DeleteDialogFragment.EXTRA_DOCUMENT_NAME, document.getTitle());
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), DELETE_CONFIRMATION_DIALOG);
    }

    public void share() {
        String type = "text/plain";
        String title = document.getTitle();
        String content = document.getTitle() + "\n\n" + document.getText();
        ShareCompat.IntentBuilder.from(getActivity()).setType(type).setChooserTitle(title).setText(content).startChooser();
    }

    @Override
    public void onConfirm(DialogInterface dialogInterface) {
        if (!document.isNew()) DocumentService.delete(getContext(), document);
        else {
            if (orientationChanged) {
                document.setId(SharedPreferenceUtils.getLastStoredId(getContext()));
                document.setCloudId(SharedPreferenceUtils.getLastStoredCloudId(getContext()));
                DocumentService.delete(getContext(), document);
            }
        }
        getActivity().finish();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
    }
}