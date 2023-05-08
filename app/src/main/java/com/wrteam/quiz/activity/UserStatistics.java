package com.wrteam.quiz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import com.google.android.material.snackbar.Snackbar;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.AudienceProgress;
import com.wrteam.quiz.helper.CircleImageView;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserStatistics extends AppCompatActivity {


    Toolbar toolbar;
    TextView tvName, tvRank, tvScore, tvCoin, tvTotalQue, tvCorrect, tvInCorrect, tvCorrectP, tvInCorrectP;
    CircleImageView imgProfile;
    public AudienceProgress progress;
    String totalQues, correctQues, inCorrectQues, strongCate, weakCate, strongRatio, weakRatio;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public RecyclerView recyclerView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.user_statistics));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);
        tvScore = findViewById(R.id.tvScore);
        tvCoin = findViewById(R.id.tvCoin);

        tvTotalQue = findViewById(R.id.tvAttended);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvInCorrect = findViewById(R.id.tvInCorrect);
        tvCorrectP = findViewById(R.id.tvCorrectP);
        tvInCorrectP = findViewById(R.id.tvInCorrectP);
        progress = findViewById(R.id.progress);


        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, UserStatistics.this), imageLoader);
        tvName.setText(getString(R.string.hello) + Session.getUserData(Session.NAME, UserStatistics.this));

        GetUserData();
        GetUserStatistics();
        Utils.showBannerAds(UserStatistics.this);

        if (Constant.IN_APP_MODE.equals("1")) {
            if (Constant.ADS_TYPE.equals("1")) {
                Utils.LoadNativeAds(UserStatistics.this);
            } else {
                Utils.LoadNativeAd(UserStatistics.this);
            }
        }
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> {
                    GetUserData();
                    GetUserStatistics();
                });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    public void GetUserStatistics() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_STATISTICS, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, UserStatistics.this));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    System.out.println("StateResponse::=" + response);
                    JSONObject obj = new JSONObject(response);

                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject object = obj.getJSONObject(Constant.DATA);
                        totalQues = object.getString(Constant.QUESTION_ANSWERED);
                        correctQues = object.getString(Constant.CORRECT_ANSWERS);
                        inCorrectQues = String.valueOf(Integer.parseInt(totalQues) - Integer.parseInt(correctQues));
                        strongCate = object.getString(Constant.STRONG_CATE);
                        weakCate = object.getString(Constant.WEAK_CATE);
                        strongRatio = object.getString(Constant.RATIO_1);
                        weakRatio = object.getString(Constant.RATIO_2);
                        tvTotalQue.setText(totalQues);
                        tvCorrect.setText(correctQues);
                        tvInCorrect.setText(inCorrectQues);
                        float percentCorrect = (Float.parseFloat(correctQues) * 100) / Float.parseFloat(totalQues);
                        float percentInCorrect = (Float.parseFloat(inCorrectQues) * 100) / Float.parseFloat(totalQues);
                        int perctn = Math.round(percentCorrect);
                        int perctnincorrect = Math.round(percentInCorrect);
                        tvCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_green, 0, 0, 0);
                        tvInCorrectP.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_red, 0, 0, 0);
                        tvCorrectP.setText(perctn + getString(R.string.modulo_sign));
                        tvInCorrectP.setText(perctnincorrect + getString(R.string.modulo_sign));
                        progress.SetAttributesForStatistics(getApplicationContext());
                        progress.setMaxProgress(Integer.parseInt(totalQues));
                        progress.setCurrentProgress(Integer.parseInt(correctQues));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);


    }

    public void GetUserData() {
        if (Utils.isNetworkAvailable(UserStatistics.this)) {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_USER_BY_ID, "1");
            params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
            ApiConfig.RequestToVolley((result, response) -> {

                if (result) {
                    try {

                        JSONObject obj = new JSONObject(response);
                        boolean error = obj.getBoolean("error");
                        if (!error) {
                            JSONObject jsonobj = obj.getJSONObject("data");
                            Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                            tvCoin.setText("" + Constant.TOTAL_COINS);
                            tvRank.setText("" + jsonobj.getString(Constant.GLOBAL_RANK));
                            tvScore.setText(jsonobj.getString(Constant.GLOBAL_SCORE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params);

        } else {
            setSnackBar();
        }
    }

    @Override
    protected void onDestroy() {
        if (Utils.mAdView != null) {
            Utils.mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
