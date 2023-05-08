package com.wrteam.quiz.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.wrteam.quiz.R;
import com.wrteam.quiz.activity.LoginTabActivity;
import com.wrteam.quiz.helper.Session;

import java.util.Objects;

public class SignUpFragment extends Fragment {


    public TextInputEditText edtName, edtEmail, edtPassword, edtRefer;
    public TextInputLayout inputName, inputEmail, inputPass;
    public ProgressDialog mProgressDialog;
    public static FirebaseAuth mAuth;
    Button accountlogin;
    String token;
    CardView signupryt;

    @SuppressLint("ClickableViewAccessibility")
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_sign_up, container, false);

        edtName = v.findViewById(R.id.edtName);
        edtEmail = v.findViewById(R.id.edtEmail);
        edtPassword = v.findViewById(R.id.edtPassword);
        edtRefer = v.findViewById(R.id.edtRefer);
        inputName = v.findViewById(R.id.inputName);
        inputEmail = v.findViewById(R.id.inputEmail);
        inputPass = v.findViewById(R.id.inputPass);
        signupryt = v.findViewById(R.id.signupryt);
        mAuth = FirebaseAuth.getInstance();
        signupryt.setOnClickListener(view -> {
            if (!validateForm()) {
                return;
            }

            showProgressDialog();
            final String email = edtEmail.getText().toString();
            final String password = edtPassword.getText().toString();
            final String name = edtName.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    assert user != null;
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                }
                            });

                    sendEmailVerification();
                } else {
                    hideProgressDialog();

                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException invalidEmail) {
                        inputEmail.setError(invalidEmail.getMessage());

                    } catch (Exception e) {
                        e.printStackTrace();
                        inputEmail.setError(e.getMessage());
                    }

                }

            });
        });


        token = Session.getDeviceToken(getActivity());
        if (token == null) {
            token = "token";
        }


        return v;
    }


    private boolean validateForm() {
        boolean valid = true;

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        //String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            inputName.setError(getString(R.string.entername));
            valid = false;
        } else {
            inputName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.email_alert_1));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            inputEmail.setError(getString(R.string.email_alert_1));
        } else {
            inputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            inputPass.setError(getString(R.string.enterpassword));
            valid = false;
        } else if (password.length() < 6) {
            inputPass.setError(getString(R.string.password_valid));
            valid = false;
        } else {
            inputPass.setError(null);
        }


        return valid;
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), task -> {
                    // [START_EXCLUDE]
                    // Re-enable button
                    //findViewById(R.id.verify_email_button).setEnabled(true);

                    if (task.isSuccessful()) {
                        String refer = edtRefer.getText().toString();
                        if (!refer.isEmpty())
                            Session.setFCode(refer, getActivity());
                        Toast.makeText(getActivity(), getString(R.string.verify_email_sent) + user.getEmail(), Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(getActivity(), LoginTabActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    } else {

                        Toast.makeText(getActivity(), getString(R.string.verify_email_sent_f), Toast.LENGTH_LONG).show();
                        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential authCredential = EmailAuthProvider.getCredential(edtEmail.getText().toString(), edtPassword.getText().toString());
                        user1.reauthenticate(authCredential).addOnCompleteListener(task1 -> user1.delete());

                        //auth.getCurrentUser().delete();
                    }
                    // [END_EXCLUDE]
                });

        // [END send_email_verification]
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
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
