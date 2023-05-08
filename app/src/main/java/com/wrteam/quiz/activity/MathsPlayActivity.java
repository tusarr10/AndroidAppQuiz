package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.AudienceProgress;
import com.wrteam.quiz.helper.CircleTimer;
import com.wrteam.quiz.helper.MathJaxWebView;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.TouchImageView;
import com.wrteam.quiz.helper.Typewriter;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.model.Question;
import com.wrteam.quiz.model.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MathsPlayActivity extends AppCompatActivity implements View.OnClickListener {
    public Question question;
    public int questionIndex = 0, btnPosition = 0,
            count_question_completed = 0,
            score = 0,
            level_coin = 6,
            correctQuestion = 0,
            inCorrectQuestion = 0;
    public TextView tvScore, coin_count;
    public boolean isOnBg = false;
    public static AdRequest adRequest;
    public RewardedAd rewardedVideoAds;
    public static TextView tvAlert, tvIndex, tvA, tvB, tvC, tvD, tvE;
    public ImageView fifty_fifty, skip_question, resetTimer, audience_poll;
    public static CoordinatorLayout innerLayout;
    public LinearLayout lifelineLyt;
    public RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;
    private Animation animation;
    private final Handler mHandler = new Handler();
    Handler handler;
    public Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, RightSwipe_E, Fade_in, fifty_fifty_anim, TimerCount;
    private AudienceProgress progressBarTwo_A, progressBarTwo_B, progressBarTwo_C, progressBarTwo_D, progressBarTwo_E;
    public static CircleTimer progressTimer;
    public static Timer timer;

    public static ArrayList<String> options;
    public static long leftTime = 0;
    public boolean isDialogOpen = false;
    public static ArrayList<Question> questionList;
    public static ProgressBar progressBar;
    TouchImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ImageView imgZoom;
    boolean isQuizStart;
    int click = 0;
    int textSize;
    TextView tvBack;
    RelativeLayout mainLayout, relayCount;
    public String fromQue, types;
    NestedScrollView mainScroll;
    public ScrollView queScroll;
    public int levelNo;
    String learning_id;
    AppCompatActivity activity;
    public Toolbar toolbar;
    Menu myMenu;
    private RewardedVideoAd rewardedVideoAd;
    Typewriter contact_popup;
    MathJaxWebView txtQuestion, btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, tvImgQues;
    String tex = "This come from string. You can insert inline formula:" +
            " \\(ax^2 + bx + c = 0\\) " +
            "or displayed formula: $$\\sum_{i=0}^n i^2 = \\frac{(n^2+n)(2n+1)}{6}$$";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mathplay);
        mainLayout = findViewById(R.id.play_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity = MathsPlayActivity.this;

        fromQue = getIntent().getStringExtra("fromQue");
        types = getIntent().getStringExtra("type");
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout, R.id.e_layout};
        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }

        textSize = Integer.parseInt(Session.getSavedTextSize(activity));
        Session.removeSharedPreferencesData(activity);
        RightSwipe_A = AnimationUtils.loadAnimation(activity, R.anim.anim_right_amaths);
        RightSwipe_B = AnimationUtils.loadAnimation(activity, R.anim.anim_right_bmaths);
        RightSwipe_C = AnimationUtils.loadAnimation(activity, R.anim.anim_right_cmaths);
        RightSwipe_D = AnimationUtils.loadAnimation(activity, R.anim.anim_right_dmath);
        RightSwipe_E = AnimationUtils.loadAnimation(activity, R.anim.anim_right_emath);
        TimerCount = AnimationUtils.loadAnimation(activity, R.anim.timer_counter);
        Fade_in = AnimationUtils.loadAnimation(activity, R.anim.fade_outmath);
        fifty_fifty_anim = AnimationUtils.loadAnimation(activity, R.anim.fifty_fifty);


        getSupportActionBar().setTitle(Constant.SUB_CATE_NAME);

        resetAllValue();
        mainScroll.setOnTouchListener((v, event) -> {
            v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        queScroll.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        tvBack.setOnClickListener(v -> BackButtonMethod());
        progressTimer.setMaxProgress(Constant.MATHASCIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.MATHASCIRCULAR_MAX_PROGRESS);

        if (Constant.IN_APP_MODE.equals("1")) {
            RewardsAdsFacebookLoad();
            RewardsAdsGoogleLoads();
        }

    }


    public void RewardsAdsGoogleLoads() {
        adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, Constant.ADMOB_REWARDS_ADS,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        // Log.d(TAG, loadAdError.getMessage());
                        rewardedVideoAds = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardedVideoAds = rewardedAd;

                    }
                });
    }


    public void RewardsAdsFacebookLoad() {
        System.out.println("====== dv " + Constant.FB_REWARDS_ADS);
        rewardedVideoAd = new RewardedVideoAd(this, Constant.FB_REWARDS_ADS);
        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                Log.e(AppController.TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                Log.d(AppController.TAG, "Rewarded video ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                Log.d(AppController.TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                Log.d(AppController.TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                Log.d(AppController.TAG, "Rewarded video completed!");

                // Call method to give reward
                Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Constant.REWARD_COIN_VALUE);
                Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.REWARD_COIN_VALUE));

                RewardAdsLoad();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                Log.d(AppController.TAG, "Rewarded video ad closed!");
            }
        };
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener)
                        .build());
    }

    public void RewardAdsLoad() {
        // Instantiate a RewardedVideoAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get
        // a no fill error).
        System.out.println("====== dv " + Constant.FB_REWARDS_ADS);
        rewardedVideoAd = new RewardedVideoAd(this, Constant.FB_REWARDS_ADS);
        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                Log.e(AppController.TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                Log.d(AppController.TAG, "Rewarded video ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                Log.d(AppController.TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                Log.d(AppController.TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                Log.d(AppController.TAG, "Rewarded video completed!");

                // Call method to give reward
                Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Constant.REWARD_COIN_VALUE);
                Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.REWARD_COIN_VALUE));
                RewardAdsLoad();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                Log.d(AppController.TAG, "Rewarded video ad closed!");
            }
        };
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener)
                        .build());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

