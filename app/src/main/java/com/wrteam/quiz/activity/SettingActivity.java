package com.wrteam.quiz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wrteam.quiz.Constant;
import com.wrteam.quiz.R;
import com.wrteam.quiz.helper.AppController;
import com.wrteam.quiz.helper.Session;

import static com.wrteam.quiz.helper.AppController.StopSound;
import static com.wrteam.quiz.helper.AppController.getAppContext;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    private Context mContext;
    private Dialog mCustomDialog;
    private SwitchCompat mSoundCheckBox, mVibrationCheckBox, mMusicCheckBox;
    private TextView ok_btn;
    private boolean isSoundOn;
    private boolean isVibrationOn;
    private boolean isMusicOn;
    RelativeLayout fontLayout, app_language;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static AlertDialog alertDialog;
    private static final String Locale_Preference = "Locale Preference";
    private static final String Locale_KeyValue = "Saved Locale";
    private static Locale myLocale;
    androidx.appcompat.app.AlertDialog alertDialogs;
    String lang = "en";
    public static Configuration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.activity_setting);

        mContext = SettingActivity.this;
        AppController.currentActivity = this;
        initViews();
        fontLayout = (RelativeLayout) findViewById(R.id.font_layout);
        fontLayout.setOnClickListener(view -> {
            try {
                fontSizeDialog();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        });

/*        app_language = (RelativeLayout) findViewById(R.id.app_language);


        app_language.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            LanguageDialog(SettingActivity.this);
        });

        sharedPreferences = getSharedPreferences(Locale_Preference, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();*/

        /*changeLocale(Session.getApplanguage(SettingActivity.this));*/
    }


    public void LanguageDialog(Activity activity) {
        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater1.inflate(R.layout.applanguage_dialog, null);
        dialog.setView(dialogView);
        TextView tvenglish, tvHindi;
        tvenglish = (TextView) dialogView.findViewById(R.id.tvenglish);
        tvHindi = (TextView) dialogView.findViewById(R.id.tvHindi);

        tvenglish.setOnClickListener(view -> {
            lang = "en";
            changeLocale(lang);
            alertDialogs.dismiss();
        });
        tvHindi.setOnClickListener(view -> {
            lang = "hi";
            changeLocale(lang);
            alertDialogs.dismiss();
        });
        alertDialogs = dialog.create();
        alertDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogs.show();

    }

    public void changeLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);//Set Selected Locale
        Session.setApplanguage(SettingActivity.this, lang);
        //saveLocale(lang);//Save the selected locale
        Locale.setDefault(myLocale);//set new locale as default
        config = new Configuration();//get Configuration
        config.locale = myLocale;//set config locale as selected locale
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());//Update the config
        //updateTexts();//Update texts according to locale
        DrawerActivity.navigationView.getMenu().clear();
        DrawerActivity.navigationView.inflateMenu(R.menu.drawer_menu);
    }

    public void saveLocale(String lang) {
        editor.putString(Locale_KeyValue, lang);
        editor.commit();
    }

    //Get locale method in preferences
    public void loadLocale() {
        String language = sharedPreferences.getString(Locale_KeyValue, "");
        changeLocale(language);
    }


    private void initViews() {
        mSoundCheckBox = (SwitchCompat) findViewById(R.id.sound_checkbox);
        mVibrationCheckBox = (SwitchCompat) findViewById(R.id.vibration_checkbox);
        mMusicCheckBox = (SwitchCompat) findViewById(R.id.show_music_checkbox);

        ok_btn = (TextView) findViewById(R.id.ok);
        populateSoundContents();
        populateVibrationContents();
        populateMusicEnableContents();
        ok_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                finish();
            }
        });
    }

    private void moreAppClicked() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("")));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(String.format(Constant.MORE_APP_URL, ""))));
        }
    }

    private void switchSoundCheckbox() {
        isSoundOn = !isSoundOn;
        Session.setSoundEnableDisable(mContext, isSoundOn);
        populateSoundContents();
    }

    private void switchVibrationCheckbox() {
        isVibrationOn = !isVibrationOn;
        Session.setVibration(mContext, isVibrationOn);
        populateVibrationContents();
    }

    private void switchMusicEnableCheckbox() {
        isMusicOn = !isMusicOn;
        if (isMusicOn) {
            Session.setMusicEnableDisable(mContext, true);
            AppController.playSound();

        } else {
            Session.setMusicEnableDisable(mContext, false);
            StopSound();
        }
        populateMusicEnableContents();
    }

    protected void populateSoundContents() {
        if (Session.getSoundEnableDisable(mContext)) {
            mSoundCheckBox.setChecked(true);
        } else {
            mSoundCheckBox.setChecked(false);
        }
        isSoundOn = Session.getSoundEnableDisable(mContext);
    }

    protected void populateVibrationContents() {
        if (Session.getVibration(mContext)) {
            mVibrationCheckBox.setChecked(true);
        } else {
            mVibrationCheckBox.setChecked(false);
        }
        isVibrationOn = Session.getVibration(mContext);
    }

    protected void populateMusicEnableContents() {
        if (Session.getMusicEnableDisable(mContext)) {
            AppController.playSound();
            mMusicCheckBox.setChecked(true);
        } else {
            StopSound();
            mMusicCheckBox.setChecked(false);
        }
        isMusicOn = Session.getMusicEnableDisable(mContext);
    }

    private void rateAppClick() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + mContext.getPackageName())));
        } catch (ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK)));
        }
    }


    public void viewClickHandler(View view) {
        switch (view.getId()) {

            case R.id.sound_layout:
                switchSoundCheckbox();
                break;
            case R.id.sound_checkbox:
                switchSoundCheckbox();
                break;
            case R.id.vibration_layout:
                switchVibrationCheckbox();
                break;
            case R.id.vibration_checkbox:
                switchVibrationCheckbox();
                break;
            case R.id.show_hint_layout:
                switchMusicEnableCheckbox();
                break;
            case R.id.show_music_checkbox:
                String[] LOCATION_PERMS = {android.Manifest.permission.READ_PHONE_STATE};
                switchMusicEnableCheckbox();
                break;

            case R.id.rate_layout:
                rateAppClick();
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                break;
            case R.id.ok:
                onBackPressed();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        overridePendingTransition(R.anim.close_next, R.anim.open_next);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {

        if (mContext != null) {
            if (mCustomDialog != null) {
                mCustomDialog.dismiss();
                mCustomDialog = null;
            }
            mVibrationCheckBox = null;
            mMusicCheckBox = null;
            mSoundCheckBox = null;
            mContext = null;
            super.onDestroy();
        }
    }

    public void fontSizeDialog() {


        String changedFontSize;

        changedFontSize = Session.getSavedTextSize(getApplicationContext());

        final AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
        dialog.setTitle("Change font Size");

        LayoutInflater inflater1 = (LayoutInflater) SettingActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater1.inflate(R.layout.dialog_font_size, null);
        dialog.setView(dialogView);

        alertDialog = dialog.create();
        Button btnOk = (Button) dialogView.findViewById(R.id.btnYes);
        final EditText edt_font_size_value = (EditText) dialogView.findViewById(R.id.edt_font_size_value);
        final SeekBar skBar_value = (SeekBar) dialogView.findViewById(R.id.skBar_value);

        skBar_value.setMax(14);
        skBar_value.setProgress(Integer.parseInt(changedFontSize) - 16);
        edt_font_size_value.setText(changedFontSize);
        edt_font_size_value.setSelection(edt_font_size_value.getText().toString().length());

        skBar_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edt_font_size_value.setText(String.valueOf(progress + 16));
                edt_font_size_value.setSelection(edt_font_size_value.getText().toString().length());
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                if (Integer.parseInt(edt_font_size_value.getText().toString().trim()) >= 30) {
                    edt_font_size_value.setText(Constant.TEXT_SIZE_MAX);
                    Session.saveTextSize(getApplicationContext(), Constant.TEXT_SIZE_MAX);

                } else if (Integer.parseInt(edt_font_size_value.getText().toString().trim()) < 16) {
                    edt_font_size_value.setText(Constant.TEXT_SIZE_MIN);
                    Session.saveTextSize(getApplicationContext(), Constant.TEXT_SIZE_MIN);

                } else {
                    Session.saveTextSize(getApplicationContext(), edt_font_size_value.getText().toString().trim());

                }
                PlayActivity.ChangeTextSize(Integer.parseInt(Session.getSavedTextSize(getApplicationContext())));
            }

        });

        edt_font_size_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentProgress = edt_font_size_value.getText().toString().trim();
                if (!currentProgress.equals("")) {
                    skBar_value.setProgress(Integer.parseInt(currentProgress) - 16);
                    edt_font_size_value.setSelection(edt_font_size_value.getText().toString().length());
                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
