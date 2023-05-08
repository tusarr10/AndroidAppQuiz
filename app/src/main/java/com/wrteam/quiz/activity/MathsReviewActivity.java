package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.fragment.MathsReviewFragment;
import com.wrteam.quiz.fragment.ReviewFragment;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.model.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MathsReviewActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    public ImageView prev, next;
    public ArrayList<Question> reviews;
    public Toolbar toolbar;
    public Menu menu;
    AlertDialog alertDialog;
    String from;
    TextView questionNo;
    RelativeLayout relayCount,bottomLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_review);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.review_answer));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        from = getIntent().getStringExtra("from");
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        relayCount=findViewById(R.id.relayCount);
        bottomLayout=findViewById(R.id.bottomLayout);
        bottomLayout.setVisibility(View.GONE);
        final Handler handler = new Handler();
        final Runnable counter = () -> {
            relayCount.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        };
        handler.postDelayed(counter, 3000);
        questionNo = findViewById(R.id.questionNo);

            reviews = MathsPlayActivity.questionList;

        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), reviews);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                questionNo.setText(((position+1)+"/"+reviews.size()));
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        questionNo.setText(((viewPager.getCurrentItem()+1)+"/"+reviews.size()));
        Utils.displayInterstitial(MathsReviewActivity.this);
        prev.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1));
        next.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1));


    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<Question> questionList;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Question> questionList) {
            super(fm);
            this.questionList = questionList;
        }

        @Override
        public Fragment getItem(int position) {
            return MathsReviewFragment.newInstance(position, questionList);
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

    }

    public void ReportDialog(final Question question) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MathsReviewActivity.this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.report_dialog, null);
        dialog.setView(dialogView);
        TextView tvReport = dialogView.findViewById(R.id.tvReport);
        TextView cancel = dialogView.findViewById(R.id.cancel);
        final EditText edtReport = dialogView.findViewById(R.id.edtReport);

        TextView tvQuestion = dialogView.findViewById(R.id.tvQuestion);
        tvQuestion.setText("Que : " + Html.fromHtml(question.getQuestion()));
        alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        tvReport.setOnClickListener(view -> {
            if (!edtReport.getText().toString().isEmpty()) {
                ReportQuestion(edtReport.getText().toString(), String.valueOf(question.getId()));
                edtReport.setError(null);
            } else {
                edtReport.setError("Please fill all the data and submit!");
            }
        });
        cancel.setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void ReportQuestion(final String message, final String qId) {
        Map<String, String> params = new HashMap<>();

        params.put(Constant.reportQuestion, "1");
        params.put(Constant.questionId, qId);
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        params.put(Constant.messageReport, message);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {

                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    String message1 = obj.getString("message");
                    if (!error) {
                        alertDialog.dismiss();
                    }
                    Toast.makeText(MathsReviewActivity.this, message1, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public void SetMark(final String like, final String id) {
        // progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();

        params.put(Constant.SetBookmark, "1");
        params.put(Constant.status, like);
        params.put(Constant.Question_Id, id);
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, MathsReviewActivity.this));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {

            }
        }, params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.setting).setVisible(false);
        menu.findItem(R.id.bookmark).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.report:
                ReportDialog(reviews.get(viewPager.getCurrentItem()));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}