/*
    public static void ChangeTextSize(int size) {

        if (btnOpt1 != null)
            btnOpt1.setTextSize(size);
        if (btnOpt2 != null)
            btnOpt2.setTextSize(size);
        if (btnOpt3 != null)
            btnOpt3.setTextSize(size);
        if (btnOpt4 != null)
            btnOpt4.setTextSize(size);
        if (btnOpt5 != null)
            btnOpt5.setTextSize(size);
    }
*/

    @SuppressLint({"SetTextI18n", "WrongViewCast"})
    public void resetAllValue() {
        relayCount = findViewById(R.id.relayCount);
        progressTimer = findViewById(R.id.progressTimer);
        tvBack = findViewById(R.id.tvBack);
        innerLayout = findViewById(R.id.innerLayout);
        lifelineLyt = findViewById(R.id.lifelineLyt);
        tvIndex = findViewById(R.id.tvIndex);
        tvAlert = findViewById(R.id.tvAlert);
        progressBar = findViewById(R.id.progressBar);
        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        coin_count = findViewById(R.id.coin_count);
        imgQuestion = findViewById(R.id.imgQuestion);
        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);
        btnOpt5 = findViewById(R.id.btnOpt5);


        imgZoom = findViewById(R.id.imgZoom);
        fifty_fifty = findViewById(R.id.fifty_fifty);
        skip_question = findViewById(R.id.skip_question);
        resetTimer = findViewById(R.id.reset_timer);
        audience_poll = findViewById(R.id.audience_poll);
        txtQuestion = findViewById(R.id.txtQuestion);
        tvImgQues = findViewById(R.id.tvImgQues);
        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);
        layout_E = findViewById(R.id.e_layout);
