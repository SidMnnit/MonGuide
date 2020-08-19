package com.monguide.monguide.utils;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.monguide.monguide.R;
import com.monguide.monguide.models.QuestionSummary;
import com.monguide.monguide.profile.ViewProfileActivity;
import com.monguide.monguide.questionandanswer.FullQuestionActivity;

import java.util.HashMap;


public class QuestionSummaryHolder extends RecyclerView.ViewHolder {
    private View view;

    private String mUID;
    private String mQID;

    private ShimmerFrameLayout mPlaceholderForShimmerContainer;
    private LinearLayout mFullQuestionSummaryContainer;

    private LinearLayout mUserDetailsContainer;
    private LinearLayout mTitleBodyContainer;

    private ImageView mProfilePictureImageView;
    private TextView mUserNameTextView;
    private TextView mTimeStampTextView;
    private TextView mTitleTextView;
    private TextView mBodyTextView;
    private TextView mUpvoteCountTextView;
    private TextView mDownVoteCountTextView;
    private TextView mAnswerCountTextView;
    private ImageView mUpvoteButton;
    private ImageView mDownVoteButton;

    private TextView mAddAnswerTextView;

    public QuestionSummaryHolder(View view) {
        super(view);
        this.view = view;

        mProfilePictureImageView = view.findViewById(R.id.questionsummary_item_profilepictureimageview);
        mUserNameTextView = view.findViewById(R.id.questionsummary_item_usernametextview);
        mTimeStampTextView = view.findViewById(R.id.questionsummary_item_timestamptextview);
        mTitleTextView = view.findViewById(R.id.questionsummary_item_titletextview);
        mBodyTextView = view.findViewById(R.id.questionsummary_item_bodytextview);
        mUpvoteCountTextView = view.findViewById(R.id.questionsummary_item_upvotecounttextview);
        mDownVoteCountTextView = view.findViewById(R.id.questionsummary_item_downvotecounttextview);
        mAnswerCountTextView = view.findViewById(R.id.questionsummary_item_answercounttextview);
        mUpvoteButton = view.findViewById(R.id.questionsummary_item_upvoteimageview);
        mDownVoteButton = view.findViewById(R.id.questionsummary_item_downvoteimageview);
        mAddAnswerTextView = view.findViewById(R.id.questionsummary_item_addanswertextview);

        mPlaceholderForShimmerContainer = view.findViewById(R.id.questionsummary_item_shimmercontainer);
        mFullQuestionSummaryContainer = view.findViewById(R.id.questionsummary_item_fullquestionsummarycontainer);

        mUserDetailsContainer = view.findViewById(R.id.questionsummary_item_userdetailscontainer);
        mTitleBodyContainer = view.findViewById(R.id.questionsummary_item_titlebodycontainer);

        // for rounded corners of profile picture
        mProfilePictureImageView.setClipToOutline(true);
    }

