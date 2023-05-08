package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.content.Intent;


import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.ImageView;

import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.AudienceProgress;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CompleteActivity extends AppCompatActivity {

    public Toolbar toolbar;

    public TextView txt_result_title, tvLevelScore, tvCorrect, tvInCorrect, tvLevelCoins, tvPlayNext, victorymsg, txtReview, txtShare, txtRate, txtHome;
    public ScrollView scrollView;
    public AudienceProgress result_prog;
    boolean isLevelCompleted;
    public RelativeLayout mainLayout;
    public String fromQue;
    public AppCompatActivity activity;
    public int levelNo;
    private ReviewInfo reviewInfo;
    private ReviewManager manager;
    ImageView victoryimg;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_complete);
        mainLayout = findViewById(R.id.mainLayout);


        Locale locale = new Locale(Session.getApplanguage(CompleteActivity.this));
        Session.setApplanguage(CompleteActivity.this, Session.getApplanguage(CompleteActivity.this));
        Locale.setDefault(locale);
        Resources resources = getResources();
        DrawerActivity.config = resources.getConfiguration();
        DrawerActivity.config.setLocale(locale);
        resources.updateConfiguration(DrawerActivity.config, resources.getDisplayMetrics());


        fromQue = getIntent().getStringExtra("fromQue");
        levelNo = getIntent().getIntExtra("levelNo", 1);
        activity = CompleteActivity.this;

        Utils.loadAd(activity);
        Utils.displayInterstitial(activity);

        result_prog = findViewById(R.id.result_progress);
        result_prog.SetAttributes1();
        scrollView = findViewById(R.id.scrollView);
        txt_result_title = findViewById(R.id.txt_result_title);
        tvCorrect = findViewById(R.id.right);
        tvInCorrect = findViewById(R.id.wrong);

        tvLevelScore = findViewById(R.id.tvScore);
        victoryimg = findViewById(R.id.victoryimg);
        tvLevelScore.setText("" + Utils.level_score);
        tvLevelCoins = findViewById(R.id.tvCoin);

        victorymsg = findViewById(R.id.victorymsg);
        tvLevelCoins.setText("" + Utils.level_coin);
        manager = ReviewManagerFactory.create(activity);

        tvCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
        tvInCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close_24, 0, 0, 0);
        tvLevelScore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.score_white, 0, 0, 0);
        tvLevelCoins.setCompoundDrawablesWithIntrinsicBounds(R.drawable.coins_wjite, 0, 0, 0);
        tvPlayNext = findViewById(R.id.tvPlayNext);
        txtReview = findViewById(R.id.txtReview);
        txtShare = findViewById(R.id.txtShare);
        txtRate = findViewById(R.id.txtRate);
        txtHome = findViewById(R.id.txtHome);
        txtRate.setText(R.string.leaderboard);
        txtShare.setText(R.string.share_score);
        txtReview.setText(R.string.review);
        txtHome.setText(R.string.quite);
        getLevel();
        isLevelCompleted = Session.isLevelCompleted(activity);
        if (isLevelCompleted) {
            victoryimg.setBackgroundResource(R.drawable.ic_victory);
            victorymsg.setText(getString(R.string.victory_));
            if (Constant.TotalLevel == Utils.RequestlevelNo) {
                Utils.RequestlevelNo = 1;
                tvPlayNext.setText(getResources().getString(R.string.play_again));
            } else {
                Utils.RequestlevelNo = Utils.RequestlevelNo + 1;
                txt_result_title.setText(getString(R.string.completed));
                tvPlayNext.setText(getResources().getString(R.string.next_play));
            }
        } else {
            victorymsg.setText(getString(R.string.defeat));
            victoryimg.setBackgroundResource(R.drawable.ic_defeat);
            txt_result_title.setText(getString(R.string.not_completed));
            tvPlayNext.setText(getResources().getString(R.string.play_next));
        }
        result_prog.setCurrentProgress(getPercentageCorrect(Utils.TotalQuestion, Utils.CoreectQuetion));
        tvCorrect.setText("" + Utils.CoreectQuetion);
        tvInCorrect.setText("" + Utils.WrongQuation);

        if (Session.isLogin(activity)) {
            GetUserData();
        }
        if (Constant.IN_APP_MODE.equals("1")) {
            if (Constant.ADS_TYPE.equals("1")) {
                Utils.LoadNativeAds(activity);
            } else {
                Utils.LoadNativeAd(activity);
            }
        }

        
    }


    public static float getPercentageCorrect(int questions, int correct) {
        float proportionCorrect = ((float) correct) / ((float) questions);
        return proportionCorrect * 100;
    }

    public void PlayAgain(View view) {

        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra("fromQue", fromQue);
        intent.putExtra("levelNo", levelNo);
        startActivity(intent);
        finish();
    }

    public void ReviewAnswers(View view) {
        Intent intentReview = new Intent(activity, ReviewActivity.class);
        intentReview.putExtra("from", "regular");
        startActivity(intentReview);
    }

    public void ShareScore(View view) {
        String shareMsg = getString(R.string.levelfinish)+" "+ Utils.RequestlevelNo +" "+ getString(R.string.with)+" " + Utils.level_score+" " + getString(R.string.scorein)+" " + getString(R.string.app_name);
        Utils.ShareInfo(scrollView, activity, shareMsg);
    }

    public void RateApp(View view) {
        Utils.displayInterstitial(activity);

        rateClicked();
    }

    public void Home(View view) {
        Intent intent1 = new Intent(activity, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("type", "default");
        startActivity(intent1);
    }

    private void rateClicked() {
        Intent leaderBoard = new Intent(activity, LeaderboardTabActivity.class);
        startActivity(leaderBoard);
    }

    public void GetUserData() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_BY_ID, "1");
        params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {
            // System.out.println("========search result " + response);
            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject jsonobj = obj.getJSONObject("data");
                        Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
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
    public void onResume() {
        super.onResume();
        Utils.loadAd(activity);
        Utils.CheckBgMusic(activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.StopSound();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getLevel() {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_LEVEL_DATA, "1");
        params.put(Constant.category, String.valueOf(Constant.CATE_ID));
        params.put(Constant.subCategoryId, String.valueOf(Constant.SUB_CAT_ID));
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {
            // System.out.println("========search result " + response);
            if (result) {
                try {
                   // System.out.println("====response " + response);
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject object = obj.getJSONObject(Constant.DATA);
                        levelNo = (Integer.parseInt(object.getString(Constant.LEVEL)));
                        LevelActivity.levelNo = (levelNo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

}