/*
        btnOpt5.getSettings().setJavaScriptEnabled(true);
        btnOpt4.getSettings().setJavaScriptEnabled(true);
        btnOpt2.getSettings().setJavaScriptEnabled(true);
        btnOpt1.getSettings().setJavaScriptEnabled(true);
        btnOpt3.getSettings().setJavaScriptEnabled(true);
*/


        tvA = findViewById(R.id.tvA);
        tvB = findViewById(R.id.tvB);
        tvC = findViewById(R.id.tvC);
        tvD = findViewById(R.id.tvD);
        tvE = findViewById(R.id.tvE);

        //ChangeTextSize(textSize);
        progressBarTwo_A = findViewById(R.id.progress_A);
        progressBarTwo_B = findViewById(R.id.progress_B);
        progressBarTwo_C = findViewById(R.id.progress_C);
        progressBarTwo_D = findViewById(R.id.progress_D);
        progressBarTwo_E = findViewById(R.id.progress_E);
        progressBarTwo_A.SetAttributes();
        progressBarTwo_B.SetAttributes();
        progressBarTwo_C.SetAttributes();
        progressBarTwo_D.SetAttributes();
        progressBarTwo_E.SetAttributes();

        animation = AnimationUtils.loadAnimation(activity, R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
        count_question_completed = Session.getCountQuestionCompleted(getApplicationContext());
        tvScore = findViewById(R.id.txtScore);
        tvScore.setText(String.valueOf(score));
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));

         contact_popup = findViewById(R.id.typewrite);

        if (Utils.isNetworkAvailable(activity)) {
            getQuestionsFromJson();
        } else {

            tvAlert.setText(getString(R.string.msg_no_internet));
            tvAlert.setVisibility(View.VISIBLE);
            innerLayout.setVisibility(View.GONE);

        }
    }


    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    private void nextQuizQuestion() {
        relayCount.setVisibility(View.VISIBLE);
        lifelineLyt.setVisibility(View.GONE);
        contact_popup.setCharacterDelay(150);
        contact_popup.animateText(getString(R.string.quepreparing));
        DrawerActivity.Locallanguage(MathsPlayActivity.this);
        queScroll.scrollTo(0, 0);
        stopTimer();


        Constant.LeftTime = 0;
        leftTime = 0;
        setAgain();

        invalidateOptionsMenu();

        layout_A.setBackgroundResource(R.drawable.card_shadow);
        layout_B.setBackgroundResource(R.drawable.card_shadow);
        layout_C.setBackgroundResource(R.drawable.card_shadow);
        layout_D.setBackgroundResource(R.drawable.card_shadow);
        layout_E.setBackgroundResource(R.drawable.card_shadow);
        layout_A.clearAnimation();
        layout_B.clearAnimation();
        layout_C.clearAnimation();
        layout_D.clearAnimation();
        layout_E.clearAnimation();

        layout_A.setClickable(false);
        layout_B.setClickable(false);
        layout_C.setClickable(false);
        layout_D.setClickable(false);
        layout_E.setClickable(false);

        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            int temp = (questionIndex + 1);
            imgQuestion.resetZoom();
            tvIndex.setText(temp + "/" + questionList.size());

            if (!question.getImage().isEmpty()) {
                txtQuestion.setVisibility(View.GONE);
                imgQuestion.setImageUrl(question.getImage(), imageLoader);
                tvImgQues.startAnimation(Fade_in);
                tvImgQues.setText(question.getQuestion());
                tvImgQues.setVisibility(View.VISIBLE);
                imgQuestion.setVisibility(View.VISIBLE);
                imgZoom.setVisibility(View.VISIBLE);
                btnOpt1.startAnimation(RightSwipe_A);
                btnOpt2.startAnimation(RightSwipe_B);
                btnOpt3.startAnimation(RightSwipe_C);
                btnOpt4.startAnimation(RightSwipe_D);
                btnOpt5.startAnimation(RightSwipe_E);

                imgZoom.setOnClickListener(view -> {
                    click++;
                    if (click == 1)
                        imgQuestion.setZoom(1.25f);
                    else if (click == 2)
                        imgQuestion.setZoom(1.50f);
                    else if (click == 3)
                        imgQuestion.setZoom(1.75f);
                    else if (click == 4) {
                        imgQuestion.setZoom(2.00f);
                        click = 0;
                    }
                });
            } else {
                /*txtQuestion.getSettings().setJavaScriptEnabled(true);*/

                txtQuestion.setText(question.getQuestion());
                /*txtQuestion.getSettings().setJavaScriptEnabled(true);*/
                imgZoom.setVisibility(View.GONE);
                imgQuestion.setVisibility(View.GONE);
                tvImgQues.setVisibility(View.GONE);
                txtQuestion.setVisibility(View.VISIBLE);

            }


            options = new ArrayList<>();
            options.addAll(question.getOptions());

/*
            if (question.getQueType().equals(Constant.TRUE_FALSE)) {

                layout_C.setVisibility(View.GONE);
                layout_D.setVisibility(View.GONE);

                btnOpt1.setGravity(Gravity.CENTER);
                btnOpt2.setGravity(Gravity.CENTER);
                lifelineLyt.setVisibility(View.GONE);
            } else {
                Collections.shuffle(options);
                layout_C.setVisibility(View.VISIBLE);
                layout_D.setVisibility(View.VISIBLE);

                btnOpt1.setGravity(Gravity.NO_GRAVITY);
                btnOpt2.setGravity(Gravity.NO_GRAVITY);
                lifelineLyt.setVisibility(View.VISIBLE);
            }
*/

            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 4)
                    layout_E.setVisibility(View.GONE);
                else
                    layout_E.setVisibility(View.VISIBLE);
            }
            btnOpt1.setText((options.get(0).trim()));
            btnOpt2.setText((options.get(1).trim()));
            btnOpt3.setText((options.get(2).trim()));
            btnOpt4.setText((options.get(3).trim()));
            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 5)
                    btnOpt5.setText((options.get(4).trim()));
            }

            final Handler handler = new Handler();
            final Runnable counter = () -> {
                relayCount.setVisibility(View.GONE);
                lifelineLyt.setVisibility(View.VISIBLE);
                txtQuestion.startAnimation(Fade_in);
                btnOpt1.startAnimation(RightSwipe_A);
                btnOpt2.startAnimation(RightSwipe_B);
                btnOpt3.startAnimation(RightSwipe_C);
                btnOpt4.startAnimation(RightSwipe_D);
                btnOpt5.startAnimation(RightSwipe_E);
                starTimer();
                layout_A.setClickable(true);
                layout_B.setClickable(true);
                layout_C.setClickable(true);
                layout_D.setClickable(true);
                layout_E.setClickable(true);

            };
            handler.postDelayed(counter, 3500);

        } else {
            levelCompleted();
        }
    }

    public void levelCompleted() {
        Utils.TotalQuestion = questionList.size();
        Utils.CoreectQuetion = correctQuestion;
        Utils.WrongQuation = inCorrectQuestion;

        stopTimer();
        int total = questionList.size();
        int percent = (correctQuestion * 100) / total;

        if (percent >= 30 && percent < 40) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveOneCoin;
            level_coin = Constant.giveOneCoin;

        } else if (percent >= 40 && percent < 60) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveTwoCoins;
            level_coin = Constant.giveTwoCoins;

        } else if (percent >= 60 && percent < 80) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveThreeCoins;
            level_coin = Constant.giveThreeCoins;

        } else if (percent >= 80) {
            Constant.TOTAL_COINS = Constant.TOTAL_COINS + Constant.giveFourCoins;
            level_coin = Constant.giveFourCoins;

        } else
            level_coin = 0;


        Utils.level_score = score;

        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
        Intent dailyIntent = new Intent(activity, DailyCompleteActivity.class);
        dailyIntent.putExtra("fromQue", fromQue);
        dailyIntent.putExtra("type", types);
        startActivity(dailyIntent);
        finish();
        blankAllValue();

    }


    public void AddReview(Question question, TextView tvBtnOpt, RelativeLayout layout) {
        layout_A.setClickable(false);
        layout_B.setClickable(false);
        layout_C.setClickable(false);
        layout_D.setClickable(false);
        layout_E.setClickable(false);

        //System.out.println("ValuesOFAnswer::="+tvBtnOpt.getText().toString());
        if (tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            rightSound();

            layout.setBackgroundResource(R.drawable.right_gradientmaths);
            layout.startAnimation(animation);
            score = score + Constant.FOR_CORRECT_ANS;
            correctQuestion = correctQuestion + 1;

        } else {

            playWrongSound();
            layout.setBackgroundResource(R.drawable.wrong_gradientmaths);
            score = score - Constant.PENALTY;
            inCorrectQuestion = inCorrectQuestion + 1;

        }
        tvScore.setText(String.valueOf(score));
        question.setSelectedAns(tvBtnOpt.getText().toString());

        if (Constant.QUICK_ANSWER_ENABLE.equals("1"))
            RightAnswerBackgroundSet();
        question.setAttended(true);
        stopTimer();

        questionIndex++;
        mHandler.postDelayed(mUpdateUITimerTask, 1000);

    }

    public void RightAnswerBackgroundSet() {
        if (tvA.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_A.setBackgroundResource(R.drawable.right_gradientmaths);
            layout_A.startAnimation(animation);

        } else if (tvB.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_B.setBackgroundResource(R.drawable.right_gradientmaths);
            layout_B.startAnimation(animation);

        } else if (tvC.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_C.setBackgroundResource(R.drawable.right_gradientmaths);
            layout_C.startAnimation(animation);

        } else if (tvD.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_D.setBackgroundResource(R.drawable.right_gradientmaths);
            layout_D.startAnimation(animation);

        } else if (tvE.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_E.setBackgroundResource(R.drawable.right_gradientmaths);
            layout_E.startAnimation(animation);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            layout_E.setClickable(false);
            if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
                progressBarTwo_A.setVisibility(View.INVISIBLE);
                progressBarTwo_B.setVisibility(View.INVISIBLE);
                progressBarTwo_C.setVisibility(View.INVISIBLE);
                progressBarTwo_D.setVisibility(View.INVISIBLE);
                progressBarTwo_E.setVisibility(View.INVISIBLE);

            }
            switch (v.getId()) {
                case R.id.a_layout:
                    AddReview(question, tvA, layout_A);
                    break;

                case R.id.b_layout:
                    AddReview(question, tvB, layout_B);

                    break;
                case R.id.c_layout:
                    AddReview(question, tvC, layout_C);

                    break;
                case R.id.d_layout:
                    AddReview(question, tvD, layout_D);

                    break;
                case R.id.e_layout:
                    AddReview(question, tvE, layout_E);

                    break;
            }

        }
    }


    private final Runnable mUpdateUITimerTask = () -> {
        if (getApplicationContext() != null) {
            nextQuizQuestion();
        }
    };


    public void PlayAreaLeaveDialog() {
        if (isQuizStart) {
            stopTimer();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            // Setting Dialog Message
            alertDialog.setMessage(getResources().getString(R.string.exit_msg_quiz));
            alertDialog.setCancelable(false);
            final AlertDialog alertDialog1 = alertDialog.create();
            // Setting OK Button
            alertDialog.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                // Write your code here to execute after dialog closed
                stopTimer();
                leftTime = 0;
                Constant.LeftTime = 0;
                finish();

            });

            alertDialog.setNegativeButton(getString(R.string.no), (dialog, which) -> {
                alertDialog1.dismiss();
                Constant.LeftTime = leftTime;
                if (Constant.LeftTime != 0) {

                    timer = new Timer(leftTime, 1000);
                    timer.start();


                }
            });
            // Showing Alert Message
            alertDialog.show();
        } else {
            stopTimer();
            finish();
        }
    }


    //play sound when answer is correct
    public void rightSound() {
        if (Session.getSoundEnableDisable(activity))
            Utils.setrightAnssound(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);

    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(activity))
            Utils.setwronAnssound(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);
    }

    //set progress again after next question
    private void setAgain() {
        if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
            progressBarTwo_A.setVisibility(View.INVISIBLE);
            progressBarTwo_B.setVisibility(View.INVISIBLE);
            progressBarTwo_C.setVisibility(View.INVISIBLE);
            progressBarTwo_D.setVisibility(View.INVISIBLE);
            progressBarTwo_E.setVisibility(View.INVISIBLE);
        }
    }

    public void FiftyFifty(View view) {
        Utils.btnClick(view, activity);
        //CheckSound();
        if (!Session.isFiftyFiftyUsed(activity)) {
            if (Constant.IN_APP_MODE.equals("1")) {
                if (Constant.TOTAL_COINS >= Constant.FIFTY_AUD_COINS) {
                    btnPosition = 0;
                    if (Session.isLogin(getApplicationContext()))
                        Utils.UpdateCoin(getApplicationContext(), "-" + Constant.FIFTY_AUD_COINS);
                    coin_count.setText(String.valueOf(Constant.TOTAL_COINS - Constant.FIFTY_AUD_COINS));

                    if (tvA.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                        btnPosition = 1;
                    if (tvB.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                        btnPosition = 2;
                    if (tvC.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                        btnPosition = 3;
                    if (tvD.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                        btnPosition = 4;


                    if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                        FiftyFiftyWith_E();
                    else
                        FiftyFiftyWithout_E();

                    Session.setFifty_Fifty(activity);
                } else
                    ShowRewarded(activity, Constant.FIFTY_AUD_COINS);
            } else {
                btnPosition = 0;

                if (tvA.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 1;
                if (tvB.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 2;
                if (tvC.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 3;
                if (tvD.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
                    btnPosition = 4;

                if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                    FiftyFiftyWith_E();
                else
                    FiftyFiftyWithout_E();

                Session.setFifty_Fifty(activity);

            }


        } else
            AlreadyUsed();
    }

    public void FiftyFiftyWithout_E() {

        if (btnPosition == 1) {
            layout_B.startAnimation(fifty_fifty_anim);
            layout_C.startAnimation(fifty_fifty_anim);
            layout_B.setClickable(false);
            layout_C.setClickable(false);

        } else if (btnPosition == 2) {
            layout_C.startAnimation(fifty_fifty_anim);
            layout_D.startAnimation(fifty_fifty_anim);
            layout_C.setClickable(false);
            layout_D.setClickable(false);

        } else if (btnPosition == 3) {
            layout_D.startAnimation(fifty_fifty_anim);
            layout_A.startAnimation(fifty_fifty_anim);
            layout_D.setClickable(false);
            layout_A.setClickable(false);

        } else if (btnPosition == 4) {
            layout_A.startAnimation(fifty_fifty_anim);
            layout_B.startAnimation(fifty_fifty_anim);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
        }
    }

    public void FiftyFiftyWith_E() {
        if (tvE.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim())) {
            btnPosition = 5;
        }

        if (btnPosition == 1) {
            layout_B.startAnimation(fifty_fifty_anim);
            layout_C.startAnimation(fifty_fifty_anim);
            layout_B.setClickable(false);
            layout_C.setClickable(false);

        } else if (btnPosition == 2) {
            layout_C.startAnimation(fifty_fifty_anim);
            layout_D.startAnimation(fifty_fifty_anim);
            layout_C.setClickable(false);
            layout_D.setClickable(false);

        } else if (btnPosition == 3) {
            layout_D.startAnimation(fifty_fifty_anim);
            layout_E.startAnimation(fifty_fifty_anim);
            layout_D.setClickable(false);
            layout_E.setClickable(false);

        } else if (btnPosition == 4) {

            layout_E.startAnimation(fifty_fifty_anim);
            layout_A.startAnimation(fifty_fifty_anim);
            layout_E.setClickable(false);
            layout_A.setClickable(false);

        } else if (btnPosition == 5) {
            layout_A.startAnimation(fifty_fifty_anim);
            layout_B.startAnimation(fifty_fifty_anim);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
        }
    }

    public void SkipQuestion(View view) {
        Utils.btnClick(view, activity);
        // CheckSound();

        if (!Session.isSkipUsed(activity)) {
            if (Constant.IN_APP_MODE.equals("1")) {
                if (Constant.TOTAL_COINS >= Constant.RESET_SKIP_COINS) {
                    stopTimer();
                    leftTime = 0;
                    Constant.LeftTime = 0;

                    if (Session.isLogin(getApplicationContext()))
                        Utils.UpdateCoin(getApplicationContext(), "-" + Constant.RESET_SKIP_COINS);
                    coin_count.setText(String.valueOf(Constant.TOTAL_COINS - Constant.RESET_SKIP_COINS));
                    questionIndex++;
                    nextQuizQuestion();
                    Session.setSkip(activity);
                } else
                    ShowRewarded(activity, Constant.RESET_SKIP_COINS);
            } else {

                stopTimer();
                leftTime = 0;
                Constant.LeftTime = 0;

                questionIndex++;
                nextQuizQuestion();
                Session.setSkip(activity);

            }

        } else
            AlreadyUsed();
    }

    public void AudiencePoll(View view) {
        Utils.btnClick(view, activity);
        //  CheckSound();
        if (!Session.isAudiencePollUsed(activity)) {
            if (Constant.IN_APP_MODE.equals("1")) {
                if (Constant.TOTAL_COINS >= Constant.FIFTY_AUD_COINS) {
                    btnPosition = 0;

                    if (Session.isLogin(getApplicationContext()))
                        Utils.UpdateCoin(getApplicationContext(), "-" + Constant.FIFTY_AUD_COINS);
                    coin_count.setText(String.valueOf(Constant.TOTAL_COINS - Constant.FIFTY_AUD_COINS));
                    if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                        AudienceWith_E();
                    else
                        AudienceWithout_E();

                    Session.setAudiencePoll(activity);
                } else
                    ShowRewarded(activity, Constant.FIFTY_AUD_COINS);
            } else {

                btnPosition = 0;
                if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                    AudienceWith_E();
                else
                    AudienceWithout_E();

                Session.setAudiencePoll(activity);

            }

        } else
            AlreadyUsed();
    }

    public void AudienceWithout_E() {
        int min = 45;
        int max = 70;
        Random r = new Random();
        int A = r.nextInt(max - min + 1) + min;
        int remain1 = 100 - A;
        int B = r.nextInt(((remain1 - 10)) + 1);
        int remain2 = remain1 - B;
        int C = r.nextInt(((remain2 - 5)) + 1);
        int D = remain2 - C;
        progressBarTwo_A.setVisibility(View.VISIBLE);
        progressBarTwo_B.setVisibility(View.VISIBLE);
        progressBarTwo_C.setVisibility(View.VISIBLE);
        progressBarTwo_D.setVisibility(View.VISIBLE);

        if (tvA.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 1;
        if (tvB.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 2;
        if (tvC.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 3;
        if (tvD.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 4;

        if (btnPosition == 1) {
            progressBarTwo_A.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(B);
            progressBarTwo_C.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);

        } else if (btnPosition == 2) {
            progressBarTwo_B.setCurrentProgress(A);
            progressBarTwo_C.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(B);

        } else if (btnPosition == 3) {
            progressBarTwo_C.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(B);

        } else if (btnPosition == 4) {
            progressBarTwo_D.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(C);
            progressBarTwo_C.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(B);

        }
    }

    public void AudienceWith_E() {
        int min = 45;
        int max = 70;
        Random r = new Random();
        int A = r.nextInt(max - min + 1) + min;
        int remain1 = 100 - A;
        int B = r.nextInt(((remain1 - 8)) + 1);
        int remain2 = remain1 - B;
        int C = r.nextInt(((remain2 - 4)) + 1);
        int remain3 = remain2 - C;
        int D = r.nextInt(((remain3 - 2)) + 1);
        int E = remain3 - D;
        progressBarTwo_A.setVisibility(View.VISIBLE);
        progressBarTwo_B.setVisibility(View.VISIBLE);
        progressBarTwo_C.setVisibility(View.VISIBLE);
        progressBarTwo_D.setVisibility(View.VISIBLE);
        progressBarTwo_E.setVisibility(View.VISIBLE);

        if (tvA.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 1;
        if (tvB.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 2;
        if (tvC.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 3;
        if (tvD.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 4;
        if (tvE.getText().toString().trim().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns().trim()))
            btnPosition = 5;

        if (btnPosition == 1) {
            progressBarTwo_A.setCurrentProgress(A);
            progressBarTwo_B.setCurrentProgress(B);
            progressBarTwo_C.setCurrentProgress(C);
            progressBarTwo_D.setCurrentProgress(D);
            progressBarTwo_E.setCurrentProgress(E);

        } else if (btnPosition == 2) {

            progressBarTwo_B.setCurrentProgress(A);
            progressBarTwo_C.setCurrentProgress(B);
            progressBarTwo_D.setCurrentProgress(C);
            progressBarTwo_E.setCurrentProgress(D);
            progressBarTwo_A.setCurrentProgress(E);

        } else if (btnPosition == 3) {

            progressBarTwo_C.setCurrentProgress(A);
            progressBarTwo_D.setCurrentProgress(B);
            progressBarTwo_E.setCurrentProgress(C);
            progressBarTwo_A.setCurrentProgress(D);
            progressBarTwo_B.setCurrentProgress(E);

        } else if (btnPosition == 4) {

            progressBarTwo_D.setCurrentProgress(A);
            progressBarTwo_E.setCurrentProgress(B);
            progressBarTwo_A.setCurrentProgress(C);
            progressBarTwo_B.setCurrentProgress(D);
            progressBarTwo_C.setCurrentProgress(E);

        } else if (btnPosition == 5) {
            progressBarTwo_E.setCurrentProgress(A);
            progressBarTwo_A.setCurrentProgress(B);
            progressBarTwo_B.setCurrentProgress(C);
            progressBarTwo_C.setCurrentProgress(D);
            progressBarTwo_D.setCurrentProgress(E);
        }
    }

    public void ResetTimer(View view) {
        Utils.btnClick(view, activity);
        // CheckSound();
        if (!Session.isResetUsed(activity)) {
            if (Constant.IN_APP_MODE.equals("1")) {
                if (Constant.TOTAL_COINS >= Constant.RESET_SKIP_COINS) {

                    if (Session.isLogin(getApplicationContext()))
                        Utils.UpdateCoin(getApplicationContext(), "-" + Constant.RESET_SKIP_COINS);
                    coin_count.setText(String.valueOf(Constant.TOTAL_COINS - Constant.RESET_SKIP_COINS));
                    Constant.LeftTime = 0;
                    leftTime = 0;
                    stopTimer();
                    starTimer();

                    Session.setReset(activity);
                } else
                    ShowRewarded(activity, Constant.RESET_SKIP_COINS);
            } else {

                Constant.LeftTime = 0;
                leftTime = 0;
                stopTimer();
                starTimer();

                Session.setReset(activity);

            }

        } else
            AlreadyUsed();
    }

    //Show alert dialog when lifeline already used in current level
    public void AlreadyUsed() {
        stopTimer();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = (TextView) dialogView.findViewById(R.id.ok);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> {
            alertDialog.dismiss();
            if (leftTime != 0) {
                timer = new Timer(leftTime, 1000);
                timer.start();
            }
        });
    }

    public void BackButtonMethod() {

        CheckSound();
        PlayAreaLeaveDialog();

    }

    public void CheckSound() {

        if (Session.getSoundEnableDisable(activity))
            Utils.backSoundonclick(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);
    }

    public void SettingButtonMethod() {
        CheckSound();
        stopTimer();

        Intent intent = new Intent(activity, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    public void getQuestionsFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.Get_MatheQestion, "1");
        if (types.equals("subcate")) {
            params.put(Constant.subCategoryId, "" + Constant.SUB_CAT_ID);
        } else {
            params.put(Constant.category, "" + Constant.CATE_ID);
        }

        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));


        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        questionList = new ArrayList<>();
                        questionList.addAll(Utils.getMathsQuestions(jsonArray, activity, fromQue));
                        nextQuizQuestion();
                        innerLayout.setVisibility(View.VISIBLE);
                        isQuizStart = true;
                    } else {
                        isQuizStart = false;
                        NotEnoughQuestion();
                    }
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public void NotEnoughQuestion() {
        invalidateOptionsMenu();
        tvAlert.setText(getString(R.string.question_not_available));


        tvAlert.setVisibility(View.VISIBLE);
        tvBack.setVisibility(View.VISIBLE);
        innerLayout.setVisibility(View.GONE);
        lifelineLyt.setVisibility(View.GONE);

    }


    public void blankAllValue() {
        questionIndex = 0;
        count_question_completed = 0;
        score = 0;
        correctQuestion = 0;
        inCorrectQuestion = 0;
    }

    public void UpdateScore(final String score) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.setMonthlyLeaderboard, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.SCORE, score);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    //System.out.println("=== update " + response);
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    String message = obj.getString("message");
                    if (error) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public void SetUserStatistics(final String ttlQue, final String correct,
                                  final String percent) {
        Map<String, String> params = new HashMap<>();

        params.put(Constant.SET_USER_STATISTICS, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.QUESTION_ANSWERED, ttlQue);
        params.put(Constant.CORRECT_ANSWERS, correct);
        params.put(Constant.COINS, String.valueOf(Constant.TOTAL_COINS));
        params.put(Constant.RATIO, percent);
        params.put(Constant.cate_id, String.valueOf(Constant.CATE_ID));
        System.out.println("====s params " + params.toString());
        ApiConfig.RequestToVolley((result, response) -> {

        }, params);

    }

    //Show dialog for rewarded ad
    //if user has not enough coin to use lifeline
    @SuppressLint("SetTextI18n")
    public void ShowRewarded(final Activity activity, int coin) {
        stopTimer();
        if (!Utils.isNetworkAvailable(activity)) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("Internet Connection Error!");
            dialog.setMessage("Internet Connection Error! Please connect to working Internet connection");
            dialog.setOnCancelListener(dialogInterface -> {
                if (leftTime != 0) {
                    timer = new Timer(leftTime, 1000);
                    timer.start();
                }

            });
            dialog.setPositiveButton("OK", (dialogInterface, i) -> {
                if (leftTime != 0) {
                    timer = new Timer(leftTime, 1000);
                    timer.start();
                }

            });
            dialog.show();


        } else {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_alert_coin, null);
            dialog.setView(dialogView);
            TextView tvCoinMsg = dialogView.findViewById(R.id.tvCoinMSg);
            TextView skip = dialogView.findViewById(R.id.skip);
            TextView watchNow = dialogView.findViewById(R.id.watch_now);
            tvCoinMsg.setText(activity.getResources().getString(R.string.coin_message1) + coin + activity.getResources().getString(R.string.coin_message2));
            final AlertDialog alertDialog = dialog.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            alertDialog.setCancelable(false);
            skip.setOnClickListener(view -> {
                alertDialog.dismiss();
                isDialogOpen = false;
                if (leftTime != 0) {
                    timer = new Timer(leftTime, 1000);
                    timer.start();
                }
            });
            watchNow.setOnClickListener(view -> {
                if (Constant.ADS_TYPE.equals("1")) {
                    showRewardedGoogleVideos();
                } else {
                    showRewardedFacebookVideo();
                }

                alertDialog.dismiss();
                isDialogOpen = false;
            });
        }
    }

    public void showRewardedFacebookVideo() {
        if (rewardedVideoAd != null) {
            Activity activityContext = activity;

            // Check if rewardedVideoAd has been loaded successfully
            if (!rewardedVideoAd.isAdLoaded()) {
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if (rewardedVideoAd.isAdInvalidated()) {
                return;
            }
            rewardedVideoAd.show();
        } else {
            reWardsNotLoad();
        }
    }

    public void showRewardedGoogleVideos() {
        if (rewardedVideoAds != null) {
            Activity activityContext = activity;
            rewardedVideoAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    rewardedVideoAds = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    // Called when ad fails to show.

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    RewardsAdsFacebookLoad();
                    // Called when ad is dismissed.
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.

                }
            });
            rewardedVideoAds.show(activityContext, rewardItem -> {
                Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Constant.REWARD_COIN_VALUE);
                if (Session.isLogin(getApplicationContext()))
                    Utils.UpdateCoin(getApplicationContext(), String.valueOf(Constant.REWARD_COIN_VALUE));
                RewardsAdsFacebookLoad();
            });


        } else {
            reWardsNotLoad();
        }
    }

    public void reWardsNotLoad() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = dialogView.findViewById(R.id.ok);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        message.setText(getString(R.string.rewards_message));
        title.setText(getString(R.string.rewads_adstitle));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> {
            if (Constant.ADS_TYPE.equals("1")) {
                RewardsAdsGoogleLoads();
            } else {
                RewardsAdsFacebookLoad();
            }
            alertDialog.dismiss();

        });
    }

    public void SaveLevel() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SET_LEVEL_DATA, "1");
        params.put(Constant.LEVEL, String.valueOf(levelNo + 1));
        params.put(Constant.category, String.valueOf(Constant.CATE_ID));
        params.put(Constant.subCategoryId, String.valueOf(Constant.SUB_CAT_ID));
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        LevelActivity.levelNo = (levelNo + 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);


    }

    public void SetMark(final String like) {
        // progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SetBookmark, "1");
        params.put(Constant.status, like);
        params.put(Constant.Question_Id, "" + question.getId());
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        String message = jsonObject.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }


    public class Timer extends CountDownTimer {
        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            //   System.out.println("===  timer  " + millisUntilFinished);
            int progress = (int) (millisUntilFinished / 1000);

            if (progressTimer == null)
                progressTimer = findViewById(R.id.circleTimer);
            else
                progressTimer.setCurrentProgress(progress);

            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000)
                progressTimer.SetTimerAttributes(Color.RED, Color.RED);
            else
                progressTimer.SetTimerAttributes(Color.parseColor(Constant.PROGRESS_COLOR), Color.parseColor(Constant.PROGRESS_COLOR));

        }

        @Override
        public void onFinish() {
            if (questionIndex >= questionList.size()) {
                levelCompleted();
            } else {
                if (isOnBg) {
                    playWrongSound();
                }
                score = score - Constant.PENALTY;
                inCorrectQuestion = inCorrectQuestion + 1;
                tvScore.setText(String.valueOf(score));
              /*  tvWrong.setText(String.valueOf(inCorrectQuestion));
                wrongProgress.setProgress(inCorrectQuestion);*/
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }
            stopTimer();
        }
    }

    public void starTimer() {
        timer = new Timer(Constant.TIME_PER_MATHSQUESTION, Constant.COUNT_DOWN_TIMER);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        isOnBg = true;
        Utils.CheckBgMusic(activity);
        if (Constant.LeftTime != 0) {
            timer = new Timer(leftTime, 1000);
            timer.start();
        }
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));

    }

    @Override
    public void onPause() {
        super.onPause();
        isOnBg = false;
        AppController.StopSound();
        Constant.LeftTime = leftTime;
        stopTimer();

    }


    @Override
    public void onDestroy() {
        leftTime = 0;
        stopTimer();
        finish();
        blankAllValue();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        myMenu = menu;
        inflater.inflate(R.menu.main_menu, menu);

    /*    final MenuItem menuItem = menu.findItem(R.id.bookmark);
        menu.findItem(R.id.bookmark).setVisible(false);
        menuItem.setVisible(questionList != null && questionList.size() > 0);*/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.report).setVisible(false);
        menu.findItem(R.id.bookmark).setVisible(false);
      /*  final MenuItem menuItem = menu.findItem(R.id.bookmark);
        menuItem.setVisible(questionList != null && questionList.size() > 0);
        menuItem.setTitle("unmark");
        if (question != null) {
            if (Session.getBooleanValue(getApplicationContext(), "question_" + question.getId())) {
                menuItem.setIcon(R.drawable.ic_mark);
                menuItem.setTitle("mark");
            } else {
                menuItem.setIcon(R.drawable.ic_unmark);
                menuItem.setTitle("unmark");
            }
        }*/

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.bookmark:
                if (questionList != null && questionList.size() > 0 && question != null)
                    if (!Session.getBooleanValue(getApplicationContext(), "question_" + question.getId())) {
                        SetMark("1");
                        menuItem.setIcon(R.drawable.ic_mark);
                        menuItem.setTitle("mark");
                        Session.setMark(getApplicationContext(), "question_" + question.getId(), true);
                    } else {
                        SetMark("0");
                        menuItem.setIcon(R.drawable.ic_unmark);
                        menuItem.setTitle("unmark");
                        Session.setMark(getApplicationContext(), "question_" + question.getId(), false);
                    }
                return true;
            case R.id.setting:
                SettingButtonMethod();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {

        PlayAreaLeaveDialog();

    }
}