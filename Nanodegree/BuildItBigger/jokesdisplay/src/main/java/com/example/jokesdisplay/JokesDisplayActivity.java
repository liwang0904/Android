package com.example.jokesdisplay;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class JokesDisplayActivity extends AppCompatActivity {
    TextView jokesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jokes_display);
        jokesText = findViewById(R.id.jokes);
        String joke = getIntent().getStringExtra("JOKES");
        jokesText.setText(joke);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public static Intent newIntent(Context context, String jokes) {
        Intent intent = new Intent(context, JokesDisplayActivity.class);
        intent.putExtra("JOKES", jokes);
        return intent;
    }
}