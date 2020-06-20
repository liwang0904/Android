package com.example.popularmovies2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ReviewActivity extends AppCompatActivity {
    private TextView tv_author, tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();

        tv_author = (TextView) findViewById(R.id.tv_reviewer_name);
        tv_content = (TextView) findViewById(R.id.tv_reviewer_content);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String author = extras.getString("name");
            String content = extras.getString("content");
            String movie_title = extras.getString("title");
            if (actionBar != null) {
                actionBar.setTitle(movie_title);
            }

            tv_author.setText(author);
            tv_content.setText(content);
        }
    }
}
