package com.wrteam.quiz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.android.volley.toolbox.NetworkImageView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.model.Category;
import com.wrteam.quiz.model.SubCategory;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubcategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    public ProgressBar progressBar;
    public ArrayList<SubCategory> subCateList;

    public TextView txtBlankList;
    public RelativeLayout layout;
    public static SubCategoryAdapter adapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Snackbar snackbar;
    public Toolbar toolbar;
    private ShimmerFrameLayout mShimmerViewContainer;

    String type;
    CardView cardQuiz;
    Activity activity;
    public EditText searchCate;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_category);
        activity = SubcategoryActivity.this;
        layout = findViewById(R.id.layout);
        type = getIntent().getStringExtra("catetype");
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Constant.cate_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Utils.displayInterstitial(SubcategoryActivity.this);
        Utils.loadAd(SubcategoryActivity.this);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        searchCate=findViewById(R.id.searchCate);
        cardQuiz=findViewById(R.id.cardQuiz);
        searchCate.setHint("Search Subcategory");
        searchCate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        txtBlankList = findViewById(R.id.txtblanklist);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        txtBlankList.setText(getString(R.string.no_category));

        subCateList = new ArrayList<>();
        getData();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            subCateList.clear();
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });
        Utils.showBannerAds(SubcategoryActivity.this);
    }

    void filter(String text){
        ArrayList<SubCategory> temp = new ArrayList();
        for(SubCategory d: subCateList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }


    private void getData() {
        mShimmerViewContainer.startShimmer();
        // progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(activity)) {
            getSubCategoryFromJson();

        } else {
            setSnackBar();
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
        }
    }

    public void getSubCategoryFromJson() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.getSubCategory, "1");
        params.put(Constant.categoryId, "" + Constant.CATE_ID);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    System.out.println("Responsesub::=" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);

                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            SubCategory subCate = new SubCategory();
                            JSONObject object = jsonArray.getJSONObject(i);
                            subCate.setId(object.getString(Constant.ID));
                            subCate.setCategoryId(object.getString(Constant.MAIN_CATE_ID));
                            subCate.setName(object.getString("subcategory_name"));
                            subCate.setImage(object.getString(Constant.IMAGE));
                            subCate.setStatus(object.getString(Constant.STATUS));
                                subCate.setMaxLevel(object.getString(Constant.MAX_LEVEL));
                                subCate.setNo_ofque(object.getString(Constant.NO_OF_CATE));
                            subCateList.add(subCate);
                        }
                        if (subCateList.size() == 0) {
                            txtBlankList.setVisibility(View.VISIBLE);
                            txtBlankList.setText(getString(R.string.no_sub_category));
                            mShimmerViewContainer.stopShimmer();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            cardQuiz.setVisibility(View.GONE);
                        }
                        adapter = new SubCategoryAdapter(activity, subCateList);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        cardQuiz.setVisibility(View.VISIBLE);
                    } else {
                        txtBlankList.setVisibility(View.VISIBLE);
                        txtBlankList.setText(getString(R.string.no_sub_category));
                        cardQuiz.setVisibility(View.GONE);
                    }
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }


    public void setSnackBar() {
        snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    private void setAnimation(View viewToAnimate, int position) {
        Context context = activity;
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

        animation.setDuration(position * 50 + 500);
        viewToAnimate.startAnimation(animation);

    }


    public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ItemRowHolder> {
        private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        private ArrayList<SubCategory> dataList;
        private Context mContext;

        public SubCategoryAdapter(Context context, ArrayList<SubCategory> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public SubCategoryAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SubCategoryAdapter.ItemRowHolder holder, final int position) {
            txtBlankList.setVisibility(View.GONE);
            final SubCategory subCate = dataList.get(position);
            holder.nameLyt.setBackgroundResource(Constant.colorBg[position % 6]);
            holder.imgCircles.setColorFilter(ContextCompat.getColor(activity, Constant.colors[position % 6]));
            holder.text.setText(subCate.getName());
            holder.noofque.setText(getString(R.string.que) + subCate.getNo_ofque());
            holder.image.setDefaultImageResId(R.drawable.ic_launcher);
            holder.image.setImageUrl(subCate.getImage(), imageLoader);
            setAnimation(holder.text, position);
            holder.relativeLayout.setOnClickListener(v -> {
                Constant.SUB_CAT_ID = subCate.getId();

                    if (subCate.getMaxLevel().isEmpty()) {
                        Constant.TotalLevel = 0;
                    } else {
                        Constant.TotalLevel = Integer.parseInt(subCate.getMaxLevel());
                    }

                    Intent intent = new Intent(activity, LevelActivity.class);
                    intent.putExtra("fromQue", "subCate");
                    startActivity(intent);

            });
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }


        public void updateList(ArrayList<SubCategory> list){
            dataList = list;
            notifyDataSetChanged();
        }
        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public NetworkImageView image;
            public TextView text, noofque;
            ImageView imgCircles;
            RelativeLayout relativeLayout, nameLyt;

            public ItemRowHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.cateImg);
                imgCircles = itemView.findViewById(R.id.imgCircles);
                text = itemView.findViewById(R.id.item_title);
                relativeLayout = itemView.findViewById(R.id.cat_layout);
                nameLyt = itemView.findViewById(R.id.nameLyt);
                noofque = itemView.findViewById(R.id.noofque);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.StopSound();
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    protected void onDestroy() {
        if (Utils.mAdView != null) {
            Utils.mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
        Utils.CheckBgMusic(activity);
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
                Utils.CheckVibrateOrSound(activity);
                Intent playQuiz = new Intent(activity, SettingActivity.class);
                startActivity(playQuiz);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}