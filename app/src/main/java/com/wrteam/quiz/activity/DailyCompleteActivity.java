package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;


import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.AudienceProgress;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DailyCompleteActivity extends AppCompatActivity {

    public Toolbar toolbar;
    public TextView txt_result_title, tvLevelScore, tvCorrect, tvInCorrect, tvLevelCoins, tvPlayNext, txtVictorymsg, txtReview, txtShare, txtRate, txtHome;
    public ScrollView scrollView;
    public AudienceProgress result_prog;
    boolean isLevelCompleted;
    public RelativeLayout mainLayout;
    public String fromQue, types;
    public Context context;
    CardView cardReview;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        mainLayout = findViewById(R.id.mainLayout);

        Locale locale = new Locale(Session.getApplanguage(DailyCompleteActivity.this));
        Session.setApplanguage(DailyCompleteActivity.this, Session.getApplanguage(DailyCompleteActivity.this));
        Locale.setDefault(locale);
        Resources resources = getResources();
        DrawerActivity.config = resources.getConfiguration();
        DrawerActivity.config.setLocale(locale);
        resources.updateConfiguration(DrawerActivity.config, resources.getDisplayMetrics());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fromQue = getIntent().getStringExtra("fromQue");
        types = getIntent().getStringExtra("type");

        context = DailyCompleteActivity.this;
        cardReview = findViewById(R.id.cardReview);


        Utils.loadAd(DailyCompleteActivity.this);

        result_prog = findViewById(R.id.result_progress);
        result_prog.SetAttributes1();
        scrollView = findViewById(R.id.scrollView);
        txt_result_title = findViewById(R.id.txt_result_title);
        tvCorrect = findViewById(R.id.right);
        tvInCorrect = findViewById(R.id.wrong);

        tvLevelScore = findViewById(R.id.tvScore);
        txtVictorymsg = findViewById(R.id.victorymsg);
        txtVictorymsg.setVisibility(View.GONE);
        tvLevelScore.setText("" + Utils.level_score);
        tvLevelCoins = findViewById(R.id.tvCoin);

        tvLevelCoins.setText("" + Utils.level_coin);

        tvCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_24, 0, 0, 0);
        tvInCorrect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close_24, 0, 0, 0);
        tvLevelScore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rank, 0, 0, 0);
        tvLevelCoins.setCompoundDrawablesWithIntrinsicBounds(R.drawable.coins, 0, 0, 0);
        tvPlayNext = findViewById(R.id.tvPlayNext);
        txtReview = findViewById(R.id.txtReview);
        txtShare = findViewById(R.id.txtShare);
        txtRate = findViewById(R.id.txtRate);
        txtHome = findViewById(R.id.txtHome);
        txtRate.setText(R.string.rateapp);
        txtShare.setText(R.string.share_score);
        txtReview.setText(R.string.review);
        txtHome.setText(R.string.quite);
        isLevelCompleted = Session.isLevelCompleted(DailyCompleteActivity.this);

        if (fromQue.equals("learning")) {
            txt_result_title.setText(R.string.learningcompleted);
        } else if (fromQue.equals("mathsplay")) {
            txt_result_title.setText(R.string.mathscompleted);
        } else if (fromQue.equals("random")) {
            txt_result_title.setText(R.string.randomcompleted);
        } else if (fromQue.equals("true_false")) {
            txt_result_title.setText(R.string.truefalsecompleted);
        } else {
            txt_result_title.setText(R.string.dailycompleted);
        }

        tvPlayNext.setText(getResources().getString(R.string.play_next));

        result_prog.setCurrentProgress((double) getPercentageCorrect(Utils.TotalQuestion, Utils.CoreectQuetion));
        tvCorrect.setText("" + Utils.CoreectQuetion);
        tvInCorrect.setText("" + Utils.WrongQuation);

        /*Utils.interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Utils.loadAd(DailyCompleteActivity.this);
            }
        });*/
        if (Session.isLogin(DailyCompleteActivity.this)) {
            GetUserData();
        }

        if (Constant.IN_APP_MODE.equals("1")) {
            if (Constant.ADS_TYPE.equals("1")) {
                Utils.LoadNativeAds(DailyCompleteActivity.this);
            } else {
                Utils.LoadNativeAd(DailyCompleteActivity.this);
            }
        }

    }

    public static float getPercentageCorrect(int questions, int correct) {
        float proportionCorrect = ((float) correct) / ((float) questions);
        return proportionCorrect * 100;
    }

    public void PlayAgain(View view) {
        if (fromQue.equals("mathsplay")) {
            Intent intent = new Intent(DailyCompleteActivity.this, MathsPlayActivity.class);
            intent.putExtra("fromQue", fromQue);
            intent.putExtra("type", types);
            startActivity(intent);
            ((DailyCompleteActivity) context).finish();
        } else {
            Intent intent = new Intent(DailyCompleteActivity.this, PlayActivity.class);
            intent.putExtra("fromQue", fromQue);
            if (fromQue.equals("learning"))
                intent.putExtra("learning_id", getIntent().getStringExtra("learning_id"));
            startActivity(intent);
            ((DailyCompleteActivity) context).finish();
        }
    }

    public void ReviewAnswers(View view) {
        if (fromQue.equals("mathsplay")) {
            Intent intentReview = new Intent(DailyCompleteActivity.this, MathsReviewActivity.class);
            startActivity(intentReview);
        }else {
            Intent intentReview = new Intent(DailyCompleteActivity.this, ReviewActivity.class);
            intentReview.putExtra("from", "regular");
            startActivity(intentReview);
        }

    }

    public void ShareScore(View view) {
        String shareMsg = getString(R.string.finishquiz)+" " + Utils.level_score +" "+ getString(R.string.scorein)+" "  + getString(R.string.app_name);
        Utils.ShareInfo(scrollView, DailyCompleteActivity.this, shareMsg);
    }

    public void RateApp(View view) {
        Utils.displayInterstitial(DailyCompleteActivity.this);
        rateClicked();
    }

    public void Home(View view) {
        Intent intent1 = new Intent(DailyCompleteActivity.this, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("type", "default");
        startActivity(intent1);
    }

    private void rateClicked() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK)));
        }
    }

    public void GetUserData() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_BY_ID, "1");
        params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {
            System.out.println("========search result " + response);
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
        Utils.loadAd(DailyCompleteActivity.this);
        Utils.CheckBgMusic(DailyCompleteActivity.this);
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

}