    public void setOnClickListenerToOpenUserProfileInFocus() {
        View.OnClickListener redirectToUserProfileInFocus
                = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ViewProfileActivity.class);
                intent.putExtra(Constants.UID, mUID);
                view.getContext().startActivity(intent);
            }
        };
        mUserDetailsContainer.setOnClickListener(redirectToUserProfileInFocus);
    }

    public void setOnClickListenerToOpenFullQuestion() {
        View.OnClickListener redirectToFullQuestion
                = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), FullQuestionActivity.class);
                intent.putExtra(Constants.QID, mQID);
                view.getContext().startActivity(intent);
            }
        };
        mTitleBodyContainer.setOnClickListener(redirectToFullQuestion);
        mAddAnswerTextView.setOnClickListener(redirectToFullQuestion);
    }

    public void setOnClickListenerToUpvoteButton() {
        mUpvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper.getReferenceToParticularQuestion(mQID)
                        .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        QuestionSummary questionSummary = mutableData.getValue(QuestionSummary.class);
                        if (questionSummary == null) {
                            return Transaction.success(mutableData);
                        }
                        String currUID = FirebaseAuth.getInstance().getUid();
                        HashMap<String, Boolean> upvoters = questionSummary.getUpvoters();
                        HashMap<String, Boolean> downvoters = questionSummary.getDownvoters();
                        boolean hasUpvotedAlready = upvoters != null && upvoters.containsKey(currUID);
                        boolean hasDownvotedAlready = downvoters != null && downvoters.containsKey(currUID);
                        if(hasUpvotedAlready) {
                            setUpvoteButtonInUnclickedState();
                            upvoters.remove(currUID);
                            questionSummary.setUpvoteCount(questionSummary.getUpvoteCount() - 1);
                        } else {
                            if(hasDownvotedAlready) {
                                setDownvoteButtonInUnclickedState();
                                downvoters.remove(currUID);
                                questionSummary.setDownvoteCount(questionSummary.getDownvoteCount() - 1);
                            }
                            setUpvoteButtonInClickedState();
                            if(upvoters == null) {
                                upvoters = new HashMap<>();
                                questionSummary.setUpvoters(upvoters);
                            }
                            upvoters.put(currUID, true);
                            questionSummary.setUpvoteCount(questionSummary.getUpvoteCount() + 1);
                        }
                        // Set value and report transaction success
                        mutableData.setValue(questionSummary);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
                });
            }
        });
    }

    public void setOnclickListenerToDownvoteButton() {
        mDownVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper.getReferenceToParticularQuestion(mQID)
                        .runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                QuestionSummary questionSummary = mutableData.getValue(QuestionSummary.class);
                                if (questionSummary == null) {
                                    return Transaction.success(mutableData);
                                }
                                String currUID = FirebaseAuth.getInstance().getUid();
                                HashMap<String, Boolean> upvoters = questionSummary.getUpvoters();
                                HashMap<String, Boolean> downvoters = questionSummary.getDownvoters();
                                boolean hasUpvotedAlready = upvoters != null && upvoters.containsKey(currUID);
                                boolean hasDownvotedAlready = downvoters != null && downvoters.containsKey(currUID);
                                if(hasDownvotedAlready) {
                                    setDownvoteButtonInUnclickedState();
                                    downvoters.remove(currUID);
                                    questionSummary.setDownvoteCount(questionSummary.getDownvoteCount() - 1);
                                } else {
                                    if(hasUpvotedAlready) {
                                        setUpvoteButtonInUnclickedState();
                                        upvoters.remove(currUID);
                                        questionSummary.setUpvoteCount(questionSummary.getUpvoteCount() - 1);
                                    }
                                    setDownvoteButtonInClickedState();
                                    if(downvoters == null) {
                                        downvoters = new HashMap<>();
                                        questionSummary.setDownvoters(downvoters);
                                    }
                                    downvoters.put(currUID, true);
                                    questionSummary.setDownvoteCount(questionSummary.getDownvoteCount() + 1);
                                }
                                mutableData.setValue(questionSummary);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
                        });
            }
        });
    }

    public void setUpvoteButtonInClickedState() {
        mUpvoteButton.setBackgroundResource(R.drawable.background_questionsummaryitem_upvotedstate);
        mUpvoteButton.setImageResource(R.drawable.ic_up_yellow_24dp);
    }

    public void setUpvoteButtonInUnclickedState() {
        mUpvoteButton.setBackgroundColor(Color.TRANSPARENT);
        mUpvoteButton.setImageResource(R.drawable.ic_up_24dp);
    }

    public void setDownvoteButtonInClickedState() {
        mDownVoteButton.setBackgroundResource(R.drawable.background_questionsummaryitem_upvotedstate);
        mDownVoteButton.setImageResource(R.drawable.ic_down_yellow_24dp);
    }

    public void setDownvoteButtonInUnclickedState() {
        mDownVoteButton.setBackgroundColor(Color.TRANSPARENT);
        mDownVoteButton.setImageResource(R.drawable.ic_down_24dp);
    }

    public ShimmerFrameLayout getmPlaceholderForShimmerContainer() {
        return mPlaceholderForShimmerContainer;
    }

    public String getmUID() {
        return mUID;
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    public String getmQID() {
        return mQID;
    }

    public void setmQID(String mQID) {
        this.mQID = mQID;
    }

    public LinearLayout getmFullQuestionSummaryContainer() {
        return mFullQuestionSummaryContainer;
    }

    public ImageView getmUpvoteButton() {
        return mUpvoteButton;
    }

    public ImageView getmDownVoteButton() {
        return mDownVoteButton;
    }

    public ImageView getmProfilePictureImageView() {
        return mProfilePictureImageView;
    }

    public TextView getmUserNameTextView() {
        return mUserNameTextView;
    }

    public TextView getmTimeStampTextView() {
        return mTimeStampTextView;
    }

    public TextView getmTitleTextView() {
        return mTitleTextView;
    }

    public TextView getmBodyTextView() {
        return mBodyTextView;
    }

    public TextView getmUpvoteCountTextView() {
        return mUpvoteCountTextView;
    }

    public TextView getmDownVoteCountTextView() {
        return mDownVoteCountTextView;
    }

    public TextView getmAnswerCountTextView() {
        return mAnswerCountTextView;
    }
}
