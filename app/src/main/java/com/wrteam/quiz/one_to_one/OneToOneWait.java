package com.wrteam.quiz.one_to_one;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.battle.MultiPlayerGame;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.CircleImageView;
import com.wrteam.quiz.helper.Session;
import com.wrteam.quiz.helper.Utils;
import com.wrteam.quiz.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class OneToOneWait extends AppCompatActivity {

    Activity activity;
    RelativeLayout shareLyt;
    TextView tvAlert, tvGameCode, tvTimer, tvGameStart, tvGameTitle, tv_player1_name, tv_player2_name;
    LinearLayout lytGameCode;
    String from, roomKey, roomId, authId, type, opponentId = "", userId2 = "", player2Name = "", player2Profile = "";
    DatabaseReference roomDBRef, gameUserRef;
    ImageView imgVs;
    ChildEventListener childGameUserListener;
    ValueEventListener roomListener;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    CountDownTimer countDownTimer;
    FloatingActionButton fabShare;

    boolean exit, isOpponentJoin;
    CircleImageView imgPlayer1, imgPlayer2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_wait);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getAllWidgets();
        activity = OneToOneWait.this;
        from = getIntent().getStringExtra("from");
        roomKey = getIntent().getStringExtra("roomKey");
        roomId = getIntent().getStringExtra("roomId");
        type = getIntent().getStringExtra("type");
        roomDBRef = FirebaseDatabase.getInstance().getReference(Constant.ONE_TO_ONE).child(roomId);
        authId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        getData();
        if (roomKey.equalsIgnoreCase(authId)) {
            tvGameStart.setText(getString(R.string.lets_start));
            lytGameCode.setVisibility(View.VISIBLE);
            shareLyt.setVisibility(View.VISIBLE);
            tvGameCode.setText(roomId);
            startTimer();
        } else {
            shareLyt.setVisibility(View.GONE);
            tvTimer.setVisibility(View.GONE);
        }
        imgPlayer1.setDefaultImageResId(R.drawable.ic_account);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_account);
        imgPlayer1.setImageUrl(Session.getUserData(Session.PROFILE, activity), imageLoader);
        tv_player1_name.setText(Session.getUserData(Session.NAME, activity));
        tvGameStart.setOnClickListener(v -> {
            if (isOpponentJoin) {
                if (roomKey.equalsIgnoreCase(authId)) {
                    roomDBRef.child(Constant.isStarted).setValue(Constant.TRUE);
                    goToPlayArea();
                }
            }

        });

    }

    private void startTimer() {
        tvTimer.setVisibility(View.VISIBLE);
        stopTimer();
        countDownTimer = new CountDownTimer(Constant.WAIT_TIME, Constant.COUNT_DOWN_TIMER) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                long totalSecs = (millisUntilFinished / 1000);
                tvTimer.setText("" + String.format("%02d", totalSecs));
            }

            public void onFinish() {
                shareLyt.setVisibility(View.GONE);
                showTimeUpAlert();
            }
        }.start();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void showTimeUpAlert() {
        try {
            stopTimer();
            final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_join_alert, null);
            dialog.setView(dialogView);
            TextView tvExit = dialogView.findViewById(R.id.tvExit);
            androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
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


    public void getData() {
        if (Utils.isNetworkAvailable(activity)) {
            exit = true;

            if (type.equals("invite")) {
                HashMap<String, String> joinMap = new HashMap<>();
                joinMap.put(Constant.UID, authId);
                joinMap.put(Constant.IMAGE, Session.getUserData(Session.PROFILE, activity));
                joinMap.put(Constant.NAME, Session.getUserData(Session.NAME, activity));
                joinMap.put(Constant.IS_JOINED, Constant.TRUE);
                joinMap.put(Constant.USER_ID, Session.getUserData(Session.USER_ID, activity));
                roomDBRef.child(Constant.IS_JOINED).setValue(Constant.TRUE);
                roomDBRef.child(Constant.joinUser).child(authId).setValue(joinMap);
            }
            Animation animZoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_out);

            imgVs.startAnimation(animZoomin);
            checkGameRoomStatus();
            getGameUSerList();
        }
    }

    public void checkGameRoomStatus() {
        roomListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Room roomData = snapshot.getValue(Room.class);
                    assert roomData != null;
                    isOpponentJoin = roomData.getIsJoined().equalsIgnoreCase(Constant.TRUE);
                    if (isOpponentJoin) {
                        tvGameStart.setVisibility(View.VISIBLE);
                        tvTimer.setVisibility(View.GONE);
                        stopTimer();
                    }
                    if (!roomKey.equalsIgnoreCase(authId)) {
                        tvGameStart.setVisibility(View.VISIBLE);
                        tvGameStart.setBackgroundColor(Color.TRANSPARENT);
                        tvGameStart.setTextColor(Color.WHITE);
                        tvGameStart.setText(getResources().getString(R.string.game_start_msg));
                        if (roomData.getIsRoomActive().equalsIgnoreCase(Constant.FALSE))
                            showRoomDeActivateAlert();
                        if (roomData.getIsStarted().equalsIgnoreCase(Constant.TRUE))
                            goToPlayArea();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        roomDBRef.addValueEventListener(roomListener);
    }

    public void goToPlayArea() {
        exit = false;
        Intent intent = new Intent(activity, PlayOneToOne.class);
        intent.putExtra("gameid", roomId)
                .putExtra("opponentId", opponentId)
                .putExtra("user_id2", userId2)
                .putExtra("player2Name", player2Name)
                .putExtra("player2Profile", player2Profile);
        startActivity(intent);
        finish();
    }

    public void getGameUSerList() {

        gameUserRef = roomDBRef.child(Constant.joinUser);
        childGameUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Room book = dataSnapshot.getValue(Room.class);
                assert book != null;
                System.out.println("===== room data " + book.getName());
                if (!authId.equalsIgnoreCase(book.getUID())) {
                    opponentId = book.getUID();
                    userId2 = book.getUserID();
                    player2Name = book.getName();
                    player2Profile = book.getImage();
                    imgPlayer2.setImageUrl(book.getImage(), imageLoader);
                    tv_player2_name.setText(book.getName());
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Room book = dataSnapshot.getValue(Room.class);
                assert book != null;
                if (!authId.equalsIgnoreCase(book.getUID())) {
                    if (book.getIsLeave().equalsIgnoreCase(Constant.TRUE)) {
                        opponentLeaveGameDialog();
                    }
                }
                //A comment has changed, use the key to determine if we are displaying this
                //comment and if so displayed the changed comment.
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Room book = dataSnapshot.getValue(Room.class);
                assert book != null;
                int index = -1;


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        gameUserRef.addChildEventListener(childGameUserListener);
    }

    public int totalActiveUser(ArrayList<Room> list) {
        int activeUser = 0;
        for (Room s : list) {
            if (s.getIsJoined().equalsIgnoreCase(Constant.TRUE) && s.getIsLeave().equalsIgnoreCase(Constant.FALSE)) {
                activeUser++;
            }
        }
        return activeUser;
    }

    public void ShareGameCode(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "\n" + getString(R.string.share_code_msg) + roomId);

        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
    }

    public void getAllWidgets() {
        imgVs = findViewById(R.id.imgVs);
        fabShare = findViewById(R.id.fabShare);
        shareLyt = findViewById(R.id.shareLyt);
        tvAlert = findViewById(R.id.tvAlert);
        tvTimer = findViewById(R.id.tvTimer);

        tvGameCode = findViewById(R.id.tvGameCode);
        tvGameCode.setTextColor(Color.WHITE);
        tvGameStart = findViewById(R.id.tvGameStart);
        tvGameTitle = findViewById(R.id.tvGameTitle);
        tvGameTitle.setTextColor(Color.WHITE);
        lytGameCode = findViewById(R.id.lytGameCode);
        tv_player1_name = findViewById(R.id.tv_player1_name);
        tv_player2_name = findViewById(R.id.tv_player2_name);
        imgPlayer1 = findViewById(R.id.imgPlayer1);
        imgPlayer2 = findViewById(R.id.imgPlayer2);

    }

    public void showRoomDeActivateAlert() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.room_deactivate_alert));
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            roomDBRef.child(Constant.joinUser).child(authId).removeValue();
            gameUserRef.removeEventListener(childGameUserListener);
            roomDBRef.removeEventListener(roomListener);
            alertDialog1.dismiss();
            finish();
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void leaveGameDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        // Setting Dialog Message
        if (roomKey.equals(authId)) {
            alertDialog.setMessage(getString(R.string.destroy_game_room_msg));
        } else {
            alertDialog.setMessage(getString(R.string.leave_game_room_msg));
        }
        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        // Setting OK Button
        alertDialog.setPositiveButton(getString(R.string.stay_back), (dialog, which) -> alertDialog1.dismiss());

        alertDialog.setNegativeButton(getString(R.string.leave), (dialog, which) -> {
            updateUserStatus();
            removeListeners();
            alertDialog1.dismiss();
            finish();
        });
        // Showing Alert Message
        alertDialog.show();

    }

    public void opponentLeaveGameDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        alertDialog.setMessage(R.string.oppentent_leave);

        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        // Setting OK Button


        alertDialog.setNegativeButton(getString(R.string.ok), (dialog, which) -> {
            updateUserStatus();
            removeListeners();
            alertDialog1.dismiss();
            finish();
        });
        // Showing Alert Message
        alertDialog.show();

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        removeListeners();
    }

    public void updateUserStatus() {
        if (roomKey.equals(authId)) {
            roomDBRef.child(Constant.isRoomActive).setValue(Constant.FALSE);
            roomDBRef.removeValue();
        } else {
            roomDBRef.child(Constant.joinUser).child(authId).child(Constant.IS_LEAVE).setValue(Constant.TRUE);

        }

    }

    public void removeListeners() {
        gameUserRef.removeEventListener(childGameUserListener);
        roomDBRef.removeEventListener(roomListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        leaveGameDialog();
    }
}