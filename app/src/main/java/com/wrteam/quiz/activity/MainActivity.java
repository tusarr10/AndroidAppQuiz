package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.toolbox.NetworkImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wrteam.quiz.R;
import com.wrteam.quiz.battle.WaitingRoomActivity;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.facebook.login.LoginManager;


import com.google.firebase.auth.FirebaseAuth;
import com.wrteam.quiz.model.Category;
import com.wrteam.quiz.model.Language;
import com.wrteam.quiz.model.Question;
import com.wrteam.quiz.model.Item;
import com.wrteam.quiz.model.Room;
import com.wrteam.quiz.one_to_one.OneToOneWait;
import com.wrteam.quiz.spin.SpinActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends DrawerActivity {

    public String type;
    public RelativeLayout lytCategory;
    ImageView imgBack, imgLanguage, imgStore;
    public String status = "0";
    public static TextView tvAlert, tvPlay, tvViewAll;
    public Toolbar toolbar;
    RelativeLayout lytContest, lytLearning, lytMaths;
    AlertDialog alertDialog, maintenanceDialog;
    public RecyclerView recyclerView, PlayMode;
    public static TextView tvName, tvScore, tvCoin, tvRank;
    public static CategoryAdapter adapter;
    public static ArrayList<Category> categoryList;
    public static ArrayList<Item> arrayList;
    public static String[] iconsName;
    AppCompatActivity activity;
    CardView spinCard;
    ProgressDialog mProgressDialog;
    String authId;
    public static CustomAdapter Customadapters;
    public static TextView txtBattleZone, txtPlayZpme, txtQuiZone, item_title, tv_play, tvOneToOne, tv_oto_play, txt_random, tv_random_play, txtContestZone, txtContest, tvContest;

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        activity = MainActivity.this;


        getAllWidgets();
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        authId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
        };
        tvViewAll.setOnClickListener(view -> openCategoryPage(Constant.REGULAR));

        tvName.setText(getString(R.string.hello) + Session.getUserData(Session.NAME, getApplicationContext()));
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, getApplicationContext()), imageLoader);
        imgProfile.setDefaultImageResId(R.drawable.ic_launcher);
        imgProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });

        tvRank.setOnClickListener(view -> {
            Intent leaderBoard = new Intent(activity, LeaderboardTabActivity.class);
            startActivity(leaderBoard);
        });

        imgStore.setVisibility(Session.getBoolean(Session.STORE, getApplicationContext()) ? View.VISIBLE : View.GONE);

        imgStore.setOnClickListener(view -> {
            Intent instruction = new Intent(getApplicationContext(), CoinStoreActivity.class);
            startActivity(instruction);
        });

        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            LanguageDialog(activity);
            if (!Session.getBoolean(Session.IS_FIRST_TIME, getApplicationContext())) {
                if (alertDialog != null)
                    alertDialog.show();
            } else {
                getMainCategoryFromJson();
            }
            imgLanguage.setVisibility(View.VISIBLE);
        } else {
            getMainCategoryFromJson();
            imgLanguage.setVisibility(View.GONE);
        }

        imgLanguage.setOnClickListener(view -> {
            if (alertDialog != null)
                alertDialog.show();
        });

        imgBack.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        type = getIntent().getStringExtra("type");
        assert type != null;

        if (!type.equals("null")) {
            if (type.equals("category")) {
                Constant.TotalLevel = Integer.parseInt(getIntent().getStringExtra("maxLevel"));
                Constant.CATE_ID = getIntent().getStringExtra("cateId");
                if (getIntent().getStringExtra("no_of").equals("0")) {
                    Intent intent = new Intent(activity, LevelActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, SubcategoryActivity.class);
                    startActivity(intent);
                }
            }
        }

        navigationView.getMenu().getItem(3).setActionView(R.layout.cart_count_layout);
        NavigationCartCount();


        if (LoginTabActivity.isNewuser) {
            Utils.UpdateCoin(MainActivity.this, String.valueOf(Constant.TOTAL_COINS));
        }

        if (Utils.isNetworkAvailable(activity)) {
            GetUserStatus();
            GetMark();
            GetUpdate(activity);

        }
        Utils.loadAd(MainActivity.this);
        DeleteOldGameRoom();
    }

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        tvViewAll = findViewById(R.id.tv_view_all);
        tvName = findViewById(R.id.tvName);
        imgProfile = findViewById(R.id.imgProfile);
        lytCategory = findViewById(R.id.lytCategory);
        tvScore = findViewById(R.id.tv_score);
        spinCard = findViewById(R.id.spinCard);

        tvCoin = findViewById(R.id.tv_coin);
        tvRank = findViewById(R.id.tv_rank);
        imgBack = findViewById(R.id.img_back_main);
        imgLanguage = findViewById(R.id.imgLanguage);
        imgStore = findViewById(R.id.imgStore);
        lytContest = findViewById(R.id.lytContest);
        tvAlert = findViewById(R.id.tvAlert);
        tvPlay = findViewById(R.id.tvPlay);
        txtBattleZone = findViewById(R.id.txtBattleZone);
        txtPlayZpme = findViewById(R.id.txtPlayZone);
        txtQuiZone = findViewById(R.id.txtQuiZone);
        item_title = findViewById(R.id.item_title);
        tv_play = findViewById(R.id.tv_play);
        tvOneToOne = findViewById(R.id.tvOneToOne);
        tv_oto_play = findViewById(R.id.tv_oto_play);
        txt_random = findViewById(R.id.txt_random);
        tv_random_play = findViewById(R.id.tv_random_play);
        txtContestZone = findViewById(R.id.txtContestZone);
        txtContest = findViewById(R.id.txtContest);
        tvContest = findViewById(R.id.tvContest);
        lytLearning = findViewById(R.id.lytLearning);
        lytMaths = findViewById(R.id.lytMaths);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PlayMode = findViewById(R.id.PlayMode);
        PlayMode.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.HORIZONTAL, false));
    }

    public void setDefaultQuiz() {
        iconsName = new String[]{getString(R.string.daily_quiz), getString(R.string.random_quiz), getString(R.string.true_false), getString(R.string.self_challenge), getString(R.string.practice)};
        arrayList = new ArrayList<>();
        for (String s : iconsName) {
            Item itemModel = new Item();
            itemModel.setName(s);
            if (s.equals(getString(R.string.daily_quiz))) {
                if (Session.getBoolean(Session.GETDAILY, activity)) {
                    arrayList.add(itemModel);
                }
            } else if (s.equals(getString(R.string.true_false))) {
                if (Session.getBoolean(Session.TRUEFALSE, activity)) {
                    arrayList.add(itemModel);
                }
            } else {
                arrayList.add(itemModel);
            }
/*

            if (s.equals(getString(R.string.true_false))) {
                System.out.println("GetEnough::="+Session.getBoolean(Session.TRUEFALSE, activity));
                if (Session.getBoolean(Session.TRUEFALSE, activity)) {
                    arrayList.a
                    dd(itemModel);
                }
            } else {
                arrayList.add(itemModel);
            }

*/

        }
        Customadapters = new CustomAdapter(getApplicationContext(), arrayList);
        PlayMode.setAdapter(Customadapters);
    }

    public void getMainCategoryFromJson() {
        // progressBar.setVisibility(View.VISIBLE);
        lytCategory.setVisibility(View.GONE);
        Map<String, String> params = new HashMap<>();
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            params.put(Constant.GET_CATE_BY_LANG, "1");
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        } else
            params.put(Constant.getCategories, "1");

        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));

        System.out.println("ValueParams::=" + params);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    categoryList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString(Constant.ERROR);
                    System.out.println("=====cate res " + response);
                    if (error.equalsIgnoreCase("false")) {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setId(object.getString(Constant.ID));
                            category.setName(object.getString(Constant.CATEGORY_NAME));
                            category.setImage(object.getString(Constant.IMAGE));
                            category.setMaxLevel(object.getString(Constant.MAX_LEVEL));
                            category.setTtlQues(object.getString(Constant.NO_OF_QUES));
                            category.setNoOfCate(object.getString(Constant.NO_OF_CATE));
                            category.setPlan(object.getString(Constant.CATEGORY_PLAY));
                            category.setCateAmount(object.getString(Constant.CATEGORY_AMOUNT));
                            category.setPurchased(Boolean.parseBoolean(object.getString(Constant.ISPURCHASED)));
                            categoryList.add(category);
                        }

                        adapter = new CategoryAdapter(activity, categoryList);
                        recyclerView.setAdapter(adapter);
                        lytCategory.setVisibility(View.VISIBLE);
                    } else {
                        lytCategory.setVisibility(View.GONE);
                        if (adapter != null) {
                            adapter = new CategoryAdapter(activity, categoryList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("Print::-" + e);
                }
            }
        }, params);
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
        super.onConfigurationChanged(newConfig);

    }


    public void ContestQuiz(View view) {
        Intent intent = new Intent(activity, ContestActivity.class);
        startActivity(intent);
    }

    public void LearningZone(View view) {
        openCategoryPage(Constant.LEARNINGZONE);
    }

    public void SpinQuiz(View view) {
        Intent spin = new Intent(getApplicationContext(), SpinActivity.class);
        startActivity(spin);
    }


    public void MathsZone(View view) {
        openCategoryPage(Constant.MATHSZONE);
    }

    public void RandomBattle(View view) {
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                if (alertDialog != null)
                    alertDialog.show();
            } else {
                searchPlayerCall();
            }
        } else {
            searchPlayerCall();
        }
    }

    public void searchPlayerCall() {
        if (Constant.isCateEnable)
            openCategoryPage(Constant.BATTLE);
        else
            startActivity(new Intent(activity, SearchPlayerActivity.class));
    }


    public void GroupBattle(View view) {
        JoinCreateDialog(Constant.MULTIPLAYER_ROOM);
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

    public void OneToOneBattle(View view) {
        JoinCreateDialog(Constant.ONE_TO_ONE);
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.viewHolder> {

        Context context;
        ArrayList<Item> arrayList;

        public CustomAdapter(Context context, ArrayList<Item> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view;
            view = LayoutInflater.from(context).inflate(R.layout.layout_playquiz, viewGroup, false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(viewHolder viewHolder, int position) {
            Item item = arrayList.get(position);
            viewHolder.iconName.setText(item.getName());
            viewHolder.noofcate.setText(getString(R.string.play_now));
            viewHolder.cardTitle.setBackgroundResource(Constant.gradientBG[position % 6]);
            viewHolder.cardTitle.setOnClickListener(v -> {
                if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                    if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                        if (alertDialog != null)
                            alertDialog.show();
                    } else {
                        setQuiz(item);
                    }
                    System.out.println("===== avail");
                } else {
                    setQuiz(item);
                    System.out.println("===== avail not");
                }
            });
        }


        public void setQuiz(Item item) {
            if (item.getName().equalsIgnoreCase(getString(R.string.daily_quiz))) {
                DailyRandomQuiz("daily");
            } else if (item.getName().equalsIgnoreCase(getString(R.string.random_quiz))) {
                DailyRandomQuiz("random");
            } else if (item.getName().equalsIgnoreCase(getString(R.string.self_challenge))) {
                startActivity(new Intent(activity, NewSelfChallengeActivity.class));
            } else if (item.getName().equalsIgnoreCase(getString(R.string.true_false))) {
                DailyRandomQuiz("true_false");
            } else if (item.getName().equalsIgnoreCase(getString(R.string.practice))) {
                openCategoryPage(Constant.PRACTICE);
            }
        }


        public void DailyRandomQuiz(String quizType) {
            Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
            intent.putExtra("fromQue", quizType);
            startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder {

            TextView iconName, noofcate;
            RelativeLayout cardTitle;

            public viewHolder(View itemView) {
                super(itemView);

                iconName = itemView.findViewById(R.id.item_title);
                cardTitle = itemView.findViewById(R.id.cardTitle);
                noofcate = itemView.findViewById(R.id.noofcate);
            }
        }

    }

    public void catePurchase(String cateID, int position) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USERPURCHASE, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        params.put(Constant.cate_id, cateID);
        System.out.println("purchaseCate" + params);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    System.out.println("ValuesOfResponse" + response);
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        if (adapter != null) {
                            adapter.dataList.get(position).setPurchased(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }


    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ItemRowHolder> {

        private final ArrayList<Category> dataList;
        public Context mContext;

        public CategoryAdapter(Context context, ArrayList<Category> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @NonNull
        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_maincat, parent, false);
            return new ItemRowHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {

            final Category category = dataList.get(position);

            holder.text.setText(category.getName());
            holder.image.setDefaultImageResId(R.drawable.ic_launcher);
            holder.image.setImageUrl(category.getImage(), imageLoader);

            System.out.println("Plan::=" + category.getPlan());

            if (category.getPlan().equals("Free") || category.getPlan().equals("")) {
                holder.relayPremium.setVisibility(View.GONE);
            } else {
                holder.relayPremium.setVisibility(View.VISIBLE);
            }


            holder.tvNoOfQue.setText(getString(R.string.que) + category.getTtlQues());
            holder.noofcat.setText(getString(R.string.category) + category.getNoOfCate());
            holder.relativeLayout.setOnClickListener(v -> {

                Constant.CATE_ID = category.getId();
                Constant.cate_name = category.getName();
                if (!category.getTtlQues().equals("0")) {
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
                    Toast.makeText(activity, getString(R.string.question_not_available), Toast.LENGTH_SHORT).show();
                }


            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public NetworkImageView image;
            public TextView text, tvNoOfQue, noofcat;
            RelativeLayout relativeLayout, cardTitle, relayPremium;

            public ItemRowHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.cateImg);
                text = itemView.findViewById(R.id.item_title);
                relativeLayout = itemView.findViewById(R.id.cat_layout);
                tvNoOfQue = itemView.findViewById(R.id.noofque);
                noofcat = itemView.findViewById(R.id.noofcate);
                relayPremium = itemView.findViewById(R.id.relayPremium);
                cardTitle = itemView.findViewById(R.id.cardTitle);
            }
        }
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
                tvCoin.setText(String.valueOf(Constant.TOTAL_COINS));
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

    public void GetMark() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GetBookmark, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {
            System.out.println("========search result " + response);
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Question question = new Question();
                            JSONObject object = jsonArray.getJSONObject(i);
                            question.setId(Integer.parseInt(object.getString(Constant.ID)));
                            Session.setMark(getApplicationContext(), "question_" + question.getId(), true);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    public void GetUpdate(final Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SYSTEM_CONFIG, "1");
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);

                    System.out.println("Learning::=" + response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject jsonobj = obj.getJSONObject("data");
                        Constant.APP_LINK = jsonobj.getString(Constant.KEY_APP_LINK);
                        Constant.MORE_APP_URL = jsonobj.getString(Constant.KEY_MORE_APP);
                        Constant.VERSION_CODE = jsonobj.getString(Constant.KEY_APP_VERSION);
                        Constant.REQUIRED_VERSION = jsonobj.getString(Constant.KEY_APP_VERSION);
                        Constant.DAILY_QUIZ_ON = jsonobj.getString(Constant.DailyQuizText);
                        Constant.Spin_ModeStatus = jsonobj.getString(Constant.SPIN_MODE);
                        if (Constant.Spin_ModeStatus.equals("1")) {
                            spinCard.setVisibility(View.VISIBLE);
                        } else {
                            spinCard.setVisibility(View.GONE);
                        }
                        if (jsonobj.has(Constant.TrueFalseText)) {
                            Constant.TRUE_FALSE_ON = jsonobj.getString(Constant.TrueFalseText);
                        }
                        Constant.CONTEST_ON = jsonobj.getString(Constant.ContestText);
                        Constant.FORCE_UPDATE = jsonobj.getString(Constant.ForceUpdateText);

                        if (jsonobj.has(Constant.Maintenance_Status)) {
                            Constant.MAINTENANCESTATUS = jsonobj.getString(Constant.Maintenance_Status);
                            if (Constant.MAINTENANCESTATUS.equals("1")) {
                                if (jsonobj.has(Constant.Maintenance_Message)) {
                                    Constant.MAINTENANCEMESSAGE = jsonobj.getString(Constant.Maintenance_Message);
                                }
                                showMaintenanceDialog();
                                return;
                            }
                        } else Constant.MAINTENANCESTATUS = "0";


                        if (jsonobj.has(Constant.LearningZoneMode)) {

                            Constant.LEARNINGZONEMODE = jsonobj.getString(Constant.LearningZoneMode);
                            if (Constant.LEARNINGZONEMODE.equals("1")) {
                                lytLearning.setVisibility(View.VISIBLE);
                            } else {
                                lytLearning.setVisibility(View.GONE);
                            }
                        } else Constant.LEARNINGZONEMODE = "0";


                        if (jsonobj.has(Constant.MathsZoneMode)) {

                            Constant.MATHSZONEMODE = jsonobj.getString(Constant.MathsZoneMode);
                            if (Constant.MATHSZONEMODE.equals("1")) {
                                lytMaths.setVisibility(View.VISIBLE);
                            } else {
                                lytMaths.setVisibility(View.GONE);
                            }
                        } else Constant.MATHSZONEMODE = "0";

                        if (jsonobj.has(Constant.RANDOM_BATTLE_CATE_MODE))
                            Constant.isCateEnable = jsonobj.getString(Constant.RANDOM_BATTLE_CATE_MODE).equals("1");
                        if (jsonobj.has(Constant.GROUP_BATTLE_CATE_MODE))
                            Constant.isGroupCateEnable = jsonobj.getString(Constant.GROUP_BATTLE_CATE_MODE).equals("1");
                        Session.setBoolean(Session.GETDAILY, Constant.DAILY_QUIZ_ON.equals("1"), activity);
                        Session.setBoolean(Session.TRUEFALSE, Constant.TRUE_FALSE_ON.equals("1"), activity);
                        Session.setBoolean(Session.GETCONTEST, Constant.CONTEST_ON.equals("1"), activity);
                        lytContest.setVisibility(Session.getBoolean(Session.GETCONTEST, activity) ? View.VISIBLE : View.GONE);

                        setDefaultQuiz();
                        String versionName = "";
                        try {
                            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                            versionName = packageInfo.versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        if (Constant.FORCE_UPDATE.equals("1")) {
                            if (compareVersion(versionName, Constant.VERSION_CODE) < 0) {
                                OpenBottomDialog(activity);
                            } else if (compareVersion(versionName, Constant.REQUIRED_VERSION) < 0) {
                                OpenBottomDialog(activity);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }


    public void showMaintenanceDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_maintaince, null);
        dialog.setView(dialogView);
        TextView message = dialogView.findViewById(R.id.text);
        TextView btnBack = dialogView.findViewById(R.id.btnBack);
        message.setText(Constant.MAINTENANCEMESSAGE);
        maintenanceDialog = dialog.create();
        Utils.setDialogBg(maintenanceDialog);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maintenanceDialog.dismiss();
                onBackPressed();
            }
        });
        maintenanceDialog.setCancelable(false);
        maintenanceDialog.setOnKeyListener((dialog1, keyCode, event) -> {
            // Disable Back key and Search key
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
                maintenanceDialog.dismiss();
                return true;
            }
            return false;
        });
        maintenanceDialog.show();
    }


    public static void OpenBottomDialog(final Activity activity) {
        View sheetView = activity.getLayoutInflater().inflate(R.layout.lyt_terms_privacy, null);
        ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeAllViews();
        }

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        ImageView imgclose = sheetView.findViewById(R.id.imgclose);
        Button btnNotNow = sheetView.findViewById(R.id.btnNotNow);
        Button btnUpadateNow = sheetView.findViewById(R.id.btnUpdateNow);

        mBottomSheetDialog.setCancelable(false);


        imgclose.setOnClickListener(v -> {
            if (mBottomSheetDialog.isShowing())
                mBottomSheetDialog.dismiss();
        });
        btnNotNow.setOnClickListener(v -> {
            if (mBottomSheetDialog.isShowing())
                mBottomSheetDialog.dismiss();
        });

        btnUpadateNow.setOnClickListener(view -> {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK)));
            System.out.println("Packge Name::=" + Constant.APP_LINK + activity.getPackageName());

        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");

        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
                    return -1;
                } else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
                    return 1;
                }
            } else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0) {
                    return 1;
                }
            } else {
                if (Integer.parseInt(arr2[i]) != 0) {
                    return -1;
                }
            }

            i++;
        }
        return 0;
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
        GetLanguage(languageView, activity, alertDialog);
    }

    public void GetUserStatus() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_BY_ID, "1");
        params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {

                    JSONObject obj = new JSONObject(response);
                    System.out.println("ValueOfGetUserById;;=" + response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject jsonobj = obj.getJSONObject("data");
                        if (jsonobj.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                            Session.clearUserSession(getApplicationContext());
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            Intent intentLogin = new Intent(activity, LoginTabActivity.class);
                            startActivity(intentLogin);
                            finish();
                        } else {
                            if (LoginTabActivity.isNewuser) {
                                // Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                                String numberString;
                                if (Math.abs(Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000000) > 1) {
                                    numberString = (Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000000) + "M";

                                } else if (Math.abs(Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000) > 1) {
                                    numberString = (Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000) + "K";

                                } else {
                                    numberString = String.valueOf(Constant.TOTAL_COINS);

                                }
                                tvCoin.setText(numberString);

                                LoginTabActivity.isNewuser = false;


                            } else {
                                Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                                String numberString;
                                if (Math.abs(Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000000) > 1) {
                                    numberString = (Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000000) + "M";

                                } else if (Math.abs(Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000) > 1) {
                                    numberString = (Integer.parseInt(String.valueOf(Constant.TOTAL_COINS)) / 1000) + "K";

                                } else {
                                    numberString = String.valueOf(Constant.TOTAL_COINS);

                                }
                                tvCoin.setText(numberString);
                            }

                            tvRank.setText(jsonobj.getString(Constant.GLOBAL_RANK));
                            tvScore.setText(jsonobj.getString(Constant.GLOBAL_SCORE));
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                try {
                                    String token = task.getResult();
                                    System.out.println("TokenGet:=" + token);
                                    if (!token.equals(Session.getUserData(Session.FCM, getApplicationContext()))) {
                                        Utils.postTokenToServer(getApplicationContext(), token);
                                    }
                                } catch (Exception e) {
                                }
                            });
                            // Utils.RemoveGameRoomId(FirebaseAuth.getInstance().getUid());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    public void LeaderBoard(View view) {
        Utils.btnClick(view, activity);
        startActivity(new Intent(activity, LeaderboardTabActivity.class));
    }

    public void Logout(View view) {
        Utils.btnClick(view, activity);
        Utils.SignOutWarningDialog(activity);
    }

    public void createGameRoom(Activity activity, String type) {
        String gameCode = Constant.randomAlphaNumeric(5);
        showProgressDialog();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String authId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference dbRef = firebaseDatabase.getReference(type).child(gameCode);
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.isRoomActive, Constant.TRUE);
        map.put(Constant.authId, authId);
        map.put(Constant.isStarted, Constant.FALSE);
        map.put(Constant.IS_JOINED, Constant.FALSE);


        dbRef.setValue(map).addOnSuccessListener(aVoid -> {
            //Do what you need to do
            Toast.makeText(activity, getString(R.string.room_created_msg), Toast.LENGTH_SHORT).show();

            //join user detail
            HashMap<String, String> joinMap = new HashMap<>();
            joinMap.put(Constant.UID, authId);
            joinMap.put(Constant.IMAGE, Session.getUserData(Session.PROFILE, activity));
            joinMap.put(Constant.NAME, Session.getUserData(Session.NAME, activity));
            joinMap.put(Constant.IS_JOINED, Constant.TRUE);
            joinMap.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, activity));
            dbRef.child(Constant.joinUser).child(authId).setValue(joinMap);
            sendRoomDataOnServer(gameCode, type);
        });
    }

    public void sendRoomDataOnServer(final String roomId, String type) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CREATE_ROOM, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.ROOM_ID, roomId);
        params.put(Constant.roomType, "private");
        params.put(Constant.NO_OF_QUES, "10");
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
            params.put(Constant.LANGUAGE_ID, Session.getUserData(Session.LANGUAGE, activity));

        System.out.println("ValueGET::=" + params);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    hideProgressDialog();

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        Intent intent;
                        if (type.equalsIgnoreCase(Constant.MULTIPLAYER_ROOM))
                            intent = new Intent(activity, WaitingRoomActivity.class);
                        else
                            intent = new Intent(activity, OneToOneWait.class);
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

    public void NavigationCartCount() {

        View viewCount = navigationView.getMenu().getItem(3).setActionView(R.layout.cart_count_layout).getActionView();
        TextView tvCount = viewCount.findViewById(R.id.tvCount);
        if (Session.getNCount(getApplicationContext()) == 0) {
            tvCount.setVisibility(View.GONE);
        } else {
            tvCount.setVisibility(View.VISIBLE);
        }
        tvCount.setText(String.valueOf(Session.getNCount(getApplicationContext())));
    }

    public void JoinCreateDialog(String type) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_join_create_battle, null);
        dialog.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvCreate = dialogView.findViewById(R.id.tvCreate);
        TextView tvJoin = dialogView.findViewById(R.id.tvJoin);
        if (type.equalsIgnoreCase(Constant.MULTIPLAYER_ROOM))
            tvTitle.setText(activity.getResources().getString(R.string.group_battle));
        else
            tvTitle.setText(activity.getResources().getString(R.string.one_to_one_battle));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        tvCreate.setOnClickListener(view -> {
            alertDialog.dismiss();

            if (type.equals(Constant.ONE_TO_ONE)) {
                createGameRoom(activity, type);
            } else {
                if (Constant.isGroupCateEnable) {
                    openCategoryPage(Constant.MULTIPLAYER_ROOM);
                } else
                    createGameRoom(activity, type);
            }

        });

        tvJoin.setOnClickListener(view -> {
            alertDialog.dismiss();
            JoinRoom(type);
        });

    }

    public void GetLanguage(final RecyclerView languageView, final Context context, final AlertDialog alertDialog) {
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

    public class LanguageAdapter extends RecyclerView.Adapter<MainActivity.LanguageAdapter.ItemRowHolder> {
        private final ArrayList<Language> dataList;
        private final Context mContext;
        AlertDialog alertDialog;

        public LanguageAdapter(Context context, ArrayList<Language> dataList, AlertDialog alertDialog) {
            this.dataList = dataList;
            this.mContext = context;
            this.alertDialog = alertDialog;
        }

        @NonNull
        @Override
        public MainActivity.LanguageAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_layout, parent, false);
            return new MainActivity.LanguageAdapter.ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MainActivity.LanguageAdapter.ItemRowHolder holder, final int position) {

            final Language language = dataList.get(position);
            final MainActivity.LanguageAdapter.ItemRowHolder itemRowHolder = holder;
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
                getMainCategoryFromJson();
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


    public void JoinRoom(String type) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        View bottomView = getLayoutInflater().inflate(R.layout.join_room_dialog, null);
        bottomSheetDialog.setContentView(bottomView);
        ImageView imgClose = bottomView.findViewById(R.id.imgClose);
        TextView tvJoinRoom = bottomView.findViewById(R.id.tvJoinRoom);
        TextView tvAlert = bottomView.findViewById(R.id.tvAlert);
        EditText edtGameCode = bottomView.findViewById(R.id.edtGameCode);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        tvJoinRoom.setOnClickListener(v -> {
            String code = edtGameCode.getText().toString().trim();
            if (code.isEmpty()) {
                tvAlert.setText(getString(R.string.enter_code));
            } else if (code.length() != 5) {
                Toast.makeText(activity, getString(R.string.game_code_alert), Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference dbRef;
                dbRef = rootRef.child(type).child(code);
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // run some code
                            Room room = snapshot.getValue(Room.class);
                            assert room != null;
                            if (type.equalsIgnoreCase(Constant.MULTIPLAYER_ROOM)) {
                                joinGroupBattle(room, dbRef, code);
                            } else {
                                joinOneToOne(room, dbRef, code);
                            }
                            bottomSheetDialog.cancel();

                        } else {
                            Toast.makeText(activity, getString(R.string.gameroom_code_alert), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        imgClose.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        assert bottomSheet != null;
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void joinGroupBattle(Room room, DatabaseReference dbRef, String code) {
        if (room.getIsRoomActive().equals(Constant.TRUE) && room.getIsStarted().equals(Constant.FALSE)) {
            dbRef.child(Constant.joinUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // get total available quest
                    int size = (int) dataSnapshot.getChildrenCount();

                    if (size < Constant.JoinMember) {
                        Intent intent;
                        intent = new Intent(activity, WaitingRoomActivity.class);
                        intent.putExtra("from", "private");
                        intent.putExtra("roomKey", room.getAuthId());
                        intent.putExtra("roomId", code);
                        intent.putExtra("type", "invite");
                        startActivity(intent);
                    } else {
                        Toast.makeText(activity, getString(R.string.gameroom_full), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            Toast.makeText(activity, getString(R.string.game_deactive_alert), Toast.LENGTH_SHORT).show();
        }
    }

    public void joinOneToOne(Room room, DatabaseReference dbRef, String code) {
        if (room.getIsRoomActive().equals(Constant.TRUE) && room.getIsStarted().equals(Constant.FALSE)) {
            dbRef.child(Constant.joinUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // get total available quest
                    int size = (int) dataSnapshot.getChildrenCount();
                    if (size == 1) {
                        Intent intent;
                        intent = new Intent(activity, OneToOneWait.class);
                        intent.putExtra("from", "private");
                        intent.putExtra("roomKey", room.getAuthId());

                        intent.putExtra("roomId", code);
                        intent.putExtra("type", "invite");
                        startActivity(intent);
                    } else {
                        Toast.makeText(activity, getString(R.string.gameroom_full), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            Toast.makeText(activity, getString(R.string.game_deactive_alert), Toast.LENGTH_SHORT).show();
        }
    }

    public void DeleteOldGameRoom() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference dbRef = rootRef.child(Constant.MULTIPLAYER_ROOM);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get total available quest
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        if (ds.getKey() != null)
                            if (dataSnapshot.child(ds.getKey()).child(Constant.authId).exists()) {
                                String ownerRoomIds = dataSnapshot.child(ds.getKey()).child(Constant.authId).getValue().toString();
                                if (ownerRoomIds.equalsIgnoreCase(authId)) {
                                    dbRef.child(ds.getKey()).removeValue();
                                }
                            }/*else{
                                dbRef.child(ds.getKey()).removeValue();
                            }*/
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference dbRefs = rootRef.child(Constant.ONE_TO_ONE);
        dbRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get total available quest
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        if (ds.getKey() != null)
                            if (dataSnapshot.child(ds.getKey()).child(Constant.authId).exists()) {
                                String ownerRoomIds = dataSnapshot.child(ds.getKey()).child(Constant.authId).getValue().toString();
                                if (ownerRoomIds.equalsIgnoreCase(authId)) {
                                    dbRefs.child(ds.getKey()).removeValue();
                                }
                            }/*else{
                                dbRef.child(ds.getKey()).removeValue();
                            }*/
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openCategoryPage(String type) {
        startActivity(new Intent(activity, CategoryActivity.class)
                .putExtra(Constant.QUIZ_TYPE, type));
    }

    @Override
    protected void onPause() {
        AppController.StopSound();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.playSound();
        DeleteOldGameRoom();
        onConfigurationChanged(SettingActivity.config);
        if (Utils.isNetworkAvailable(activity)) {
            Utils.GetSystemConfig(getApplicationContext());
            GetUserStatus();
            invalidateOptionsMenu();
            if (Session.isLogin(activity)) {
                NavigationCartCount();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}