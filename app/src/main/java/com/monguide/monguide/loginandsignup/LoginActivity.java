package com.monguide.monguide.loginandsignup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.monguide.monguide.R;
import com.monguide.monguide.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLogInButton;
    private ProgressBar mProgressBar;
    private TextView mSignupTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            startHomeActivity(false);
        }

        setContentView(R.layout.activity_login);

        mEmailEditText = (AppCompatEditText) findViewById(R.id.activity_login_emailedittext);
        mPasswordEditText = (AppCompatEditText) findViewById(R.id.activity_login_passwordedittext);
        mLogInButton = (AppCompatButton) findViewById(R.id.activity_login_loginbutton);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_login_progressbar);
        mSignupTextView = (AppCompatTextView) findViewById(R.id.activity_login_signuptextview);

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthentication();
            }
        });

        mSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUpActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            startHomeActivity(false);
        }
    }

    private void startAuthentication() {
        if(checkLoginDetails()) {
            mSignupTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            String email = mEmailEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startHomeActivity(true);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mSignupTextView.setVisibility(View.VISIBLE);
                       Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_login_rootview),
                                getResources().getString(R.string.illegal_login),
                                Snackbar.LENGTH_LONG);
                       snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text)
                              .setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snackbar.show();
                    }
                }
            });
        }
    }

    private boolean checkLoginDetails() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if(TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getResources().getString(R.string.required));
            return false;
        } else if(TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getResources().getString(R.string.required));
            return false;
        }
        return true;
    }

    private void startSignUpActivity() {
        startActivity(new Intent(this,SignUpActivity.class));
    }

    private void startHomeActivity(boolean startWithAnimation) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(!startWithAnimation)
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
