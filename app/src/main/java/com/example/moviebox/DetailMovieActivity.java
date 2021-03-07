package com.example.moviebox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.androidnetworking.common.Priority;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailMovieActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView MovTitle, MovName, MovRating, MovRelease, MovPopularity, MovOverview;
    ImageView imgCover, imgPhoto;
    RecyclerView rvTrailer;
    RatingBar ratingBar;
    String NameFilm, ReleaseDate, Popularity, Overview, Cover, Thumbnail, movieURL;
    int Id;
    double Rating;
    ModelMovie modelMovie;
    ProgressDialog progressDialog;
    List<ModelTrailer> modelTrailer = new ArrayList<>();
    TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("\n" +
                "Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("\n" +
                "Now showing the trailer");

        ratingBar = findViewById(R.id.ratingBar);
        imgCover = findViewById(R.id.imgCover);
        imgPhoto = findViewById(R.id.imgPhoto);
        MovTitle = findViewById(R.id.Title);
        MovName = findViewById(R.id.MovieName);
        MovRating = findViewById(R.id.Rating);
        MovRelease = findViewById(R.id.Release);
        MovPopularity = findViewById(R.id.Popularity);
        MovOverview = findViewById(R.id.Overview);
        rvTrailer = findViewById(R.id.rvTrailer);



        modelMovie = (ModelMovie) getIntent().getSerializableExtra("detailMovie");
        if (modelMovie != null) {

            Id = modelMovie.getId();
            NameFilm = modelMovie.getTitle();
            Rating = modelMovie.getVoteAverage();
            ReleaseDate = modelMovie.getReleaseDate();
            Popularity = modelMovie.getPopularity();
            Overview = modelMovie.getOverview();
            Cover = modelMovie.getBackdropPath();
            Thumbnail = modelMovie.getPosterPath();
            movieURL = "https://www.themoviedb.org/movie/"+ "" + Id;

            MovTitle.setText(NameFilm);
            MovName.setText(NameFilm);
            MovRating.setText(Rating + "/10");
            MovRelease.setText(ReleaseDate);
            MovPopularity.setText(Popularity);
            MovOverview.setText(Overview);
            MovTitle.setSelected(true);
            MovName.setSelected(true);

            float newValue = (float)Rating;
            ratingBar.setNumStars(5);
            ratingBar.setStepSize((float) 0.5);
            ratingBar.setRating(newValue / 2);

            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w780/" + Cover)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgCover);

            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w780/" + Thumbnail)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgPhoto);

            rvTrailer.setHasFixedSize(true);
            rvTrailer.setLayoutManager(new LinearLayoutManager(this));

            getTrailer();

        }

    }

    private void getTrailer() {
        progressDialog.show();
        //MovieVideo
        AndroidNetworking.get("http://api.themoviedb.org/3/" + "movie/{id}/videos?" + "api_key=6a001b157b40a447c05be9472959cfca" + "&language=en-US")
                .addPathParameter("id", String.valueOf(Id))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ModelTrailer dataApi = new ModelTrailer();
                                dataApi.setKey(jsonObject.getString("key"));
                                dataApi.setType(jsonObject.getString("type"));
                                modelTrailer.add(dataApi);
                                showTrailer();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailMovieActivity.this,
                                    "Failed to display data!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Toast.makeText(DetailMovieActivity.this,
                                "\n" +
                                        "There is no internet network!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showTrailer() {
        trailerAdapter = new TrailerAdapter(DetailMovieActivity.this, modelTrailer);
        rvTrailer.setAdapter(trailerAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}