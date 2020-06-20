package com.example.popularmovies2;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ArrayList<Movie> arrayList;
    private GridView gridView;
    private ProgressBar mProgressBar;
    private String TAG = MainActivity.class.getSimpleName();
    private String sortBy = "";
    private String poster_path, title, release_date, description, users_rating, movie_id;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();
        gridView = (GridView) findViewById(R.id.gridView);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (savedInstanceState != null) {
            arrayList = savedInstanceState.getParcelableArrayList("key");
            show_details(index);
        } else {
            if (AppStatus.getInstance(this).isOnline()) {
                action_show_popular();
            } else {
                action_show_favorites();
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("key", arrayList);
        index = gridView.getFirstVisiblePosition();
        super.onSaveInstanceState(savedInstanceState);
    }

    private class GetMovies extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            Uri uri = Uri.parse(ApiRequest.API_URL + sortBy).buildUpon()
                    .appendQueryParameter("api_key", ApiRequest.API_KEY)
                    .build();
            URL url;
            String jsonStr = null;
            try {
                url = new URL(uri.toString());
                jsonStr = sh.makeServiceCall(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray movies = jsonObject.getJSONArray("results");
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject m = movies.getJSONObject(i);
                        arrayList.add(new Movie(
                                m.getString("poster_path"),
                                m.getString("title"),
                                m.getString("id"),
                                m.getString("release_date"),
                                m.getString("vote_average"),
                                m.getString("overview")
                        ));
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Maybe your internet is not working!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.INVISIBLE);
            show_details(0);
        }
    }

    public void show_details(int mIndex) {
        CustomGridAdapter adapter = new CustomGridAdapter(
                getApplicationContext(), R.layout.grid_item, arrayList
        );
        gridView.setAdapter(adapter);
        gridView.setSelection(mIndex);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie movie = arrayList.get(position);
                Intent intent = new Intent("android.intent.action.DETAILSACTIVITY");

                title = movie.getTitle();
                poster_path = movie.getPoster_path();
                description = movie.getDescription();
                users_rating = movie.getUsers_rating();
                release_date = movie.getRelease_date();
                movie_id = movie.getId();

                intent.putExtra("title", title);
                intent.putExtra("poster", poster_path);
                intent.putExtra("description", description);
                intent.putExtra("release_date", release_date);
                intent.putExtra("users_rating", users_rating);
                intent.putExtra("id", movie_id);

                startActivity(intent);

            }
        });
    }

    public boolean action_show_favorites() {
        mProgressBar.setVisibility(View.INVISIBLE);
        sortBy = "";
        arrayList.clear();
        DatabaseHandler db = new DatabaseHandler(this);
        arrayList = db.getAllMovies();

        show_details(0);

        Toast.makeText(getApplicationContext(), "Showing favorites", Toast.LENGTH_LONG).show();
        return true;
    }

    public boolean action_show_popular() {
        if (!sortBy.equals("popular")) {
            sortBy = "popular";
            arrayList.clear();
            new GetMovies().execute();
            Toast.makeText(getApplicationContext(), "Searching for popular movies!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "You are already in popular!", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public boolean action_show_rate() {

        if (!sortBy.equals("top_rated")) {
            sortBy = "top_rated";
            arrayList.clear();
            new GetMovies().execute();
            Toast.makeText(getApplicationContext(), "Searching for top rated movies!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "You are already in top rated!", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_favorites:
                action_show_favorites();
                return true;
            case R.id.action_sort_by_popularity:
                if (AppStatus.getInstance(this).isOnline()) {
                    action_show_popular();
                } else {
                    Toast.makeText(getApplicationContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_sort_by_rate:
                if (AppStatus.getInstance(this).isOnline()) {
                    action_show_rate();
                } else {
                    Toast.makeText(getApplicationContext(), "Please connect to the internet!", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (sortBy.equals("")) {
            action_show_favorites();
        }
    }
}
