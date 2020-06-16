package com.example.popularmovies1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    ArrayList<String> posters;
    ArrayList<Integer> ids;

    String sort_type;

    GridView gridView;
    ImageAdapter imageAdapter;

    FetchMovies fetchMovies;

    String api_key = "33794c1f64d8154fab6ffab92de21f27";

    public class FetchMovies extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr;

            posters = new ArrayList<>();
            ids = new ArrayList<>();

            try {
                String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + params[0] + "?api_key=" + api_key);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if(inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                if(builder.length() == 0) {
                    return null;
                }

                moviesJsonStr = builder.toString();

                JSONObject main = new JSONObject(moviesJsonStr);
                JSONArray array = main.getJSONArray("results");
                JSONObject movie;
                for (int i = 0; i < array.length(); i++) {
                    movie = array.getJSONObject(i);
                    ids.add(movie.getInt("id"));
                    posters.add(movie.getString("poster_path"));
                }

            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            imageAdapter = new ImageAdapter(MainActivity.this, posters);
            gridView.setAdapter(imageAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.grid_view);

        sharedPreferences = getSharedPreferences("popular_movies", MODE_PRIVATE);
        sort_type = sharedPreferences.getString("sort_type", "popular");

        if (sort_type.equals("popular")) {
            getSupportActionBar().setTitle(R.string.app_name);
        } else if (sort_type.equals("top_rated")) {
            getSupportActionBar().setTitle(R.string.top_rated);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("Movie ID", ids.get(i));
                intent.putExtra("Poster", posters.get(i));
                startActivity(intent);
            }
        });

        fetchMovies = new FetchMovies();
        fetchMovies.execute(sort_type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            int selected = 0;

            sort_type = sharedPreferences.getString("sort_type", "popular");
            if (sort_type.equals("popular")) {
            } else if (sort_type.equals("top_rated")) {
                selected = 1;
            }

            builder.setTitle(R.string.dialog_title);
            builder.setSingleChoiceItems(R.array.sort_types, selected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        editor.putString("sort_type", "popular");
                    } else if (i == 1) {
                        editor.putString("sort_type", "top_rated");
                    }
                }
            });

            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    editor.apply();
                }
            });

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}