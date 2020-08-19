package com.monguide.monguide.question;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.monguide.monguide.R;
import com.monguide.monguide.models.QuestionSummary;
import com.monguide.monguide.utils.DatabaseHelper;

public class AddQuestionActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private EditText mTitleEditText;
    private EditText mBodyEditText;
    private Button mSubmitButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addquestion);

        mToolbar = (Toolbar) findViewById(R.id.activity_addquestion_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitleEditText = findViewById(R.id.activity_addquestion_titleedittext);
        mBodyEditText = findViewById(R.id.activity_addquestion_bodyedittext);

        mSubmitButton = findViewById(R.id.activity_addquestion_submitbutton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkQuestionDetails()) {
                    sendToDatabase();
                }
            }
        });
        mProgressBar = findViewById(R.id.activity_addquestion_progressbar);
    }

    private void sendToDatabase(){
        mSubmitButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        final String qid = DatabaseHelper.getReferenceToAllQuestions().push().getKey();
        QuestionSummary questionSummary
                = new QuestionSummary(
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        mTitleEditText.getText().toString().trim(),
                        mBodyEditText.getText().toString().trim());

        DatabaseHelper.getReferenceToAllQuestionsAskedByUser(FirebaseAuth.getInstance().getUid())
                .child(qid).setValue(true);
        DatabaseHelper.getReferenceToParticularQuestion(qid)
                .setValue(questionSummary, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError == null) {
                            // post added successfully
                            // finish the activity
                            Toast toast = Toast.makeText(AddQuestionActivity.this,
                                    "Question added successfully",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            finish();
                        } else {
                            // rare case
                            // error occured
                            Toast.makeText(AddQuestionActivity.this,
                                    getResources().getString(R.string.error),
                                    Toast.LENGTH_LONG)
                                    .show();
                            DatabaseHelper.getReferenceToAllQuestionsAskedByUser(FirebaseAuth.getInstance().getUid())
                                    .child(qid).removeValue();
                            mProgressBar.setVisibility(View.GONE);
                            mSubmitButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private boolean checkQuestionDetails() {
        if(TextUtils.isEmpty(mTitleEditText.getText())) {
            mTitleEditText.setError(getResources().getString(R.string.required));
            return false;
        } else if(TextUtils.isEmpty(mBodyEditText.getText())) {
            mBodyEditText.setError(getResources().getString(R.string.required));
            return false;
        }
        return true;
    }
}
