package com.example.teleprompter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayDocumentActivity extends AppCompatActivity implements ScrollView.ScrollViewListener {
    private static final boolean AUTO_HIDE = true;
    public static final String PARCELABLE_KEY = "parcel-data-key";

    private final Handler handler = new Handler();
    private View view;
    private View controlView;
    private boolean isVisible;
    private int delayMillis;
    private int scrollOffset;
    private Handler animationHandler;
    private AnimationRunnable animationRunnable;

    private final Runnable showRunnablePart = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.show();
            controlView.setVisibility(View.VISIBLE);
        }
    };

    private final Runnable hideRunnablePart = new Runnable() {
        @Override
        public void run() {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable hideRunnable = this::hide;

    private final View.OnTouchListener delayTouchListener = (view, motionEvent) -> {
        view.performClick();
        if (AUTO_HIDE) delayedHide(3000);
        return false;
    };

    @BindView(R.id.slide_show_bg)
    FrameLayout slideShowBackgroundView;
    @BindView(R.id.control_container)
    LinearLayout scrollContainer;
    @BindView(R.id.slideshow_scroller)
    ScrollView scrollView;
    @BindView(R.id.fullscreen_content)
    TextView contentView;
    @BindView(R.id.countdown_view)
    FrameLayout countdownView;
    @BindView(R.id.countdown_text)
    TextView countdownText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.slideshow_play)
    Button playButton;
    @BindView(R.id.slideshow_pause)
    Button pauseButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_document);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        playButton.setVisibility(View.VISIBLE);

        isVisible = true;

        Spec spec = null;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PARCELABLE_KEY))
            spec = intent.getParcelableExtra(PARCELABLE_KEY);

        view = findViewById(R.id.fullscreen_content);
        controlView = findViewById(R.id.fullscreen_content_controls);

        view.setOnClickListener(view -> toggle());

        scrollContainer.setOnTouchListener((view, motionEvent) -> {
            toggle();
            return true;
        });

        findViewById(R.id.slideshow_play).setOnTouchListener(delayTouchListener);
        playButton.setOnClickListener(view -> start());
        pauseButton.setOnClickListener(view -> stop());

        scrollView.setScrollViewListener(this);

        if (spec != null) {
            String content = spec.getContent();
            contentView.setText(content);
            contentView.setTextColor(spec.getFontColor());
            setFontSize(spec.getFontSize());
            slideShowBackgroundView.setBackgroundColor(spec.getBackgroundColor());
            setSpeed(spec.getScrollSpeed());
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        controlView.setVisibility(View.GONE);
        isVisible = false;
        handler.removeCallbacks(showRunnablePart);
        handler.postDelayed(hideRunnablePart, 300);
    }

    private void delayedHide(int delayMillis) {
        handler.removeCallbacks(hideRunnable);
        handler.postDelayed(hideRunnable, delayMillis);
    }

    private void show() {
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        isVisible = true;
        handler.removeCallbacks(hideRunnablePart);
        handler.postDelayed(showRunnablePart, 300);
    }

    private void toggle() {
        if (!isVisible) show();
        else hide();
    }

    private void start() {

        int y = scrollView.getScrollY();
        scrollView.setScrollable(false);
        animationHandler = new Handler();
        animationRunnable = new AnimationRunnable(y);
        animationHandler.postDelayed(animationRunnable, 6000);
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
    }

    private void stop() {
        System.out.println("********************STOPPED");
        animationHandler.removeCallbacks(animationRunnable);
        scrollView.setScrollable(true);
        pauseButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id != android.R.id.home)
            return super.onOptionsItemSelected(item);
        else {
            onBackPressed();
            return true;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationHandler != null) animationHandler.removeCallbacks(animationRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        countdownView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new CountdownRunnable(), 2000);
    }

    private void setFontSize(int fontSize) {
        int size = 16;
        if (fontSize == 0) size = R.integer.font_size_small;
        else if (fontSize == 1) size = R.integer.font_size_medium;
        else if (fontSize == 2) size = R.integer.font_size_large;
        else if (fontSize == 3) size = R.integer.font_size_extra_large;
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(size));
    }

    private void setSpeed(int scrollSpeed) {
        if (scrollSpeed == 0) {
            delayMillis = 25;
            scrollOffset = 1;
        } else if (scrollSpeed == 1) {
            delayMillis = 30;
            scrollOffset = 2;
        } else if (scrollSpeed == 2) {
            delayMillis = 30;
            scrollOffset = 3;
        } else if (scrollSpeed == 3) {
            delayMillis = 25;
            scrollOffset = 3;
        } else if (scrollSpeed == 4) {
            delayMillis = 30;
            scrollOffset = 4;
        }
    }

    private class AnimationRunnable implements Runnable {
        private int destination;

        AnimationRunnable(int scrollTo) {
            destination = scrollTo;
        }

        @Override
        public void run() {
            scrollView.smoothScrollTo(0, destination);
            animationHandler = new Handler();
            animationRunnable = new AnimationRunnable(destination + scrollOffset);
            animationHandler.postDelayed(animationRunnable, delayMillis);
        }
    }

    private class CountdownRunnable implements Runnable {
        @Override
        public void run() {
            int count = Integer.parseInt(countdownText.getText().toString());
            if (count <= 1) {
                countdownText.setText(getString(R.string.countdown_3));
                countdownView.setVisibility(View.GONE);
                start();
            } else {
                count--;
                countdownText.setText(Integer.toString(count));
                new Handler().postDelayed(new CountdownRunnable(), 1000);
            }
        }
    }

    @Override
    public void onScrollChanged(ScrollView scrollView, int x1, int y1, int x2, int y2) {
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        if (diff <= 40) if (animationHandler != null) stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}