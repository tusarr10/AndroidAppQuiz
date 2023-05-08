package com.wrteam.quiz.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewSelfChallengeActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView cateRecyclerView, subRecyclerView, questionView, timeView;
    ProgressBar progressBar1, progressBar2;
    public ArrayList<Category> categoryList, subCateList;
    CustomAdapter customAdapter;
    Snackbar snackbar;
    RelativeLayout subCateLyt;

    String cateId = "", subCateId = "", selectedQues = "", selectedMin = "";
    boolean isSubCateAvailable, isPlayable;
    String challengeType = "", ID = "";
    TextView tvCateName, tvSubCateName, tvAlert, tvSelectQues;
    Spinner subCateSpinner, cateSpinner;
    ArrayList<Integer> queNoList, minuteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_self_challenge);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.self_challenge));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        subCateLyt = findViewById(R.id.subCateLyt);
        tvCateName = findViewById(R.id.tvCateName);
        tvAlert = findViewById(R.id.tvAlert);
        tvSubCateName = findViewById(R.id.tvSubCateName);
        tvSelectQues = findViewById(R.id.tvSelectQues);
        cateSpinner = findViewById(R.id.cateSpinner);
        subCateSpinner = findViewById(R.id.subCateSpinner);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        cateRecyclerView = findViewById(R.id.cateRecyclerview);
        subRecyclerView = findViewById(R.id.subRecyclerview);
        questionView = findViewById(R.id.questionView);
        timeView = findViewById(R.id.timeView);
        questionView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        timeView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        tvCateName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
        tvSubCateName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand, 0);
        tvCateName.setOnClickListener(v -> cateSpinner.performClick());
        tvSubCateName.setOnClickListener(v -> subCateSpinner.performClick());
        if (Constant.IN_APP_MODE.equals("1")) {
            if (Constant.ADS_TYPE.equals("1")) {
                Utils.LoadNativeAds(NewSelfChallengeActivity.this);
            } else {
                Utils.LoadNativeAd(NewSelfChallengeActivity.this);
            }
        }

        getData();
        setTime();
    }

    public void setTime() {
        minuteList = new ArrayList<>();
        for (int i = 0; i <= Constant.MAX_MINUTES; i++) {
            if (i % 3 == 0) {
                if (i != 0)
                    minuteList.add(i);
            }
        }
        SelectAdapter adapter = new SelectAdapter(getApplicationContext(), minuteList, "time");
        timeView.setAdapter(adapter);
    }

    public void setQuestionCount(int queLength) {
        //System.out.println("===que size " + queLength);
        queNoList = new ArrayList<>();
        for (int i = 0; i <= queLength; i++) {
            if (i % 5 == 0) {
                if (i != 0)
                    queNoList.add(i);
            }
        }
        if (queNoList.size() == 0)
            tvAlert.setVisibility(View.VISIBLE);
        else
            tvAlert.setVisibility(View.GONE);

        SelectAdapter adapter = new SelectAdapter(getApplicationContext(), queNoList, "que");
        questionView.setAdapter(adapter);
    }

    public void setSnackBar() {
        snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    private void getData() {
        if (Utils.isNetworkAvailable(NewSelfChallengeActivity.this)) {
            GetCategories();
            invalidateOptionsMenu();
        } else {
            setSnackBar();
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        textView.setBackgroundResource(R.drawable.gradient_cat_blue);
        textView.setTextColor(Color.WHITE);
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void GetCategories() {
        progressBar1.setVisibility(View.VISIBLE);
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
                    categoryList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString(Constant.ERROR);
                    // System.out.println("====cate res " + response);
                    if (error.equalsIgnoreCase("false")) {

                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setId(object.getString(Constant.ID));
                            category.setName(object.getString(Constant.CATEGORY_NAME));
                            category.setImage(object.getString(Constant.IMAGE));
                            category.setTtlQues(object.getString(Constant.NO_OF_QUES));
                            category.setNoOfCate(object.getString(Constant.NO_OF_CATE));
                            category.setPlan(object.getString(Constant.CATEGORY_PLAY));
                            category.setCateAmount(object.getString(Constant.CATEGORY_AMOUNT));
                            category.setPurchased(Boolean.parseBoolean(object.getString(Constant.ISPURCHASED)));
                            categoryList.add(category);
                        }

                        customAdapter = new CustomAdapter(categoryList, "cate");
                        cateSpinner.setAdapter(customAdapter);


                        cateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                Category cate = categoryList.get(i);
                                cateId = cate.getId();
                                tvCateName.setText(cate.getName());
                                setTextViewDrawableColor(tvCateName, R.color.white);
                                //cateSpinner.setSelection(i);
                                if (cate.getNoOfCate().equalsIgnoreCase("0")) {

                                    if (Integer.parseInt(cate.getTtlQues()) >= 5) {
                                        if (cate.getPlan().equals("Free") || cate.getPlan().equals("")) {
                                            isPlayable = true;
                                            questionView.setVisibility(View.VISIBLE);
                                            setQuestionCount(Integer.parseInt(cate.getTtlQues()));
                                            isSubCateAvailable = false;
                                            subCateLyt.setVisibility(View.GONE);
                                        } else {
                                            System.out.println("InsideValue");
                                            premiumAlert(cate, i);
                                        }


                                    } else {
                                        isSubCateAvailable = false;
                                        subCateLyt.setVisibility(View.GONE);
                                        questionView.setVisibility(View.GONE);
                                        tvAlert.setVisibility(View.VISIBLE);
                                        selectedQues = "";
                                    }
                                } else {
                                    if (cate.getPlan().equals("Free") || cate.getPlan().equals("")) {
                                        isPlayable = true;
                                        GetSubCategories(cateId);
                                    } else {
                                        System.out.println("OutsideValue");
                                        premiumAlert(cate, i);
                                    }
                                }

                                //cateSpinner.setSelection(i);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                                System.out.println("OnNothing");
                            }

                        });

                    }
                    progressBar1.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public void premiumAlert(Category cate, int i) {

        if (cate.isPurchased()) {
            GetSubCategories(cateId);
            isPlayable = true;
        } else {
            if (Constant.TOTAL_COINS >= Integer.parseInt(cate.getCateAmount())) {
                buyCategoryAlertDialog(cate.getName(), cate.getCateAmount(), cate.getId(), cate, i);
            } else {
                isPlayable = false;
                Toast.makeText(NewSelfChallengeActivity.this, getString(R.string.not_enough_coin), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void buyCategoryAlertDialog(String cateName, String cateCoins, String cateID, Category category, int position) {
        try {

            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(NewSelfChallengeActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_leave_battle, null);
            dialog1.setView(dialogView);

            final AlertDialog alertDialog = dialog1.create();
            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            tvTitle.setText("Premium Category");
            TextView btnok = dialogView.findViewById(R.id.btn_ok);
            TextView btnNo = dialogView.findViewById(R.id.btnNo);
            tvMessage.setText("For Unlock " + cateName + " You need to pay " + cateCoins + " Coins");
            btnok.setOnClickListener(v -> {
                isPlayable = true;
                Constant.TOTAL_COINS = Constant.TOTAL_COINS - Integer.parseInt(cateCoins);

                Utils.UpdateCoin(NewSelfChallengeActivity.this, "-" + cateCoins);
                catePurchase(cateID, position);
                Toast.makeText(NewSelfChallengeActivity.this, "Category Unlocked!! Enjoy Game", Toast.LENGTH_SHORT).show();
                GetSubCategories(cateId);

                alertDialog.dismiss();

            });
            btnNo.setOnClickListener(view -> {
                cateSpinner.setAdapter(customAdapter);
                isPlayable = false;
                alertDialog.dismiss();

            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
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
                        if (categoryList != null) {
                            categoryList.get(position).setPurchased(true);
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

    public void GetSubCategories(final String cateId) {
        progressBar2.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.getSubCategory, "1");
        params.put(Constant.categoryId, cateId);
        System.out.println("====params " + params.toString());
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    subCateList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString(Constant.ERROR);
                    //System.out.println("====sub cate res " + response);
                    if (error.equalsIgnoreCase("false")) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setId(object.getString(Constant.ID));
                            category.setName(object.getString(Constant.SUB_CATE_NAME));
                            category.setImage(object.getString(Constant.IMAGE));
                            category.setTtlQues(object.getString(Constant.NO_OF_CATE));
                            subCateList.add(category);
                        }
                        isSubCateAvailable = true;
                        subCateLyt.setVisibility(View.VISIBLE);
                        CustomAdapter customAdapter = new CustomAdapter(subCateList, "subCate");
                        subCateSpinner.setAdapter(customAdapter);
                        subCateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                Category subCate = subCateList.get(i);
                                subCateId = subCate.getId();
                                tvSubCateName.setText(subCate.getName());
                                setTextViewDrawableColor(tvSubCateName, R.color.white);
                                if (Integer.parseInt(subCate.getTtlQues()) >= 5) {
                                    setQuestionCount(Integer.parseInt(subCate.getTtlQues()));
                                    questionView.setVisibility(View.VISIBLE);
                                } else {
                                    questionView.setVisibility(View.GONE);
                                    tvAlert.setVisibility(View.VISIBLE);
                                    selectedQues = "";
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } else {
                        isSubCateAvailable = false;
                        subCateLyt.setVisibility(View.GONE);
                    }

                    progressBar2.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);

    }

    public void StartChallenge(View view) {
        if (isPlayable) {
            if (cateId.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.category_select), Toast.LENGTH_SHORT).show();
            } else if (isSubCateAvailable && subCateId.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.subcategory_select), Toast.LENGTH_SHORT).show();
            } else if (selectedQues.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.question_select), Toast.LENGTH_SHORT).show();
            } else if (selectedMin.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.minutes_select), Toast.LENGTH_SHORT).show();
            } else {
                if (!isSubCateAvailable) {
                    challengeType = "cate";
                    ID = cateId;
                } else {
                    challengeType = "subCate";
                    ID = subCateId;
                }
                Intent intent = new Intent(getApplicationContext(), SelfChallengeQuestion.class);
                intent.putExtra("type", challengeType);
                intent.putExtra("id", ID);
                intent.putExtra("limit", "" + selectedQues);
                intent.putExtra("time", Integer.parseInt(selectedMin));
                startActivity(intent);
            }
        } else {
            Toast.makeText(NewSelfChallengeActivity.this, getString(R.string.not_enough_coinorpurchase), Toast.LENGTH_SHORT).show();
        }

    }

    public class CustomAdapter extends BaseAdapter {
        ArrayList<Category> cateList;
        String type;

        public CustomAdapter(ArrayList<Category> cateList, String type) {
            this.cateList = cateList;
            this.type = type;
        }

        @Override
        public int getCount() {
            return cateList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner, null);
            Category category = cateList.get(i);

            TextView tvName = view.findViewById(R.id.tvName);
            TextView tvTotal = view.findViewById(R.id.tvTotal);
            tvName.setText(category.getName());
            tvTotal.setText(getString(R.string.ttl_ques) + category.getTtlQues());
            if (type.equals("cate")) {
                if (category.getPlan().equals("Paid")) {
                    tvTotal.setText("Premium");
                    tvTotal.setTextColor(ContextCompat.getColor(NewSelfChallengeActivity.this, R.color.white));
                    tvTotal.setBackgroundResource(R.drawable.premimum_circle);
                } else
                    tvTotal.setVisibility(View.GONE);
            }
            return view;
        }
    }


    public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ItemRowHolder> {

        private ArrayList<Integer> cateList;
        Context mContext;

        String type;

        public SelectAdapter(Context context, ArrayList<Integer> cateList, String type) {
            this.cateList = cateList;
            this.mContext = context;

            this.type = type;
        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_slection, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemRowHolder holder, @SuppressLint("RecyclerView") final int position) {
            if (type.equals("time")) {
                if (selectedMin.equals(String.valueOf(cateList.get(position)))) {
                    holder.tvSelect.setBackgroundResource(R.drawable.gradient_cat_blue);
                    holder.tvSelect.setTextColor(Color.WHITE);
                    holder.setIsRecyclable(false);
                } else
                    holder.tvSelect.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));


            } else {

                if (selectedQues.equals(String.valueOf(cateList.get(position)))) {
                    holder.tvSelect.setBackgroundResource(R.drawable.gradient_cat_blue);
                    holder.tvSelect.setTextColor(Color.WHITE);
                    holder.setIsRecyclable(false);
                } else
                    holder.tvSelect.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            }
            holder.tvSelect.setText(String.valueOf(cateList.get(position)));


            holder.tvSelect.setOnClickListener(v -> {

                if (type.equals("time")) {
                    selectedMin = String.valueOf(cateList.get(position));
                } else {
                    selectedQues = String.valueOf(cateList.get(position));

                }
                notifyDataSetChanged();

            });
        }

        @Override
        public int getItemCount() {
            return (null != cateList ? cateList.size() : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tvSelect;


            public ItemRowHolder(View itemView) {
                super(itemView);
                tvSelect = itemView.findViewById(R.id.tvSelect);

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.StopSound();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.playSound();
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


