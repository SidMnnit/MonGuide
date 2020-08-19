package com.monguide.monguide.utils;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.monguide.monguide.R;
import com.monguide.monguide.profile.ViewProfileActivity;
import com.monguide.monguide.questionandanswer.FullQuestionActivity;

public class NotificationHolder extends RecyclerView.ViewHolder {
    private View view;

    private String mUID;
    private String mQID;

    private String mNID;

    private RelativeLayout root;

    private ShimmerFrameLayout mPlaceholderForShimmerContainer;
    private LinearLayout mFullNotificationContainer;

    private ImageView mProfilePictureImageView;
    private TextView mUserNameTextView;

    private TextView mQuestionTextView;

    public NotificationHolder(View view) {
        super(view);
        this.view = view;

        root = view.findViewById(R.id.notification_item_root);

        mProfilePictureImageView = view.findViewById(R.id.notification_item_profileimage);
        mUserNameTextView = view.findViewById(R.id.notification_item_usernametextview);

        mFullNotificationContainer = view.findViewById(R.id.notification_item_fullnotificationcontainer);
        mPlaceholderForShimmerContainer = view.findViewById(R.id.notification_item_shimmercontainer);

        mQuestionTextView = view.findViewById(R.id.notification_item_questiontextview);

        // for rounded corners of profile picture
        mProfilePictureImageView.setClipToOutline(true);
    }

    public void setOnClickListenerToOpenUserProfileInFocus() {
        View.OnClickListener redirectToUserProfileInFocus
                = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set notification to read
                DatabaseHelper.getReferenceToNotificationsOfParticularUser(FirebaseAuth.getInstance().getUid())
                        .child(mNID)
                        .child("read").setValue(true);
                root.setBackgroundResource(R.drawable.background_whiteroundcorners);
                Intent intent = new Intent(view.getContext(), ViewProfileActivity.class);
                intent.putExtra(Constants.UID, mUID);
                view.getContext().startActivity(intent);
            }
        };
        mProfilePictureImageView.setOnClickListener(redirectToUserProfileInFocus);
        mUserNameTextView.setOnClickListener(redirectToUserProfileInFocus);
    }

    public void setOnClickListenerToOpenFullQuestion() {
        View.OnClickListener redirectToFullQuestion
                = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set notification to read
                DatabaseHelper.getReferenceToNotificationsOfParticularUser(FirebaseAuth.getInstance().getUid())
                        .child(mNID)
                        .child("read").setValue(true);
                root.setBackgroundResource(R.drawable.background_whiteroundcorners);
                Intent intent = new Intent(view.getContext(), FullQuestionActivity.class);
                intent.putExtra(Constants.QID, mQID);
                view.getContext().startActivity(intent);
            }
        };
        mQuestionTextView.setOnClickListener(redirectToFullQuestion);
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    public void setmQID(String mQID) {
        this.mQID = mQID;
    }

    public String getmUID() {
        return mUID;
    }

    public String getmQID() {
        return mQID;
    }

    public ShimmerFrameLayout getmPlaceholderForShimmerContainer() {
        return mPlaceholderForShimmerContainer;
    }

    public LinearLayout getmFullNotificationContainer() {
        return mFullNotificationContainer;
    }

    public ImageView getmProfilePictureImageView() {
        return mProfilePictureImageView;
    }

    public TextView getmUserNameTextView() {
        return mUserNameTextView;
    }

    public TextView getmQuestionTextView() {
        return mQuestionTextView;
    }

    public RelativeLayout getRoot() {
        return root;
    }

    public String getmNID() {
        return mNID;
    }

    public void setmNID(String mNID) {
        this.mNID = mNID;
    }
}
