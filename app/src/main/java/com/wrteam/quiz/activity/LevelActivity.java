package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import com.wrteam.quiz.helper.ApiConfig;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wrteam.quiz.R;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.model.Level;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelActivity extends AppCompatActivity {
    public Toolbar toolbar;
    LevelListAdapter adapter;
    public static String fromQue;
    List<Level> levelList;
    RecyclerView recyclerView;
    public TextView tvAlert;
    public ProgressBar progressBar;
    public RelativeLayout layout;
    public static int levelNo = 0;
    SwipeRefreshLayout swipeLyt;
    AppCompatActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity = LevelActivity.this;
        invalidateOptionsMenu();

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.select_level));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.showBannerAds(activity);

        progressBar = findViewById(R.id.progressBar);
        swipeLyt = findViewById(R.id.swipeLyt);
        recyclerView = findViewById(R.id.recyclerView);
        tvAlert = findViewById(R.id.tvAlert);

        recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        fromQue = getIntent().getStringExtra("fromQue");
        getLevel();

        swipeLyt.setOnRefreshListener(() -> {
            getLevel();
            swipeLyt.setRefreshing(false);
        });
    }


    private void getData() {
        if (Utils.isNetworkAvailable(activity)) {
            progressBar.setVisibility(View.GONE);
            adapter = new LevelListAdapter(activity, levelList);
            recyclerView.setAdapter(adapter);
        } else {
            setSnackBar();
        }
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(layout, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public void getLevel() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_LEVEL_DATA, "1");
        params.put(Constant.category, String.valueOf(Constant.CATE_ID));
        params.put(Constant.subCategoryId, String.valueOf(Constant.SUB_CAT_ID));
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    levelList = new ArrayList<>();

                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject object = obj.getJSONObject(Constant.DATA);
                        levelNo = (Integer.parseInt(object.getString(Constant.LEVEL)));
                        for (int i = 0; i < Constant.TotalLevel; i++) {
                            Level level = new Level();
                            level.setLevelNo(Integer.parseInt(object.getString(Constant.LEVEL)));
                            level.setLevel("" + (i + 1));
                            levelList.add(level);
                        }
                        if (levelList.size() == 0) {
                            tvAlert.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            getData();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }


    @Override
    protected void onDestroy() {
        if (Utils.mAdView != null) {
            Utils.mAdView.destroy();
        }
        super.onDestroy();
    }

    public class LevelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public Activity activity;
        private List<Level> levelList;

        public LevelListAdapter(Activity activity, List<Level> levelList) {
            this.levelList = levelList;
            this.activity = activity;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_lyt, parent, false);
            return new LevelViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            LevelViewHolder viewHolder = (LevelViewHolder) holder;
            Level level = levelList.get(position);
            viewHolder.levelNo.setText("" + level.getLevel());

            if (levelNo >= position + 1)
                viewHolder.lock.setImageResource(R.drawable.unlock);
            else
                viewHolder.lock.setImageResource(R.drawable.lock);

            if (position >= (levelNo - 1)) {
                viewHolder.lock.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary));
            } else {
                viewHolder.lock.setColorFilter(ContextCompat.getColor(activity, R.color.gray));
                viewHolder.levelNo.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            }

            viewHolder.imgCircles.setColorFilter(ContextCompat.getColor(activity, Constant.colors[position % 6]));
            viewHolder.relativeLayout.setOnClickListener(view -> {
                Utils.RequestlevelNo = position + 1;
                if (levelNo >= position + 1) {
                    Intent intent = new Intent(activity, PlayActivity.class);
                    intent.putExtra("fromQue", fromQue);
                    intent.putExtra("levelNo", levelNo);
                    startActivity(intent);
                } else {
                    Toast.makeText(activity, getString(R.string.level_locked), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return levelList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class LevelViewHolder extends RecyclerView.ViewHolder {

            ImageView lock, imgCircles;
            RelativeLayout relativeLayout;
            TextView levelNo;

            public LevelViewHolder(View itemView) {
                super(itemView);
                lock = itemView.findViewById(R.id.lock);
                imgCircles = itemView.findViewById(R.id.imgCircles);
                levelNo = itemView.findViewById(R.id.level_no);
                relativeLayout = itemView.findViewById(R.id.relativeLayout);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.bookmark).setVisible(false);
        menu.findItem(R.id.report).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.setting:
                Intent playQuiz = new Intent(activity, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            getLevel();
        }
    }
}

