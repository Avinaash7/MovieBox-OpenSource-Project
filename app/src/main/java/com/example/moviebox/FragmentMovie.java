package com.example.moviebox;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import com.androidnetworking.common.Priority;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;
import com.androidnetworking.AndroidNetworking;
import java.text.ParseException;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class
FragmentMovie extends Fragment implements MovieHorizontalAdapter.onSelectData, MovieAdapter.onSelectData {

private RecyclerView rvNowPlaying, rvFilmRecommend;
private MovieHorizontalAdapter movieHorizontalAdapter;
private MovieAdapter movieAdapter;
private ProgressDialog progressDialog;
private SearchView searchFilm;
private List<ModelMovie> moviePlayNow = new ArrayList<>();
private List<ModelMovie> moviePopular = new ArrayList<>();

public FragmentMovie() {
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_film, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        //progressDialog.setCancelable(false);
        progressDialog.setMessage("Displaying Movies");
        progressDialog.show();


        searchFilm = rootView.findViewById(R.id.searchFilm);
        searchFilm.setQueryHint("Search Film");
        searchFilm.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
@Override
public boolean onQueryTextSubmit(String query) {
        setSearchMovie(query);
        return false;
        }

@Override
public boolean onQueryTextChange(String newText) {
        if (newText.equals(""))
        getMovie();
        return false;
        }
        });

        int searchPlateId = searchFilm.getContext().getResources()
        .getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchFilm.findViewById(searchPlateId);
        if (searchPlate != null) {
        searchPlate.setBackgroundColor(Color.TRANSPARENT);
        }

        rvNowPlaying = rootView.findViewById(R.id.NowPlaying);
        rvNowPlaying.setHasFixedSize(true);
        rvNowPlaying.setLayoutManager(new CardSliderLayoutManager(getActivity()));
        new CardSnapHelper().attachToRecyclerView(rvNowPlaying);

        rvFilmRecommend = rootView.findViewById(R.id.rvFilmRecommend);
        rvFilmRecommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFilmRecommend.setHasFixedSize(true);

        getMovieHorizontal();
        getMovie();

        return rootView;
        }

private void setSearchMovie(String query) {
        progressDialog.show();
        progressDialog.setCancelable(false);
        AndroidNetworking.get("http://api.themoviedb.org/3/" + "search/movie?"
        + "api_key=6a001b157b40a447c05be9472959cfca" + "&language=en-US" + "&query=" + query)
        .setPriority(Priority.HIGH)
        .build()
        .getAsJSONObject(new JSONObjectRequestListener() {
@Override
public void onResponse(JSONObject response) {
        try {
        progressDialog.dismiss();
        moviePopular = new ArrayList<>();
        JSONArray jsonArray = response.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        ModelMovie dataApi = new ModelMovie();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMMM yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String datePost = jsonObject.getString("release_date");

        dataApi.setId(jsonObject.getInt("id"));
        dataApi.setTitle(jsonObject.getString("title"));
        dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
        dataApi.setOverview(jsonObject.getString("overview"));
        dataApi.setReleaseDate(formatter.format(dateFormat.parse(datePost)));
        dataApi.setPosterPath(jsonObject.getString("poster_path"));
        dataApi.setBackdropPath(jsonObject.getString("backdrop_path"));
        dataApi.setPopularity(jsonObject.getString("popularity"));
        moviePopular.add(dataApi);
        showMovie();
        }
        } catch (JSONException | ParseException e) {
        e.printStackTrace();
        Toast.makeText(getActivity(), "Failed to display data!", Toast.LENGTH_SHORT).show();
        }
        }

@Override
public void onError(ANError anError) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "There is no internet network!", Toast.LENGTH_SHORT).show();
        }
        });
        }

private void getMovieHorizontal() {
        progressDialog.show();
        AndroidNetworking.get("http://api.themoviedb.org/3/" + "movie/now_playing?" + "api_key=6a001b157b40a447c05be9472959cfca" + "&language=en-US")
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
        ModelMovie dataApi = new ModelMovie();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMMM yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String datePost = jsonObject.getString("release_date");

        dataApi.setId(jsonObject.getInt("id"));
        dataApi.setTitle(jsonObject.getString("title"));
        dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
        dataApi.setOverview(jsonObject.getString("overview"));
        dataApi.setReleaseDate(formatter.format(dateFormat.parse(datePost)));
        dataApi.setPosterPath(jsonObject.getString("poster_path"));
        dataApi.setBackdropPath(jsonObject.getString("backdrop_path"));
        dataApi.setPopularity(jsonObject.getString("popularity"));
        moviePlayNow.add(dataApi);
        showMovieHorizontal();
        }
        } catch (JSONException | ParseException e) {
        e.printStackTrace();
        Toast.makeText(getActivity(), "Failed to display data!", Toast.LENGTH_SHORT).show();
        }
        }

@Override
public void onError(ANError anError) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "There is no internet network!", Toast.LENGTH_SHORT).show();
        }
        });
        }

private void getMovie() {
        progressDialog.show();
        AndroidNetworking.get("http://api.themoviedb.org/3/" + "discover/movie?"+"api_key=6a001b157b40a447c05be9472959cfca" + "&language=en-US")
        .setPriority(Priority.HIGH)
        .build()
        .getAsJSONObject(new JSONObjectRequestListener() {
@Override
public void onResponse(JSONObject response) {
        try {
        progressDialog.dismiss();
        moviePopular = new ArrayList<>();
        JSONArray jsonArray = response.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        ModelMovie dataApi = new ModelMovie();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMMM yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String datePost = jsonObject.getString("release_date");

        dataApi.setId(jsonObject.getInt("id"));
        dataApi.setTitle(jsonObject.getString("title"));
        dataApi.setVoteAverage(jsonObject.getDouble("vote_average"));
        dataApi.setOverview(jsonObject.getString("overview"));
        dataApi.setReleaseDate(formatter.format(dateFormat.parse(datePost)));
        dataApi.setPosterPath(jsonObject.getString("poster_path"));
        dataApi.setBackdropPath(jsonObject.getString("backdrop_path"));
        dataApi.setPopularity(jsonObject.getString("popularity"));
        moviePopular.add(dataApi);
        showMovie();
        }
        } catch (JSONException | ParseException e) {
        e.printStackTrace();
        Toast.makeText(getActivity(), "Failed to display data!", Toast.LENGTH_SHORT).show();
        }
        }

@Override
public void onError(ANError anError) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "There is no internet network!", Toast.LENGTH_SHORT).show();
        }
        });
        }

private void showMovieHorizontal() {
        movieHorizontalAdapter = new MovieHorizontalAdapter(getActivity(), moviePlayNow, this);
        rvNowPlaying.setAdapter(movieHorizontalAdapter);
        movieHorizontalAdapter.notifyDataSetChanged();
        }

private void showMovie() {
        movieAdapter = new MovieAdapter(getActivity(), moviePopular, this);
        rvFilmRecommend.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
        }

@Override
public void onSelected(ModelMovie modelMovie) {
        Intent intent = new Intent(getActivity(), DetailMovieActivity.class);
        intent.putExtra("detailMovie", modelMovie);
        startActivity(intent);
        }
        }