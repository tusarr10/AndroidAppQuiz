package com.wrteam.quiz.activity;

import static com.wrteam.quiz.activity.MainActivity.Customadapters;
import static com.wrteam.quiz.activity.MainActivity.adapter;
import static com.wrteam.quiz.activity.MainActivity.arrayList;
import static com.wrteam.quiz.activity.MainActivity.item_title;
import static com.wrteam.quiz.activity.MainActivity.tvContest;
import static com.wrteam.quiz.activity.MainActivity.tvOneToOne;
import static com.wrteam.quiz.activity.MainActivity.tvViewAll;
import static com.wrteam.quiz.activity.MainActivity.tv_oto_play;
import static com.wrteam.quiz.activity.MainActivity.tv_play;
import static com.wrteam.quiz.activity.MainActivity.tv_random_play;
import static com.wrteam.quiz.activity.MainActivity.txtBattleZone;
import static com.wrteam.quiz.activity.MainActivity.txtContest;
import static com.wrteam.quiz.activity.MainActivity.txtContestZone;
import static com.wrteam.quiz.activity.MainActivity.txtPlayZpme;
import static com.wrteam.quiz.activity.MainActivity.txtQuiZone;
import static com.wrteam.quiz.activity.MainActivity.txt_random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.ArcNavigationView;
import com.wrteam.quiz.helper.CircleImageView;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.google.android.material.navigation.NavigationView;
import com.wrteam.quiz.model.Item;
import com.wrteam.quiz.spin.SpinActivity;

