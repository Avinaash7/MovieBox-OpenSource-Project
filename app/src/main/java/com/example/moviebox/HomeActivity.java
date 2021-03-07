package com.example.moviebox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

public class HomeActivity extends AppCompatActivity {
    private static int Splash_time=7000;
    MediaPlayer mplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        VideoView v= (VideoView)findViewById(R.id.videoView);
        v.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.jokervid);
        v.start();
        mplayer = MediaPlayer.create(this,R.raw.introsound);
        mplayer.start();
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent homeIntent= new Intent(HomeActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },Splash_time);
    }
}