package com.wrteam.quiz.one_to_one;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.activity.LoginTabActivity;
import com.wrteam.quiz.activity.SearchPlayerActivity;
import com.wrteam.quiz.activity.SplashActivity;
import com.wrteam.quiz.helper.ApiConfig;
import com.wrteam.quiz.helper.CircleImageView;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.helper.VolleyCallback;
import com.wrteam.quiz.model.Room;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OneToOneBattle extends AppCompatActivity {
    String FORMAT = "%02d";
    TextView tvTimer, tvGameCode, tvStartGame, tvWaitMsg;
    LinearLayout lytTimer, lytGameCode;
    DatabaseReference dbRef, createRoomDbRef;
    FirebaseDatabase firebaseDatabase;
    String authId, gameCode;
    ProgressDialog mProgressDialog;
    Activity activity;
    CountDownTimer countDownTimer;
    FloatingActionButton fabShare;
    ValueEventListener roomListener;
    boolean exit;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_battle);
        activity = OneToOneBattle.this;
        getAllWidgets();
        firebaseDatabase = FirebaseDatabase.getInstance();
        authId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        dbRef = firebaseDatabase.getReference(Constant.ONE_TO_ONE);
        from = getIntent().getStringExtra("from");
        getData();
    }

    public void getAllWidgets() {

        tvTimer = findViewById(R.id.tvTimer);
        tvGameCode = findViewById(R.id.tvGameCode);
        tvWaitMsg = findViewById(R.id.tvWaitMsg);
        fabShare = findViewById(R.id.fabShare);
        lytTimer = findViewById(R.id.lytTimer);
        tvStartGame = findViewById(R.id.tvStartGame);
        lytGameCode = findViewById(R.id.lytGameCode);

    }

    public void StartGame(View view) {
        showProgressDialog();
        gameCode = Constant.randomAlphaNumeric(6);
        createRoomDbRef = dbRef.child(gameCode);
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.authId, authId);
        map.put(Constant.isRoomActive, Constant.TRUE);
        map.put(Constant.IS_OPPOSITE_JOIN, Constant.FALSE);
        map.put(Constant.isStarted, Constant.FALSE);

        // roomId = dbRef.push().getKey();
        // map.put(Constant.roomID, roomId);

        createRoomDbRef.setValue(map).addOnSuccessListener(aVoid -> {
            //Do what you need to do
            Toast.makeText(activity, getString(R.string.room_create_msg), Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            //join user detail
            HashMap<String, String> joinMap = new HashMap<>();
            joinMap.put(Constant.IMAGE, Session.getUserData(Session.PROFILE, activity));
            joinMap.put(Constant.NAME, Session.getUserData(Session.NAME, activity));
            joinMap.put(Constant.IS_LEAVE, Constant.FALSE);
            createRoomDbRef.child(Constant.joinUser).child(authId).setValue(joinMap);

            //Show Timer and Game Code
            tvGameCode.setText(gameCode);
            lytTimer.setVisibility(View.VISIBLE);
            lytGameCode.setVisibility(View.VISIBLE);
            tvStartGame.setVisibility(View.GONE);
            startTimer();
            checkGameRoomStatus();
        });


    }

    public void getData() {
        if (Utils.isNetworkAvailable(activity)) {
            exit = true;

            if (from.equals(Constant.JOIN)) {
                String gameCode = getIntent().getStringExtra(Constant.GAME_CODE);
                createRoomDbRef = dbRef.child(getIntent().getStringExtra(Constant.GAME_CODE));
                HashMap<String, String> joinMap = new HashMap<>();
                joinMap.put(Constant.IMAGE, Session.getUserData(Session.PROFILE, activity));
                joinMap.put(Constant.NAME, Session.getUserData(Session.NAME, activity));
                joinMap.put(Constant.IS_LEAVE, Constant.FALSE);
                createRoomDbRef.child(Constant.joinUser).child(authId).setValue(joinMap);
                createRoomDbRef.child(Constant.IS_OPPOSITE_JOIN).setValue(Constant.TRUE);
                showProgressDialog();
                checkGameRoomStatus();


            }
        }
    }

    public void checkGameRoomStatus() {

        roomListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Room roomData = snapshot.getValue(Room.class);
                    if (roomData.getAuthId().equalsIgnoreCase(authId)) {
                        /*if (roomData.getIsRoomActive().equalsIgnoreCase(Constant.FALSE))
                            showRoomDeActivateAlert();*/
                        if (roomData.getIsOppositeJoin().equalsIgnoreCase(Constant.TRUE)) {
                            Toast.makeText(activity, "Start in few second", Toast.LENGTH_LONG).show();

                            createRoomDbRef.child(Constant.isStarted).setValue(Constant.TRUE);
                            goForQuestions(gameCode);
                        }
                    } else {
                        if (roomData.getIsOppositeJoin().equalsIgnoreCase(Constant.TRUE) && roomData.getIsStarted().equalsIgnoreCase(Constant.TRUE)) {
                            hideProgressDialog();
                            goForQuestions(getIntent().getStringExtra(Constant.GAME_CODE));

                        }
                    }

                }
                System.out.println("===call===");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("===call===onCancelled");
            }
        };
        createRoomDbRef.addValueEventListener(roomListener);
    }

    public void goForQuestions(String gameCode) {
        createRoomDbRef.child(Constant.joinUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!Objects.requireNonNull(snapshot.getKey()).equalsIgnoreCase(authId)) {
                            Room room = snapshot.getValue(Room.class);
                            // run some code
                            System.out.println("====room data " + snapshot.getKey());
                            System.out.println("====room data " + room.getName());
                            Intent intent = new Intent(activity, PlayOneToOne.class);
                            intent.putExtra(Constant.GAME_CODE, gameCode);
                            intent.putExtra(Constant.OPP_USER, room);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showTimeUpAlert() {
        try {
            stopTimer();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(OneToOneBattle.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_join_alert, null);
            dialog.setView(dialogView);
            TextView tvExit = dialogView.findViewById(R.id.tvExit);

            AlertDialog alertDialog = dialog.create();
            tvExit.setOnClickListener(v -> {
                finish();
                alertDialog.dismiss();
            });

            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {

        stopTimer();
        countDownTimer = new CountDownTimer(Constant.WAIT_TIME, Constant.COUNT_DOWN_TIMER) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished / 1000);
                tvTimer.setText("" + String.format(FORMAT, progress));
            }

            public void onFinish() {

                fabShare.setVisibility(View.GONE);
                showTimeUpAlert();
            }
        }.start();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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

    public void ShareGameCode(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Here is my one to one battle game code : " + gameCode);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //    deleteGameRoom();
        if (createRoomDbRef != null && roomListener != null)
            createRoomDbRef.removeEventListener(roomListener);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }


    }

}