import java.util.ArrayList;
import java.util.Locale;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static ArcNavigationView navigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle drawerToggle;
    protected FrameLayout frameLayout;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public static CircleImageView imgProfile;
    public TextView tvEmail;
    public static TextView tvName;
    static final float END_SCALE = 0.7f;
    private static Locale myLocale;
    public static Configuration config;
    androidx.appcompat.app.AlertDialog alertDialogs;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparentStatusAndNavigation(DrawerActivity.this);
        setContentView(R.layout.activity_drawer);
        frameLayout = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        Utils.loadAd(DrawerActivity.this);
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                frameLayout.setScaleX(offsetScale);
                frameLayout.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = frameLayout.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                frameLayout.setTranslationX(xTranslation);
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {


            case R.id.leaderboard:

                Intent leaderBoard = new Intent(getApplicationContext(), LeaderboardTabActivity.class);
                startActivity(leaderBoard);
                drawerLayout.closeDrawers();
                break;

            case R.id.statistic:

                Intent statistic = new Intent(getApplicationContext(), UserStatistics.class);
                startActivity(statistic);

                Utils.displayInterstitial(DrawerActivity.this);
                drawerLayout.closeDrawers();
                break;

            case R.id.setting:
                Intent setting = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(setting);
                break;

            case R.id.notification:
                Intent notification = new Intent(getApplicationContext(), NotificationList.class);
                startActivity(notification);
                drawerLayout.closeDrawers();
                Utils.displayInterstitial(DrawerActivity.this);
                break;
            case R.id.bookmark:

                Intent bookmark = new Intent(getApplicationContext(), BookmarkList.class);
                startActivity(bookmark);
                drawerLayout.closeDrawers();
                Utils.displayInterstitial(DrawerActivity.this);
                break;

            case R.id.invite:

                Intent invite = new Intent(getApplicationContext(), InviteFriendActivity.class);
                startActivity(invite);
                Utils.displayInterstitial(DrawerActivity.this);
                drawerLayout.closeDrawers();
                break;

            case R.id.DevicLang:
                SignOutWarningDialog();
                break;

            case R.id.instruction:
                Intent instruction = new Intent(getApplicationContext(), InstructionActivity.class);
                instruction.putExtra("type", "instruction");
                startActivity(instruction);
                break;
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constant.SHARE_APP_TEXT + " " + Constant.APP_LINK);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
                break;
            case R.id.about:
                Intent about = new Intent(getApplicationContext(), PrivacyPolicy.class);
                about.putExtra("type", "about");
                startActivity(about);
                drawerLayout.closeDrawers();
                break;
            case R.id.terms:
                Intent terms = new Intent(getApplicationContext(), PrivacyPolicy.class);
                terms.putExtra("type", "terms");
                startActivity(terms);
                drawerLayout.closeDrawers();
                break;
            case R.id.privacy:
                Intent privacy = new Intent(getApplicationContext(), PrivacyPolicy.class);
                privacy.putExtra("type", "privacy");
                startActivity(privacy);
                drawerLayout.closeDrawers();
                break;
            case R.id.logout:
                Utils.SignOutWarningDialog(DrawerActivity.this);
                break;
            default:
        }
        return false;
    }


    public void SignOutWarningDialog() {
        final android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(DrawerActivity.this);
        LayoutInflater inflater = (LayoutInflater) DrawerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.applanguage_dialog, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);

        final android.app.AlertDialog alertDialog = dialog1.create();
        TextView tvenglish = dialogView.findViewById(R.id.tvenglish);
        TextView tvHindi = dialogView.findViewById(R.id.tvHindi);
        TextView tvArbic = dialogView.findViewById(R.id.tvArbic);


        tvenglish.setOnClickListener(view -> {
            changeLocale("en");
            alertDialog.dismiss();
        });
        tvHindi.setOnClickListener(view -> {
            changeLocale("hi");
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    public void changeLocale(String lang) {


        if (lang.equalsIgnoreCase(""))
            return;
        Locale locale = new Locale(lang);
        Session.setApplanguage(DrawerActivity.this, lang);
        Locale.setDefault(locale);
        Resources resources = getResources();
        config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu);
        onConfigurationChanged(config);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        txtBattleZone.setText(R.string.battle_zone);
        txtPlayZpme.setText(R.string.play_zone);
        tvViewAll.setText(R.string.view_all);
        txtQuiZone.setText(R.string.quiz_zone);
        item_title.setText(R.string.group_battle);
        tv_play.setText(R.string.play_now);
        tvOneToOne.setText(R.string.one_to_one_battle);
        tv_oto_play.setText(R.string.play_now);
        txt_random.setText(R.string.random_battle);
        tv_random_play.setText(R.string.play_now);
        txtContestZone.setText(R.string.contest_zone);
        txtContest.setText(R.string.contest_play);
        tvContest.setText(R.string.join_now);

        setDefaultQuiz();
        try {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            super.onConfigurationChanged(newConfig);
        } catch (NullPointerException e) {

        }

    }

    public void setDefaultQuiz() {
        Locallanguage(DrawerActivity.this);
        MainActivity.iconsName= new String[]{getResources().getString(R.string.daily_quiz), getResources().getString(R.string.random_quiz), getResources().getString(R.string.true_false), getResources().getString(R.string.self_challenge), getResources().getString(R.string.practice)};
        arrayList.clear();
        for (String s :  MainActivity.iconsName) {
            Item itemModel = new Item();
            itemModel.setName(s);
            if (s.equals(getString(R.string.daily_quiz))) {
                if (Session.getBoolean(Session.GETDAILY, DrawerActivity.this)) {
                    arrayList.add(itemModel);
                }
            } else {
                arrayList.add(itemModel);
            }

        }
        if (Customadapters != null) {
            Customadapters.notifyDataSetChanged();
        }
    }

    public static void Locallanguage(Activity activity){
        Locale locale = new Locale(Session.getApplanguage(activity));
        Session.setApplanguage(activity, Session.getApplanguage(activity));
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        DrawerActivity.config = resources.getConfiguration();
        DrawerActivity.config .setLocale(locale);
        resources.updateConfiguration(DrawerActivity.config , resources.getDisplayMetrics());
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        /*drawerToggle.syncState();*/
    }
}
