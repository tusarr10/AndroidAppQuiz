package com.wrteam.quiz.helper;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.login.LoginManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;


import com.wrteam.quiz.activity.LoginTabActivity;
import com.wrteam.quiz.model.Language;
import com.wrteam.quiz.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.wrteam.quiz.helper.AppController.StopSound;

public class Utils {

    public static InterstitialAd interstitialAd;
    public static NativeBannerAd nativeBannerAd;
    public static NativeAdLayout nativeAdLayout;
    public static AdView mAdView;
    public static com.google.android.gms.ads.AdView mAdViewS;
    public static TextToSpeech textToSpeech;
    public static AdRequest adRequest;
    public static LinearLayout adView;
    public static AlertDialog alertDialog;
    private static Vibrator sVibrator;
    public static int TotalQuestion = 1;
    public static int TotalTournment = 1;
    public static int CoreectQuetion = 1;
    public static int WrongQuation = 1;

    public static com.google.android.gms.ads.interstitial.InterstitialAd interstitial;
    public static int TournmentScore;

    public static int level_coin = 1;
    public static int level_score = 0;
    public static final long VIBRATION_DURATION = 100;


    @SuppressWarnings("unused")
    public final static double DOUBLE_EPSILON = Double.longBitsToDouble(1);

    @SuppressWarnings("unused")
    public final static float FLOAT_EPSILON = Float.intBitsToFloat(1);
    public static int RequestlevelNo = 1;
    public static final boolean DEFAULT_SOUND_SETTING = true;
    public static final boolean DEFAULT_VIBRATION_SETTING = true;
    public static final boolean DEFAULT_MUSIC_SETTING = false;


