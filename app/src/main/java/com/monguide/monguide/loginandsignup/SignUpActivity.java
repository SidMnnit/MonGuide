package com.monguide.monguide.loginandsignup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;
import com.monguide.monguide.R;
import com.monguide.monguide.home.HomeActivity;
import com.monguide.monguide.models.UserDetails;
import com.monguide.monguide.utils.DatabaseHelper;
import com.monguide.monguide.utils.StorageHelper;

public class SignUpActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;

    private Toolbar mToolbar;
    private ImageView mProfileImageView;
    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mCollegeNameEditText;
    private EditText mCourseNameEditText;
    private EditText mGraduationYearEditText;
    private EditText mCompanyNameEditText;
    private EditText mJobProfileEditText;
    private ProgressBar mProgressBar;
    private Button mSignupButton;
    private Uri mImageAddress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mToolbar = (Toolbar) findViewById(R.id.activity_signup_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mUserNameEditText = (EditText) findViewById (R.id.activity_signup_nameedittext);
        mEmailEditText = (EditText) findViewById (R.id.activity_signup_emailedittext);
        mPasswordEditText = (EditText) findViewById (R.id.activity_signup_passwordedittext);
        mProfileImageView = (ImageView) findViewById (R.id.activity_signup_profilepictureimageview);
        mCollegeNameEditText = (EditText) findViewById (R.id.activity_signup_collegeNameEditText);
        mCourseNameEditText = (EditText) findViewById (R.id.activity_signup_courseName_EditText);
        mGraduationYearEditText = (EditText) findViewById(R.id.activity_signup_graduationyearedittext);
        mCompanyNameEditText = (EditText) findViewById (R.id.activity_signup_companyNameEditText);
        mJobProfileEditText = (EditText) findViewById (R.id.activity_signup_jobProfile_EditText);
        mProgressBar = (ProgressBar) findViewById(R.id.activity_signup_progressbar);
        mSignupButton = (Button) findViewById (R.id.activity_signup_signupbutton);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasChosenImage() && checkProfileDetails() && checkEducationDetails() && checkWorkDetails()) {
                    sendToDatabase();
                }
            }
        });

        // for rounded corners after image selection
        // this is not recognized as an xml attribute
        mProfileImageView.setClipToOutline(true);

        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

    }

    private void sendToDatabase(){
        mSignupButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uploadUserDetails();
                    uploadProfilePictureAToDatabase();
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mSignupButton.setVisibility(View.VISIBLE);
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // for image selected
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            mImageAddress = data.getData();
            mProfileImageView.setImageURI(mImageAddress);
        }
    }

    private boolean hasChosenImage() {
        if(mImageAddress == null) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            Toast.makeText(this, getResources().getString(R.string.noImageError), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void uploadUserDetails() {
        String uid = mAuth.getCurrentUser().getUid();
        String userName = mUserNameEditText.getText().toString();
        String collegeName = mCollegeNameEditText.getText().toString();
        String courseName = mCourseNameEditText.getText().toString();
        int graduationYear = Integer.parseInt(mGraduationYearEditText.getText().toString());
        String companyName = hasWorkDetails() ? mCompanyNameEditText.getText().toString() : "N/A";
        String jobProfile = hasWorkDetails() ? mJobProfileEditText.getText().toString() : "N/A";
        DatabaseHelper.getReferenceToParticularUser(uid).setValue(
                new UserDetails(
                        userName,
                        new UserDetails.EducationDetails(
                                collegeName, courseName, graduationYear
                        ),
                        new UserDetails.WorkDetails(
                                companyName,
                                jobProfile
                        )
                )
        );
    }

    private void uploadProfilePictureAToDatabase() {
        String uid = mAuth.getCurrentUser().getUid();
        StorageHelper.getReferenceToProfilePictureOfParticularUser(uid)
                .putFile(mImageAddress)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        startHomeActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        mSignupButton.setVisibility(View.VISIBLE);
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                        // incomplete profile, delete this user
                        mAuth.getCurrentUser().delete();
                    }
        });
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private boolean checkProfileDetails() {
        if(TextUtils.isEmpty(mUserNameEditText.getText())){
            mUserNameEditText.setError(getResources().getString(R.string.required));
            return false;
        } else if(TextUtils.isEmpty(mPasswordEditText.getText())){
            mPasswordEditText.setError(getResources().getString(R.string.required));
            return false;
        } else if(TextUtils.isEmpty(mEmailEditText.getText())){
            mEmailEditText.setError(getResources().getString(R.string.required));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkEducationDetails() {
        if(TextUtils.isEmpty(mCollegeNameEditText.getText())) {
            mCollegeNameEditText.setError(getResources().getString(R.string.required));
            return false;
        } else if(TextUtils.isEmpty(mCourseNameEditText.getText())) {
            mCourseNameEditText.setError(getResources().getString(R.string.required));
            return false;
        } else if(TextUtils.isEmpty(mGraduationYearEditText.getText())) {
            mGraduationYearEditText.setError(getResources().getString(R.string.required));
            return false;
        } else {
            return true;
        }
    }

    private boolean hasWorkDetails() {
        return !TextUtils.isEmpty(mCompanyNameEditText.getText()) && !TextUtils.isEmpty(mJobProfileEditText.getText());
    }

    private boolean checkWorkDetails() {
        // Return true if both are filled or both are empty
        if((TextUtils.isEmpty(mCompanyNameEditText.getText()) && TextUtils.isEmpty(mJobProfileEditText.getText())) ||
                (!TextUtils.isEmpty(mCompanyNameEditText.getText()) && !TextUtils.isEmpty(mJobProfileEditText.getText()))) {
            return true;
        } else {
            if(TextUtils.isEmpty(mCompanyNameEditText.getText())) {
                mCompanyNameEditText.setError(getResources().getString(R.string.required));
                return false;
            } else {
                mJobProfileEditText.setError(getResources().getString(R.string.required));
                return false;
            }
        }
    }
}
