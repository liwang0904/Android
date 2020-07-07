package com.example.teleprompter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindBool;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {
    public static final String EDIT_FRAGMENT_TAG = "document-edit-tag";
    public static final String PARCEL_KEY = "parcel-key";

    @BindBool(R.bool.isTabletLand)
    boolean isTabletLand;
    @BindBool(R.bool.isTabletPort)
    boolean isTabletPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        final Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Document document = null;
        Intent intent = getIntent();
        if (intent.hasExtra(PARCEL_KEY)) document = intent.getParcelableExtra(PARCEL_KEY);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PARCEL_KEY, document);
            EditFragment fragment = new EditFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.edit_fragment_container, fragment, EDIT_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        EditFragment fragment = (EditFragment) getSupportFragmentManager().findFragmentByTag(EDIT_FRAGMENT_TAG);
        int id = item.getItemId();
        if (id == R.id.action_delete) fragment.delete();
        else if (id == R.id.action_share) fragment.share();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.document_edit, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}