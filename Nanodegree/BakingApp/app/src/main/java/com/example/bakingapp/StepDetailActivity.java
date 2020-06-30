package com.example.bakingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

public class StepDetailActivity extends AppCompatActivity {
    private TextView description;

    private PlayerView playerView;
    private SimpleExoPlayer exoPlayer;
    private static final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

    private StepDetailActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        viewModel = ViewModelProviders.of(this).get(StepDetailActivityViewModel.class);
        Intent intent = getIntent();
        if (savedInstanceState == null && intent.hasExtra(getPackageName()) && intent.hasExtra("position")) {
            viewModel.setPosition(intent.getIntExtra("position", 0));
            viewModel.setRecipe((Recipe) Parcels.unwrap(intent.getParcelableExtra(getPackageName())));
        }
        description = findViewById(R.id.description_for_step_tv);
        FloatingActionButton changeButton = findViewById(R.id.change_step_btn);
        playerView = findViewById(R.id.video_view);
        ImageView fullScreenIcon = playerView.findViewById(R.id.exo_fullscreen_icon);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            fullScreenIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_expand));
            changeButton.show();
            ViewGroup.LayoutParams params = playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = 800;
            playerView.setLayoutParams(params);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fullScreenIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_shrink));
            changeButton.hide();
            ViewGroup.LayoutParams params = playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            playerView.setLayoutParams(params);
        }

        fullScreenIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StepDetailActivity.this.toggleFullscreen();
            }
        });

        changeButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.getPosition() >= viewModel.getRecipe().getSteps().size() - 1) {
                    viewModel.setPosition(0);
                    viewModel.setPreviousPosition(-1);
                } else {
                    viewModel.setPreviousPosition(viewModel.getPosition());
                    viewModel.setPosition(viewModel.getPreviousPosition() + 1);
                }
                StepDetailActivity.this.releasePlayer();
                StepDetailActivity.this.initPlayer();
            }
        }));
    }

    private void toggleFullscreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            viewModel.setPlayBackPosition(exoPlayer.getCurrentPosition());
            viewModel.setPlayWhenReady(false);
            viewModel.setIndex(exoPlayer.getCurrentWindowIndex());
            viewModel.setPreviousPosition(viewModel.getPreviousPosition());
            viewModel.setPosition(viewModel.getPosition());
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void initPlayer() {
        if (viewModel.getRecipe() != null && viewModel.getPreviousPosition() != viewModel.getPosition()) {
            TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            exoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(factory), new DefaultLoadControl());
            playerView.setPlayer(exoPlayer);
            MediaSource mediaSource = buildMediaSource();
            viewModel.setPlayWhenReady(true);
            if (mediaSource != null) {
                playerView.setVisibility(View.VISIBLE);
                exoPlayer.prepare(mediaSource, true, false);
                exoPlayer.setPlayWhenReady(viewModel.isPlayWhenReady());
                exoPlayer.seekTo(viewModel.getIndex(), viewModel.getPlayBackPosition());
            } else {
                playerView.setVisibility(View.INVISIBLE);
            }
            description.setText(viewModel.getRecipe().getSteps().get(viewModel.getPosition()).getDescription());
        }
    }

    private MediaSource buildMediaSource() {
        if (viewModel.getRecipe().getSteps().get(viewModel.getPosition()).getVideoURL().equals(""))
            return null;
        Uri uri = Uri.parse(viewModel.getRecipe().getSteps().get(viewModel.getPosition()).getVideoURL());
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(Util.getUserAgent(this, getResources().getString(R.string.app_name)))).createMediaSource(uri);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        initPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void hideSystemUI() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}