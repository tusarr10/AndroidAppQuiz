package com.wrteam.quiz.spin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.activity.CategoryActivity;
import com.wrteam.quiz.activity.LevelActivity;
import com.wrteam.quiz.activity.PlayActivity;
import com.wrteam.quiz.activity.PrivacyPolicy;
import com.wrteam.quiz.activity.SettingActivity;
import com.wrteam.quiz.activity.SubcategoryActivity;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.model.Category;
import com.wrteam.quiz.model.LuckyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class SpinActivity extends AppCompatActivity {
    List<LuckyItem> data = new ArrayList<>();
    LuckyWheelView luckyWheelView;
    TextView play;
    boolean isColor;
    public Toolbar toolbar;
    int index;
    public boolean isDialogOpen = false;
    ProgressBar progressBar;
    RelativeLayout mainRelay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);
        luckyWheelView =  findViewById(R.id.luckyWheel);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Spin & Play");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=findViewById(R.id.progressBar);

        play = findViewById(R.id.play);

        mainRelay=findViewById(R.id.mainRelay);
        mainRelay.setVisibility(View.GONE);
        getMainCategoryFromJson();

        play.setOnClickListener(view -> {
            index = getRandomIndex();
            luckyWheelView.startLuckyWheelWithTargetIndex(index);
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(this::dialogSpin);
        Utils.showBannerAds(SpinActivity.this);
    }


    public void dialogSpin(int index) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(SpinActivity.this);
        LayoutInflater inflater = (LayoutInflater) SpinActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_spin, null);
        dialog.setView(dialogView);
        TextView tvCoinMsg = dialogView.findViewById(R.id.tvSpinmsg);
        TextView skip = dialogView.findViewById(R.id.cancel);
        TextView watchNow = dialogView.findViewById(R.id.play_now);

        String message ="Play " + data.get(index).secondaryText + " Category Get more score and top on Leaderboard!!";
        String s1=data.get(index).secondaryText;
        final Spannable wordtoSpan = new SpannableString(message);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLACK);
                ds.setTypeface(Typeface.DEFAULT_BOLD);
                ds.setUnderlineText(false);
            }
        }, message.indexOf(s1), message.indexOf(s1) + s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCoinMsg.setText(wordtoSpan);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        skip.setOnClickListener(view -> {
            alertDialog.dismiss();
            isDialogOpen = false;

        });
        watchNow.setOnClickListener(view -> {
            alertDialog.dismiss();
            isDialogOpen = false;
            Constant.CATE_ID = data.get(index).id;
            Constant.cate_name = data.get(index).secondaryText;
            if (!data.get(index).no_ofQuestion.equals("0")) {
                if (!data.get(index).no_ofCategory.equals("0")) {
                    Intent intent = null;
                    intent = new Intent(SpinActivity.this, SubcategoryActivity.class);
                    startActivity(intent);
                } else {
                    if (data.get(index).Level == null) {
                        Constant.TotalLevel = 0;
                    } else if (data.get(index).Level.equals("null")) {
                        Constant.TotalLevel = 0;
                    } else {
                        Constant.TotalLevel = Integer.parseInt(data.get(index).Level);
                    }
                    Intent intent = new Intent(SpinActivity.this, LevelActivity.class);
                    intent.putExtra("fromQue", "cate");
                    startActivity(intent);
                }
            } else {
                Toast.makeText(SpinActivity.this, getString(R.string.question_not_available), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMainCategoryFromJson() {
         progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            params.put(Constant.GET_CATE_BY_LANG, "1");
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        } else
            params.put(Constant.getCategories, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    data = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println("ResponseofSpinCate:-"+response);
                    String error = jsonObject.getString(Constant.ERROR);
                    if (error.equalsIgnoreCase("false")) {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            LuckyItem category = new LuckyItem();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setSecondaryText(object.getString(Constant.CATEGORY_NAME));
                            category.setIcon(object.getString(Constant.IMAGE));
                            category.setId(object.getString(Constant.ID));
                            category.setNo_ofCategory(object.getString(Constant.NO_OF_CATE));
                            category.setLevel(object.getString(Constant.MAX_LEVEL));
                            category.setPlan(object.getString(Constant.CATEGORY_PLAY));
                            category.setCateAmount(object.getString(Constant.CATEGORY_AMOUNT));
                            category.setPurchased(Boolean.parseBoolean(object.getString(Constant.ISPURCHASED)));
                            category.setNo_ofQuestion(object.getString(Constant.NO_OF_QUES));

                            if (isColor) {
                                category.setColor(getResources().getColor(R.color.colorPrimary));
                                isColor = false;
                            } else {
                                category.setColor(getResources().getColor(R.color.colorPrimaryDark));
                                isColor = true;
                            }
                            System.out.println("PlanOfCategory"+category.getPlan());
                            if (category.getPlan().equals("Free") || category.getPlan().equals("")) {
                                data.add(category);
                            }else {
                                System.out.println("PlanOfCategory"+category.isPurchased);
                                if(category.isPurchased){
                                    data.add(category);
                                }
                            }
                        }
                        mainRelay.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        luckyWheelView.setData(data);
                        luckyWheelView.setRound(5);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, params);


    }

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 1) + 0;
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cate_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