    public static void backSoundonclick(Context mContext) {
        try {
            int resourceId = R.raw.click2;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setrightAnssound(Context mContext) {
        try {
            int resourceId = R.raw.right;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.setOnCompletionListener(mp -> {

                mp.reset();
                mp.release();

            });
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CheckBgMusic(Activity activity) {
        if (Session.getMusicEnableDisable(activity))
            AppController.playSound();
        else
            StopSound();
    }

    public static void setwronAnssound(Context mContext) {
        try {
            int resourceId = R.raw.wrong;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.setOnCompletionListener(mp -> {

                mp.reset();
                mp.release();

            });
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void vibrate(Context context, long duration) {
        if (sVibrator == null) {
            sVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (sVibrator != null) {
            if (duration == 0) {
                duration = 50;
            }
            sVibrator.vibrate(duration);
        }
    }

    public static boolean isNetworkAvailable(Activity activity) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void loadAd(Activity activity) {
        if (Constant.IN_APP_MODE.equals("1")) {

            if (Constant.ADS_TYPE.equals("1")) {
                adRequest = new AdRequest.Builder().build();
                com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, Constant.ADMOB_INTERSTITIAL, adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitial = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {

                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                loadAd(activity);
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        interstitial = null;

                    }
                });
            } else {
                interstitialAd = new InterstitialAd(activity, Constant.FB_INTERSTITIAL);
                // Create listeners for the Interstitial Ad
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        // Interstitial ad displayed callback
                        //Log.e(TAG, "Interstitial ad displayed.");
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        // Interstitial dismissed callback
                        // Log.e(TAG, "Interstitial ad dismissed.");
                        loadAd(activity);
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Ad error callback
                        //Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        // Interstitial ad is loaded and ready to be displayed
                        // Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                        // Show the ad
                        //interstitialAd.show();
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Ad clicked callback
                        //Log.d(TAG, "Interstitial ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Ad impression logged callback
                        // Log.d(TAG, "Interstitial ad impression logged!");
                    }
                };

                // For auto play video ads, it's recommended to load the ad
                // at least 30 seconds before it is shown
                interstitialAd.loadAd(
                        interstitialAd.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build());
            }
            // Instantiate an InterstitialAd object.
            // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
            // now, while you are testing and replace it later when you have signed up.
            // While you are using this temporary code you will only get test ads and if you release
            // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
            //interstitialAd = new InterstitialAd(context, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID");

        }
    }


    public static void displayInterstitial(Activity activity) {

        if (Constant.ADS_TYPE.equals("1")) {
            if (interstitial != null) {
                interstitial.show(activity);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        } else {
            if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if (interstitialAd.isAdInvalidated()) {
                return;
            }
            // Show the ad
            interstitialAd.show();
        }
        // Check if interstitialAd has been loaded successfully
    }

    public static void showBannerAds(Activity activity) {
        if (Constant.IN_APP_MODE.equals("1")) {
            if (Constant.ADS_TYPE.equals("1")) {
                LinearLayout adBannerLayout = activity.findViewById(R.id.banner_AdView);
                mAdViewS = new com.google.android.gms.ads.AdView(activity);
                mAdViewS.setAdUnitId(Constant.ADMOB_BANNER);
                mAdViewS.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
                adBannerLayout.addView(mAdViewS);
                AdRequest adRequest;
                adRequest = new AdRequest.Builder().build();
                mAdViewS.loadAd(adRequest);
            } else {
                mAdView = new AdView(activity, Constant.FB_BANNER, AdSize.BANNER_HEIGHT_50);
                // Find the Ad Container
                LinearLayout adContainer = activity.findViewById(R.id.banner_AdView);
                // Add the ad view to your activity layout
                adContainer.addView(mAdView);
                // Request an ad
                mAdView.loadAd();
            }

        }
    }

    public static void CheckVibrateOrSound(Context context) {

        if (Session.getSoundEnableDisable(context)) {
            backSoundonclick(context);
        }
        if (Session.getVibration(context)) {
            vibrate(context, Utils.VIBRATION_DURATION);
        }
    }

    public static void InitializeTTF(Context context) {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                textToSpeech.setPitch(1.1f);

            }
        });
    }

    public static void btnClick(View view, Activity activity) {
        Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        CheckVibrateOrSound(activity);
    }

    public static void setDialogBg(AlertDialog alertDialog) {
        if (alertDialog != null)
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

    public static Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public static void saveImage(ScrollView scrollView, Activity activity) {
        try {

            Bitmap bitmap = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());
            File cachePath = new File(activity.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void ShareImage(Activity activity, String shareMsg) {
        File imagePath = new File(activity.getCacheDir(), "images");

        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", newFile);

        if (contentUri != null) {
            String shareBody = shareMsg;
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }

    }

    public static void ShareInfo(ScrollView scrollView, Activity activity, String shareMsg) {
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        new DownloadFiles(scrollView, pDialog, activity, shareMsg).execute();
    }

    public static class DownloadFiles extends AsyncTask<String, Integer, String> {
        ScrollView scrollView;
        ProgressDialog pDialog;
        Activity activity;
        String shareMsg;

        public DownloadFiles(ScrollView linearLayout, ProgressDialog pDialog, Activity activity, String shareMsg) {
            this.scrollView = linearLayout;
            this.pDialog = pDialog;
            this.activity = activity;
            this.shareMsg = shareMsg;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            saveImage(scrollView, activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog != null)
                pDialog.show();
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog != null)
                pDialog.dismiss();
            ShareImage(activity, shareMsg);
        }
    }

    public static void GetSystemConfig(final Context context) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SYSTEM_CONFIG, "1");
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);

                    System.out.println("ValueOfSysConfig::-"+response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject jsonobj = obj.getJSONObject("data");
                        Constant.APP_LINK = jsonobj.getString(Constant.KEY_APP_LINK);
                        Constant.MORE_APP_URL = jsonobj.getString(Constant.KEY_MORE_APP);
                        Constant.VERSION_CODE = jsonobj.getString(Constant.KEY_APP_VERSION);
                        Constant.REQUIRED_VERSION = jsonobj.getString(Constant.KEY_APP_VERSION);
                        Constant.LANGUAGE_MODE = jsonobj.getString(Constant.KEY_LANGUAGE_MODE);
                        Constant.IN_APP_PURCHASE = jsonobj.getString(Constant.KEY_IN_APP_PURCHASE_MODE);
                        Constant.OPTION_E_MODE = jsonobj.getString(Constant.KEY_OPTION_E_MODE);
                        Constant.SHARE_APP_TEXT = jsonobj.getString(Constant.KEY_SHARE_TEXT);
                        Constant.REFER_COIN_VALUE = jsonobj.getString(Constant.REFER_COIN);
                        Constant.EARN_COIN_VALUE = jsonobj.getString(Constant.EARN_COIN);
                        Constant.REWARD_COIN_VALUE = Integer.parseInt(jsonobj.getString(Constant.REWARD_COIN));
                        Constant.QUICK_ANSWER_ENABLE = jsonobj.getString(Constant.KEY_ANSWER_MODE);
                        Constant.Spin_ModeStatus=jsonobj.getString(Constant.SPIN_MODE);

                        Constant.WelComeCoins=jsonobj.getString(Constant.WELCOME_COINS);
                        Constant.DAILY_QUIZ_ON = jsonobj.getString(Constant.DailyQuizText);
                        if (jsonobj.has(Constant.TrueFalseText)) {
                            Constant.TRUE_FALSE_ON = jsonobj.getString(Constant.TrueFalseText);
                        }
                        Constant.CONTEST_ON = jsonobj.getString(Constant.ContestText);
                        Constant.FORCE_UPDATE = jsonobj.getString(Constant.ForceUpdateText);

                        Session.setBoolean(Session.GETDAILY, Constant.DAILY_QUIZ_ON.equals("1"), context);
                        Session.setBoolean(Session.TRUEFALSE, Constant.TRUE_FALSE_ON.equals("1"), context);
                        Session.setBoolean(Session.GETCONTEST, Constant.CONTEST_ON.equals("1"), context);

                        if (jsonobj.has(Constant.Ads_Type)) {
                            Constant.ADS_TYPE = jsonobj.getString(Constant.Ads_Type);
                        } else Constant.ADS_TYPE = "0";

                        if (jsonobj.has(Constant.INAppAdsMode)) {
                            Constant.IN_APP_MODE = jsonobj.getString(Constant.INAppAdsMode);
                        } else Constant.IN_APP_MODE = "0";


                        Constant.FB_INTERSTITIAL = jsonobj.getString(Constant.FbInterstitialAds);
                        Constant.FB_REWARDS_ADS = jsonobj.getString(Constant.FbRewardsAds);
                        Constant.FB_BANNER = jsonobj.getString(Constant.FbBannerAds);
                        Constant.FB_NATIVE = jsonobj.getString(Constant.FbNativeAds);


                        Constant.APP_ID = jsonobj.getString(Constant.AppID);
                        Constant.ADMOB_REWARDS_ADS = jsonobj.getString(Constant.AdmobRewardsAds);
                        Constant.ADMOB_INTERSTITIAL = jsonobj.getString(Constant.AdmobInterstitial);
                        Constant.ADMOB_BANNER = jsonobj.getString(Constant.AdmobBanner);
                        Constant.ADMOB_NATIVE = jsonobj.getString(Constant.AdmobNative);
                        Constant.ADMOB_OPEN_ADS = jsonobj.getString(Constant.AdmobOpenAds);

                        Session.setUserData(Session.OPEN_ADS, Constant.ADMOB_OPEN_ADS, context);
                        Session.setUserData(Session.INAPP_MODE, Constant.IN_APP_MODE, context);
                        Session.setUserData(Session.ADDTYPE, Constant.ADS_TYPE, context);


                        Constant.FACEBOOK_LINK = jsonobj.getString(Constant.KEY_FACEBOOK_LINK);
                        Constant.INSTAGRAM_LINK = jsonobj.getString(Constant.KEY_INSTAGRAM_LINK);
                        Constant.YOUTUBE_LINK = jsonobj.getString(Constant.KEY_YOUTUBE_LINK);


                        Session.setBoolean(Session.E_MODE, Constant.OPTION_E_MODE.equals("1"), context);
                        if (Constant.LANGUAGE_MODE.equals("1"))
                            Session.setBoolean(Session.LANG_MODE, true, context);
                        else {
                            Session.setBoolean(Session.LANG_MODE, false, context);
                            Session.setCurrentLanguage(Constant.D_LANG_ID, context);

                        }
                        Session.setBoolean(Session.STORE, Constant.IN_APP_PURCHASE.equals("1"), context);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public void LanguageDialog(Activity activity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater1.inflate(R.layout.language_dialog, null);
        dialog.setView(dialogView);
        RecyclerView languageView = dialogView.findViewById(R.id.recyclerView);
        languageView.setLayoutManager(new LinearLayoutManager(activity));
        alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Utils.GetLanguage(languageView, activity, alertDialog);
    }


    public static void postTokenToServer(final Context context, final String token) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.updateFcmId, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, context));
        params.put(Constant.fcmId, token);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        Session.setUserData(Session.FCM, token, context);
                        FirebaseDatabase.getInstance().getReference("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("fcm_id").setValue(token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    public static void UpdateCoin(final Context context, final String coins) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.setUserCoin, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, context));
        params.put(Constant.COINS, coins);
        System.out.println("Params::=" + params);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    System.out.println("Coins:=" + response);
                    JSONObject obj = new JSONObject(response);

                    if (obj.getString(Constant.ERROR).equals(Constant.FALSE)) {
                        JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                        Constant.TOTAL_COINS = Integer.parseInt(jsonObject.getString(Constant.COINS));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public static ArrayList<Question> getMathsQuestions(JSONArray jsonArray, Activity activity, String fromque) {
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Question question = new Question();
                JSONObject object = jsonArray.getJSONObject(i);
                question.setId(Integer.parseInt(object.getString(Constant.ID)));
                question.setQuestion(object.getString(Constant.QUESTION));

                question.setImage(object.getString(Constant.IMAGE));
                question.setNote(object.getString(Constant.NOTE));

                question.setQueType(object.getString(Constant.QUE_TYPE));
                question.addOption(object.getString(Constant.OPTION_A).trim());
                question.addOption(object.getString(Constant.OPTION_B).trim());
                question.addOption(object.getString(Constant.OPTION_C).trim());
                question.addOption(object.getString(Constant.OPTION_D).trim());
                if (Session.getBoolean(Session.E_MODE, activity)) {
                    if (!object.getString(Constant.OPTION_E).isEmpty() || !object.getString(Constant.OPTION_E).equals(""))
                        question.addOption(object.getString(Constant.OPTION_E).trim());
                }
                question.setSelectedOpt("none`");
                String rightAns = object.getString("answer");
                question.setAnsOption(rightAns);
                question.setTrueAns(object.getString("answer"));
              /*  if (rightAns.equalsIgnoreCase("A")) {
                    question.setTrueAns(object.getString(Constant.OPTION_A).trim());
                } else if (rightAns.equalsIgnoreCase("B")) {
                    question.setTrueAns(object.getString(Constant.OPTION_B).trim());
                } else if (rightAns.equalsIgnoreCase("C")) {
                    question.setTrueAns(object.getString(Constant.OPTION_C).trim());
                } else if (rightAns.equalsIgnoreCase("D")) {
                    question.setTrueAns(object.getString(Constant.OPTION_D).trim());
                } else if (rightAns.equalsIgnoreCase("E")) {
                    question.setTrueAns(object.getString(Constant.OPTION_E).trim());
                }*/


                questionList.add(question);


            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Excepation :=" + e);
        }
        return questionList;
    }


    public static ArrayList<Question> getQuestions(JSONArray jsonArray, Activity activity, String fromque) {
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Question question = new Question();
                JSONObject object = jsonArray.getJSONObject(i);
                question.setId(Integer.parseInt(object.getString(Constant.ID)));
                question.setQuestion(object.getString(Constant.QUESTION));

                if (!fromque.equals("learning")) {
                    question.setImage(object.getString(Constant.IMAGE));
                    question.setLevel(object.getString(Constant.LEVEL));
                    question.setNote(object.getString(Constant.NOTE));
                }
                question.setQueType(object.getString(Constant.QUE_TYPE));
                question.addOption(object.getString(Constant.OPTION_A).trim());
                question.addOption(object.getString(Constant.OPTION_B).trim());
                question.addOption(object.getString(Constant.OPTION_C).trim());
                question.addOption(object.getString(Constant.OPTION_D).trim());
                if (Session.getBoolean(Session.E_MODE, activity)) {
                    if (!object.getString(Constant.OPTION_E).isEmpty() || !object.getString(Constant.OPTION_E).equals(""))
                        question.addOption(object.getString(Constant.OPTION_E).trim());
                }
                question.setSelectedOpt("none");
                String rightAns = object.getString("answer");
                question.setAnsOption(rightAns);
                if (rightAns.equalsIgnoreCase("A")) {
                    question.setTrueAns(object.getString(Constant.OPTION_A).trim());
                } else if (rightAns.equalsIgnoreCase("B")) {
                    question.setTrueAns(object.getString(Constant.OPTION_B).trim());
                } else if (rightAns.equalsIgnoreCase("C")) {
                    question.setTrueAns(object.getString(Constant.OPTION_C).trim());
                } else if (rightAns.equalsIgnoreCase("D")) {
                    question.setTrueAns(object.getString(Constant.OPTION_D).trim());
                } else if (rightAns.equalsIgnoreCase("E")) {
                    question.setTrueAns(object.getString(Constant.OPTION_E).trim());
                }


                questionList.add(question);


            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Excepation :=" + e);
        }
        return questionList;
    }

