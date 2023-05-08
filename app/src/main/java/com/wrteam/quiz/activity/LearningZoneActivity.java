package com.wrteam.quiz.activity;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.Utils;

public class LearningZoneActivity extends YouTubeBaseActivity {

    String api_key = "";
    public ProgressBar prgLoading;
    public WebView mWebView;
    public String id,video_id;
    public Toolbar toolbar;
    TextView tvStartGame;
    YouTubePlayerView ytPlayer;
    ImageView imgBack;
    TextView txtTitle;

    Activity activity;

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_learningzone);

        activity= new LearningZoneActivity();
        id = getIntent().getStringExtra("id");
        video_id=getIntent().getStringExtra("videoid");
        api_key=getIntent().getStringExtra("apiKey");
        prgLoading = findViewById(R.id.prgLoading);
        tvStartGame = findViewById(R.id.tvStartGame);
        tvStartGame.setVisibility(View.VISIBLE);
        mWebView = findViewById(R.id.webView1);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> onBackPressed());
        ytPlayer = (YouTubePlayerView) findViewById(R.id.ytPlayer);
        txtTitle=findViewById(R.id.txtTitle);
        txtTitle.setText(Constant.cate_name);

        if(video_id.equals("")){
            ytPlayer.setVisibility(View.GONE);
        }else {
            ytPlayer.setVisibility(View.VISIBLE);
        }
        ytPlayer.initialize(
                api_key,
                new YouTubePlayer.OnInitializedListener() {
                    // Implement two methods by clicking on red
                    // error bulb inside onInitializationSuccess
                    // method add the video link or the playlist
                    // link that you want to play In here we
                    // also handle the play and pause
                    // functionality
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(video_id);
                        youTubePlayer.play();
                    }
                    // Inside onInitializationFailure
                    // implement the failure functionality
                    // Here we will show toast
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult
                                                                youTubeInitializationResult) {
                        Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
                    }

                });
        try {

            mWebView.setClickable(true);
            mWebView.setFocusableInTouchMode(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            // getSupportActionBar().setTitle(Constant.cate_name);
            /*GetPrivacyAndTerms();*/
            if (Utils.isNetworkAvailable(this)) {
                if (!prgLoading.isShown()) {
                    prgLoading.setVisibility(View.VISIBLE);
                }
                mWebView.setVerticalScrollBarEnabled(true);
                mWebView.loadDataWithBaseURL("", getIntent().getStringExtra("message"), "text/html", "UTF-8", "");
                mWebView.setBackgroundColor(getResources().getColor(R.color.bg_color));
            } else {
                //setSnackBar();
            }
            prgLoading.setVisibility(View.GONE);
            tvStartGame.setOnClickListener(v -> {

                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("fromQue", "learning");
                intent.putExtra("learning_id", id);
                startActivity(intent);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();

    }
}