package com.example.popularmovies2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class CustomGridAdapter extends ArrayAdapter<Movie> {
    private Context context;

    CustomGridAdapter(Context context, int resource, ArrayList<Movie> movies) {
        super(context, resource, movies);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.grid_item, null, true);
        }

        Movie movie = getItem(position);
        String poster = ApiRequest.IMAGE_URL + ApiRequest.IMAGE_SIZE + movie.getPoster_path();

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewPoster);
        imageView.setAdjustViewBounds(true);
        Picasso.with(context).load(poster).placeholder(R.drawable.placeholder).into(imageView);

        return convertView;
    }
}