    public static void SignOutWarningDialog(final Activity activity) {
        final android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_logout, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);

        final android.app.AlertDialog alertDialog = dialog1.create();
        TextView tvMessage = dialogView.findViewById(R.id.tv_message);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);

        TextView btnok = dialogView.findViewById(R.id.tvNo);
        TextView btnNo = dialogView.findViewById(R.id.tvYes);

        btnok.setOnClickListener(v -> alertDialog.dismiss());
        btnNo.setOnClickListener(view -> {
            Session.clearUserSession(activity);
            LoginManager.getInstance().logOut();
            LoginTabActivity.mAuth.signOut();
            FirebaseAuth.getInstance().signOut();
            Intent intentLogin = new Intent(activity, LoginTabActivity.class);
            activity.startActivity(intentLogin);
            activity.finish();

        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
/*        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        alertDialog.setMessage(activity.getResources().getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(activity);
                LoginManager.getInstance().logOut();
                LoginTabActivity.mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                Intent intentLogin = new Intent(activity, LoginTabActivity.class);
                activity.startActivity(intentLogin);
                activity.finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();*/
    }

    public static void transparentStatusAndNavigation(Activity context) {

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true, context);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, context);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public static void GetLanguage(final RecyclerView languageView, final Context context, final AlertDialog alertDialog) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_LANGUAGES, "1");
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        ArrayList<Language> languageList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Language language = new Language();
                            JSONObject object = jsonArray.getJSONObject(i);
                            language.setId(object.getString(Constant.ID));
                            language.setLanguage(object.getString(Constant.LANGUAGE));
                            languageList.add(language);
                        }
                        if (languageList.size() == 1) {
                            Session.setCurrentLanguage(languageList.get(0).getId(), context);
                            Session.setBoolean(Session.IS_FIRST_TIME, true, context);
                        }
                        LanguageAdapter languageAdapter = new LanguageAdapter(context, languageList, alertDialog);
                        languageView.setAdapter(languageAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);


    }

    public static class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ItemRowHolder> {
        private ArrayList<Language> dataList;
        private Context mContext;
        AlertDialog alertDialog;

        public LanguageAdapter(Context context, ArrayList<Language> dataList, AlertDialog alertDialog) {
            this.dataList = dataList;
            this.mContext = context;
            this.alertDialog = alertDialog;
        }

        @Override
        public LanguageAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_layout, parent, false);
            return new LanguageAdapter.ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull LanguageAdapter.ItemRowHolder holder, final int position) {

            final Language language = dataList.get(position);
            final ItemRowHolder itemRowHolder = holder;
            itemRowHolder.tvLanguage.setText(language.getLanguage());
            if (Session.getCurrentLanguage(mContext).equals(language.getId())) {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
            } else {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_unchecked);
            }
            itemRowHolder.radio.setOnClickListener(view -> {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
                Session.setCurrentLanguage(language.getId(), mContext);
                Session.setBoolean(Session.IS_FIRST_TIME, true, mContext);
                notifyDataSetChanged();
                alertDialog.dismiss();

            });

        }

        @Override
        public int getItemCount() {
            return (dataList.size());
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public ImageView radio;
            public TextView tvLanguage;


            public ItemRowHolder(View itemView) {
                super(itemView);
                radio = itemView.findViewById(R.id.radio);
                tvLanguage = itemView.findViewById(R.id.tvLanguage);
            }

        }

    }

    public static void ForgotPasswordPopUp(final Activity activity, final FirebaseAuth firebaseAuth) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lyt_forgot_password, null);
        dialog.setView(dialogView);
        TextView tvSubmit = dialogView.findViewById(R.id.tvSubmit);

        final EditText edtEmail = dialogView.findViewById(R.id.edtEmail);

        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        tvSubmit.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty())
                edtEmail.setError(activity.getResources().getString(R.string.email_alert_1));
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                edtEmail.setError(activity.getResources().getString(R.string.email_alert_2));
            else {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(activity, "Email sent", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
            }
        });

        alertDialog.show();
    }

    public static void LoadNativeAd(Activity activity) {
        if (Constant.IN_APP_MODE.equals("1")) {
            // Instantiate a NativeBannerAd object.
            // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
            // now, while you are testing and replace it later when you have signed up.
            // While you are using this temporary code you will only get test ads and if you release
            // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
            nativeBannerAd = new NativeBannerAd(activity, Constant.FB_NATIVE);
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e(AppController.TAG, "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e(AppController.TAG, "Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d(AppController.TAG, "Native ad is loaded and ready to be displayed!");
                    showNativeAdWithDelay(activity);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d(AppController.TAG, "Native ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d(AppController.TAG, "Native ad impression logged!");
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }

    }

    public static void inflateAd(NativeBannerAd nativeBannerAd, Activity activity) {

        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = activity.findViewById(R.id.native_banner_ad_container);
        LayoutInflater inflater = LayoutInflater.from(activity);
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.lyt_fb_native_ads, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    public static void showNativeAdWithDelay(Activity activity) {

        // Check if nativeAd has been loaded successfully
        if (nativeBannerAd == null || !nativeBannerAd.isAdLoaded()) {
            return;
        }
        // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
        if (nativeBannerAd.isAdInvalidated()) {
            return;
        }
        inflateAd(nativeBannerAd, activity); // Inflate NativeAd into a container, same as in previous code examples
    }


    public static void LoadNativeAds(final AppCompatActivity activity) {
        AdLoader.Builder builder = new AdLoader.Builder(activity, Constant.ADMOB_NATIVE)
                .forNativeAd(nativeAd -> {

                    FrameLayout frameLayout = activity.findViewById(R.id.adFrameLyt);
                    NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.lyt_native_ads, null);
                    // populateNativeAdView(unifiedNativeAd,adView);
                    populateNativeAdView(nativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                });

        // Load the Native ads.

        builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Handle the failure by logging, altering the UI, and so on.
            }

            @Override
            public void onAdClicked() {
                // Log the click event or other custom behavior.
            }
        }).build().loadAd(new AdRequest.Builder().build());

    }


    public static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd


        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.GONE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }


    public static void setWindowFlag(final int bits, boolean on, Activity context) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}