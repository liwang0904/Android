package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

public class JokeAsyncTask extends AsyncTask<Void, Void, String> {
    private static MyApi apiService = null;
}
