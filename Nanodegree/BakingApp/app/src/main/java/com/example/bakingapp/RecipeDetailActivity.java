package com.example.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnFragmentInteractionListener {
    private Recipe recipe;
    private Boolean isTwoPane;
    private static int index = 0;
    private static long playbackPosition = 0;
    private static boolean play = true;
    private PlayerView playerView;
    private SimpleExoPlayer exoPlayer;
    private int position = 0;
    private int previousPosition = -1;
    private static final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    RecipeDetailFragment recipeDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent intent = getIntent();
        if (intent.hasExtra(getPackageName()))
            recipe = Parcels.unwrap(intent.getParcelableExtra(getPackageName()));
        if (findViewById(R.id.tablet_right_view_fl) == null)
            isTwoPane = false;
        else {
            isTwoPane = true;
            FloatingActionButton changeButton = findViewById(R.id.change_recipe_step_btn);
            TextView description = findViewById(R.id.description_for_step_tablet_tv);
            playerView = findViewById(R.id.video_view);
            if (intent.hasExtra("position")) {
                position = intent.getIntExtra("position", 0);
                initPlayer();
                description.setText(recipe.getSteps().get(position).getDescription());
            }
            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position >= recipe.getSteps().size() - 1) {
                        position = 0;
                        previousPosition = -1;
                    } else {
                        previousPosition = position;
                        position = previousPosition + 1;
                    }
                    releasePlayer();
                    initPlayer();
                }
            });
        }
        setUpFragment(savedInstanceState);
    }

    private void setUpFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            recipeDetailFragment = (RecipeDetailFragment) getSupportFragmentManager().findFragmentByTag("myfragmenttag");
        else
            recipeDetailFragment = new RecipeDetailFragment();

        if (!recipeDetailFragment.isInLayout()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(getPackageName(), Parcels.wrap(recipe));
            bundle.putBoolean("isTwoPane", isTwoPane);
            recipeDetailFragment.setArguments(bundle);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.recipe_detail_fm, recipeDetailFragment, "myfragmenttag").commit();
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            index = exoPlayer.getCurrentWindowIndex();
            play = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void initPlayer() {
        if (isTwoPane && recipe != null && previousPosition != position) {
            TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            exoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(factory), new DefaultLoadControl());
            playerView.setPlayer(exoPlayer);
            MediaSource mediaSource = buildMediaSource();
            if (mediaSource != null) {
                playerView.setVisibility(View.VISIBLE);
                exoPlayer.prepare(mediaSource, true, false);
                exoPlayer.setPlayWhenReady(play);
                exoPlayer.seekTo(index, playbackPosition);
            } else
                playerView.setVisibility(View.INVISIBLE);
        }
    }

    private MediaSource buildMediaSource() {
        if (recipe.getSteps().get(position).getVideoURL().equals(""))
            return null;
        Uri uri = Uri.parse(recipe.getSteps().get(position).getVideoURL());
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(Util.getUserAgent(this, getResources().getString(R.string.app_name)))).createMediaSource(uri);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "myFragmentName", recipeDetailFragment);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void onFragmentInteraction(int i) {
        if (isTwoPane) {
            previousPosition = position;
            position = i;
            initPlayer();
        } else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            Parcelable wrapped = Parcels.wrap(recipe);
            intent.putExtra(getPackageName(), wrapped);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    @Override
    public void onChange(Integer id) {
        recipe = GsonInstance.getInstance().fromJson(RecipeRepository.getInstance().getRecipe(id, this), Recipe.class);
        setUpFragment(null);
    }
}