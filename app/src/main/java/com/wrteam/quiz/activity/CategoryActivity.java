package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.shimmer.ShimmerFrameLayout;


import com.google.android.gms.common.data.DataHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.wrteam.quiz.battle.WaitingRoomActivity;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.Session;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.toolbox.ImageLoader;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.helper.Utils;

import com.wrteam.quiz.model.Category;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CategoryActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public ProgressBar progressBar;

    public TextView empty_msg;
    public RelativeLayout layout;
    public ArrayList<Category> categoryList;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Snackbar snackbar;
    public Toolbar toolbar;
    public AlertDialog alertDialog;
    public static CategoryAdapter adapter;
    public EditText searchCate;
    private ShimmerFrameLayout mShimmerViewContainer;
    CardView cardQuiz;
    String quizType;
    Activity activity;
    ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DrawerActivity.Locallanguage(CategoryActivity.this);
        setContentView(R.layout.activity_category);
        activity = CategoryActivity.this;
        quizType = getIntent().getStringExtra(Constant.QUIZ_TYPE);
        layout = findViewById(R.id.layout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.select_category));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Utils.displayInterstitial(CategoryActivity.this);
        Utils.loadAd(CategoryActivity.this);

        cardQuiz = findViewById(R.id.cardQuiz);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        empty_msg = findViewById(R.id.txtblanklist);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        searchCate = findViewById(R.id.searchCate);

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getData();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });

        Utils.showBannerAds(CategoryActivity.this);
        searchCate.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(CategoryActivity.this, searchCate.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }


    void filter(String text) {
        ArrayList<Category> temp = new ArrayList();
        for (Category d : categoryList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    private void getData() {
        //mShimmerViewContainer.startShimmerAnimation();
        //progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(activity)) {
            getMainCategoryFromJson();
            invalidateOptionsMenu();

        } else {
            setSnackBar();
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
        }

    }



    public void setSnackBar() {
        snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    @Override
    protected void onDestroy() {
        if (Utils.mAdView != null) {
            Utils.mAdView.destroy();
        }
        super.onDestroy();
    }

    /*
     * Get Quiz Category from Json
     */
    public void getMainCategoryFromJson() {
        // progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            params.put(Constant.GET_CATE_BY_LANG, "1");
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        } else
            params.put(Constant.getCategories, "1");
        if (quizType.equals(Constant.LEARNINGZONE)) {
            params.put(Constant.type, "2");
        } else if (quizType.equals(Constant.MATHSZONE)) {
            params.put(Constant.type, "3");
        } else {
            params.put(Constant.type, "1");
        }
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        System.out.println("ParmsGetValue::=" + params);
        ApiConfig.RequestToVolley((result, response) ->

        {

            if (result) {
                try {
                    categoryList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println("RealResponse::=" + response);

                    String error = jsonObject.getString(Constant.ERROR);

                    if (error.equalsIgnoreCase("false")) {
                        empty_msg.setVisibility(View.GONE);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setId(object.getString(Constant.ID));
                            category.setName(object.getString(Constant.CATEGORY_NAME));
                            category.setImage(object.getString(Constant.IMAGE));
                            if (!quizType.equals(Constant.LEARNINGZONE) && (!quizType.equals(Constant.MATHSZONE))) {
                                category.setMaxLevel(object.getString(Constant.MAX_LEVEL));
                                category.setPlan(object.getString(Constant.CATEGORY_PLAY));
                                category.setCateAmount(object.getString(Constant.CATEGORY_AMOUNT));
                                category.setPurchased(Boolean.parseBoolean(object.getString(Constant.ISPURCHASED)));
                            }
                            if (!quizType.equals(Constant.LEARNINGZONE)) {
                                category.setTtlQues(object.getString(Constant.NO_OF_QUES));
                            }
                            category.setNoOfCate(object.getString(Constant.NO_OF_CATE));
                       /*     if(Constant.IN_APP_MODE.equals("1")) {
                                if (i != 0 && i % 5 == 0) {
                                    categoryList.add(new Category(true));
                                }
                            }*/
                            categoryList.add(category);
                        }

                        adapter = new CategoryAdapter(activity, categoryList);
                        recyclerView.setAdapter(adapter);
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        cardQuiz.setVisibility(View.VISIBLE);

                    } else {

                        empty_msg.setText(getString(R.string.no_category));
                        progressBar.setVisibility(View.GONE);
                        empty_msg.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.stopShimmer();
                        cardQuiz.setVisibility(View.GONE);
                        mShimmerViewContainer.setVisibility(View.GONE);

                        if (adapter != null) {
                            adapter = new CategoryAdapter(activity, categoryList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("ValueGet::=" + e);
                }
            }
        }, params);


    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
        Utils.CheckBgMusic(activity);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppController.StopSound();
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        Context context = activity;
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

        animation.setDuration(position * 50L + 500);
        viewToAnimate.startAnimation(animation);

    }

    public void categoryCall(Category category) {
        if (!category.getNoOfCate().equals("0")) {
            Intent intent = new Intent(activity, SubcategoryActivity.class);
            startActivity(intent);
        } else {
            if (category.getMaxLevel() == null) {
                Constant.TotalLevel = 0;
            } else if (category.getMaxLevel().equals("null")) {
                Constant.TotalLevel = 0;
            } else {
                Constant.TotalLevel = Integer.parseInt(category.getMaxLevel());
            }
            Intent intent = new Intent(activity, LevelActivity.class);
            intent.putExtra("fromQue", "cate");
            startActivity(intent);
        }
    }

    public void zoneCall(Category category) {
        Intent intent;
        if (quizType.equals(Constant.PRACTICE)) {
            intent = new Intent(activity, PracticeQuiz.class);
            startActivity(intent);
        } else if (quizType.equals(Constant.MULTIPLAYER_ROOM)) {
            createGameRoom(activity);

        } else if (quizType.equals(Constant.BATTLE)) {
            intent = new Intent(activity, SearchPlayerActivity.class);
            startActivity(intent);
        }
    }


    public void catePurchase(String cateID, int position) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USERPURCHASE, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        params.put(Constant.cate_id, cateID);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    // System.out.println("ValuesOfResponse" + response);
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        if (adapter != null) {
                            adapter.dataList.get(position).setPurchased(true);
                        }
                        if (MainActivity.categoryList != null)
                            MainActivity.categoryList.get(position).setPurchased(true);
                        if (MainActivity.adapter != null) {
                            MainActivity.adapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    private void buyCategoryAlertDialog(String cateName, String cateCoins, String cateID, Category category, int position) {
        try {

            final android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_leave_battle, null);
            dialog1.setView(dialogView);
            dialog1.setCancelable(true);

            final android.app.AlertDialog alertDialog = dialog1.create();
            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            tvTitle.setText("Premium Category");
            TextView btnok = dialogView.findViewById(R.id.btn_ok);
            TextView btnNo = dialogView.findViewById(R.id.btnNo);
            tvMessage.setText("For Unlock " + cateName + " You need to pay " + cateCoins + " Coins");
            btnok.setOnClickListener(v -> {

                Constant.TOTAL_COINS = Constant.TOTAL_COINS - Integer.parseInt(cateCoins);
                // tvCoin.setText(String.valueOf(Constant.TOTAL_COINS));
                Utils.UpdateCoin(activity, "-" + cateCoins);
                catePurchase(cateID, position);
                Toast.makeText(activity, "Category Unlocked!! Enjoy Game", Toast.LENGTH_SHORT).show();
                categoryCall(category);

                alertDialog.dismiss();

            });
            btnNo.setOnClickListener(view -> {

                alertDialog.dismiss();
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buyZoneAlertDialog(String cateName, String cateCoins, String cateID, Category category, int position) {
        try {

            final android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_leave_battle, null);
            dialog1.setView(dialogView);
            dialog1.setCancelable(true);

            final android.app.AlertDialog alertDialog = dialog1.create();
            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            tvTitle.setText("Premium Category");
            TextView btnok = dialogView.findViewById(R.id.btn_ok);
            TextView btnNo = dialogView.findViewById(R.id.btnNo);
            tvMessage.setText("For Unlock " + cateName + " You need to pay " + cateCoins + " Coins");
            btnok.setOnClickListener(v -> {

                Constant.TOTAL_COINS = Constant.TOTAL_COINS - Integer.parseInt(cateCoins);

                Utils.UpdateCoin(activity, "-" + cateCoins);
                catePurchase(cateID, position);
                Toast.makeText(activity, "Category Unlocked!! Enjoy Game", Toast.LENGTH_SHORT).show();
                zoneCall(category);

                alertDialog.dismiss();

            });
            btnNo.setOnClickListener(view -> {

                alertDialog.dismiss();
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        ArrayList<Category> dataList;
        public Context mContext;
        final int MENU_ITEM_VIEW_TYPE = 0;
        final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

        public CategoryAdapter(Context context, ArrayList<Category> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);
            return new ItemRowHolder(v);

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, final int position) {


            DrawerActivity.Locallanguage(CategoryActivity.this);
            int viewType = getItemViewType(position);
            if (viewType == MENU_ITEM_VIEW_TYPE) {
                final ItemRowHolder holder = (ItemRowHolder) holder1;
                final Category category = dataList.get(position);
                holder.nameLyt.setBackgroundResource(Constant.colorBg[position % 6]);
                holder.imgCircles.setColorFilter(ContextCompat.getColor(activity, Constant.colors[position % 6]));
                holder.text.setText(category.getName());
                if (quizType.equals(Constant.MATHSZONE)) {
                    holder.noofque.setVisibility(View.GONE);
                } else {
                    holder.noofque.setText(quizType.equals(Constant.LEARNINGZONE) ? getString(R.string.avail_cate) + category.getNoOfCate() : getString(R.string.que) + category.getTtlQues());
                }
                //holder.noofque.setText(quizType.equals(Constant.LEARNINGZONE) ? getString(R.string.avail_cate) + category.getNoOfCate() : getString(R.string.que) + category.getTtlQues());
                holder.image.setDefaultImageResId(R.drawable.ic_launcher);
                holder.image.setImageUrl(category.getImage(), imageLoader);


                setAnimation(holder.text, position);
                if (!quizType.equals(Constant.LEARNINGZONE) && (!quizType.equals(Constant.MATHSZONE))) {
                    if (category.getPlan().equals("Free") || category.getPlan().equals("")) {
                        holder.relayPremium.setVisibility(View.GONE);
                    } else {
                        holder.relayPremium.setVisibility(View.VISIBLE);
                    }
                }

                holder.relativeLayout.setOnClickListener(v -> {
                    Constant.CATE_ID = category.getId();
                    Constant.cate_name = category.getName();
                    if (!quizType.equals(Constant.LEARNINGZONE)) {
                        if (quizType.equals(Constant.MATHSZONE)) {
                            if (!category.getNoOfCate().equals("0")) {
                                Intent intent = null;
                                intent = new Intent(activity, MathSubcategoryActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(activity, MathsPlayActivity.class);
                                intent.putExtra("fromQue", "mathsplay");
                                intent.putExtra("type", "cate");
                                startActivity(intent);
                            }
                        } else if (!category.getTtlQues().equals("0")) {
                            if (quizType.equals(Constant.REGULAR)) {
                                if (category.getPlan().equals("Free") || category.getPlan().equals("")) {
                                    categoryCall(category);
                                } else {
                                    if (category.isPurchased) {
                                        categoryCall(category);
                                    } else {
                                        if (Constant.TOTAL_COINS >= Integer.parseInt(category.getCateAmount())) {
                                            buyCategoryAlertDialog(category.getName(), category.getCateAmount(), category.getId(), category, position);
                                        } else {
                                            Toast.makeText(activity, getString(R.string.not_enough_coin), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            } else {
                                if (category.getPlan().equals("Free") || category.getPlan().equals("")) {
                                    zoneCall(category);
                                } else {
                                        if (category.isPurchased) {
                                            zoneCall(category);
                                        } else {
                                            if (Constant.TOTAL_COINS >= Integer.parseInt(category.getCateAmount())) {
                                                buyZoneAlertDialog(category.getName(), category.getCateAmount(), category.getId(), category, position);
                                            }else {
                                                Toast.makeText(activity, getString(R.string.not_enough_coin), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                }
                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.question_not_available), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent intent = new Intent(activity, LearningChapterActivity.class);
                        intent.putExtra("title", category.getName());
                        startActivity(intent);
                    }

                });
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }


        public void updateList(ArrayList<Category> list) {
            dataList = list;
            notifyDataSetChanged();
        }


        @Override
        public int getItemViewType(int position) {
            if (categoryList.get(position).isAdsShow()) {
                return UNIFIED_NATIVE_AD_VIEW_TYPE;
            }
            return MENU_ITEM_VIEW_TYPE;
        }


        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public NetworkImageView image;
            public TextView text, noofque;
            RelativeLayout relativeLayout, nameLyt, relayPremium;
            ImageView imgCircles;

            public ItemRowHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.cateImg);
                imgCircles = itemView.findViewById(R.id.imgCircles);
                text = itemView.findViewById(R.id.item_title);
                relativeLayout = itemView.findViewById(R.id.cat_layout);
                nameLyt = itemView.findViewById(R.id.nameLyt);
                noofque = itemView.findViewById(R.id.noofque);
                relayPremium = itemView.findViewById(R.id.relayPremium);
            }
        }

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
        } else if (itemId == R.id.setting) {
            Utils.CheckVibrateOrSound(activity);
            Intent playQuiz = new Intent(activity, SettingActivity.class);
            startActivity(playQuiz);
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void createGameRoom(Activity activity) {

        String gameCode = Constant.randomAlphaNumeric(5);
        showProgressDialog();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String authId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference dbRef = firebaseDatabase.getReference(Constant.MULTIPLAYER_ROOM).child(gameCode);
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.isRoomActive, Constant.TRUE);
        map.put(Constant.authId, authId);
        map.put(Constant.isStarted, Constant.FALSE);

        dbRef.setValue(map).addOnSuccessListener(aVoid -> {
            //Do what you need to do
            Toast.makeText(activity, getString(R.string.room_created_msg), Toast.LENGTH_SHORT).show();
            //room userDetail
            HashMap<String, String> map1 = new HashMap<>();
            map1.put(Constant.IMAGE, Session.getUserData(Session.PROFILE, activity));
            map1.put(Constant.NAME, Session.getUserData(Session.NAME, activity));
            map1.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, activity));
            dbRef.child(Constant.roomUser).setValue(map1);
            //join user detail
            HashMap<String, String> joinMap = new HashMap<>();
            joinMap.put(Constant.UID, authId);
            joinMap.put(Constant.IMAGE, Session.getUserData(Session.PROFILE, activity));
            joinMap.put(Constant.NAME, Session.getUserData(Session.NAME, activity));
            joinMap.put(Constant.IS_JOINED, "true");
            joinMap.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, activity));
            dbRef.child(Constant.joinUser).child(authId).setValue(joinMap);
            sendRoomDataOnServer(gameCode);
        });


    }

    public void sendRoomDataOnServer(final String roomId) {
        Map<String, String> params = new HashMap<>();
        params.put("create_room", "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put("room_id", roomId);
        params.put("room_type", "private");
        if (Constant.isGroupCateEnable)
            params.put("category", Constant.CATE_ID);
        params.put("no_of_que", "10");
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            params.put("language_id", Session.getUserData(Session.LANGUAGE, activity));
        }

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    hideProgressDialog();

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        Intent intent = new Intent(activity, WaitingRoomActivity.class);
                        intent.putExtra("from", "private");
                        intent.putExtra("roomKey", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        intent.putExtra("roomId", roomId);
                        intent.putExtra("type", "regular");
                        startActivity(intent);
                    } else
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}