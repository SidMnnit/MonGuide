package com.monguide.monguide.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.monguide.monguide.R;
import com.monguide.monguide.loginandsignup.LoginActivity;
import com.monguide.monguide.models.UserDetails;
import com.monguide.monguide.utils.Constants;
import com.monguide.monguide.utils.DatabaseHelper;
import com.monguide.monguide.utils.FirebaseAskedQuestionSummaryAdapter;
import com.monguide.monguide.utils.StorageHelper;
import com.monguide.monguide.utils.interfaces.Refreshable;

public class ViewProfileActivity extends AppCompatActivity implements Refreshable {
    private static final String TAG = "ViewProfileActivity";

    private String mCurrUID;

    private Toolbar mToolbar;

    private ImageView mProfilePictureImageView;
    private TextView mUsernameTextView;

    private TextView mJobProfileTextView;
    private TextView mCompanyNameTextView;

    private TextView mCourseNameTextView;
    private TextView mCollegeNameTextView;
    private TextView mGraduationYearTextView;

    private ShimmerFrameLayout mPlaceholderForShimmerContainer;
    private LinearLayout mFullUserDetailsContainer;

    private RecyclerView mRecyclerView;
    private FirebaseAskedQuestionSummaryAdapter mAdapter;

    private LinearLayout mEndOfContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewprofile);

        mToolbar = findViewById(R.id.activity_viewprofile_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            // open self profile if no uid is given in intent
            mCurrUID = FirebaseAuth.getInstance().getUid();
            // show logout option
            mToolbar.inflateMenu(R.menu.activity_viewprofile_toolbar);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    FirebaseAuth.getInstance().signOut();
                    startLoginActivity();
                    return true;
                }
            });
        } else {
            mCurrUID = extras.getString(Constants.UID);
        }

        mProfilePictureImageView = (ImageView) findViewById(R.id.activity_viewprofile_imageview_profilepicture);
        mUsernameTextView = (TextView) findViewById(R.id.activity_viewprofile_textview_username);
        mJobProfileTextView = (TextView) findViewById(R.id.activity_viewprofile_textview_jobprofile);
        mCompanyNameTextView = (TextView) findViewById(R.id.activity_viewprofile_textview_companyname);
        mCourseNameTextView = (TextView) findViewById(R.id.activity_viewprofile_textview_coursename);
        mCollegeNameTextView = (TextView) findViewById(R.id.activity_viewprofile_textview_collegename);
        mGraduationYearTextView = (TextView) findViewById(R.id.activity_viewprofile_textview_graduationyear);

        mPlaceholderForShimmerContainer = findViewById(R.id.activity_viewprofile_shimmercontainer);
        mFullUserDetailsContainer = findViewById(R.id.activity_viewprofile_fullprofiledetailscontainer);

        mRecyclerView = findViewById(R.id.activity_viewprofile_recyclerview);

        mEndOfContent = findViewById(R.id.container_endofcontent);

        mProfilePictureImageView.setClipToOutline(true);

        activateShimmer();
        populateUserDetails();
        populateQuestionsAsked();
    }

    private void reloadContent() {
        activateShimmer();
        populateUserDetails();
        populateQuestionsAsked();
    }

    private void populateUserDetails() {
        StorageHelper.getReferenceToProfilePictureOfParticularUser(mCurrUID)
                .getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                    String url = task.getResult().toString();
                    Glide.with(getApplicationContext())
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    mProfilePictureImageView.setImageDrawable(resource);
                                    deactivateShimmer();
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });
            }
        });
        DatabaseHelper.getReferenceToParticularUser(mCurrUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                mUsernameTextView.setText(userDetails.getName());
                mJobProfileTextView.setText(userDetails.getWorkDetails().getJobProfile());
                mCompanyNameTextView.setText(userDetails.getWorkDetails().getCompanyName());
                mCourseNameTextView.setText(userDetails.getEducationDetails().getCourseName());
                mCollegeNameTextView.setText(userDetails.getEducationDetails().getCollegeName());
                mGraduationYearTextView.setText(String.valueOf(userDetails.getEducationDetails().getGraduationYear()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void populateQuestionsAsked() {
        setupRecyclerViewWithAdapter();
    }

    private void setupRecyclerViewWithAdapter() {
        LinearLayoutManager linearLayoutForRecyclerView = new LinearLayoutManager(this);
        // to get latest post first
        linearLayoutForRecyclerView.setReverseLayout(true);
        linearLayoutForRecyclerView.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutForRecyclerView);
        mRecyclerView.setItemViewCacheSize(50);
        // mRecyclerView.setNestedScrollingEnabled(false);
        // query to get uid of all the questions
        // asked by a particular user
        Query baseQuery = DatabaseHelper.getReferenceToAllQuestionsAskedByUser(mCurrUID);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();
        DatabasePagingOptions<Boolean> options = new DatabasePagingOptions.Builder<Boolean>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, Boolean.class)
                .build();
        mAdapter = new FirebaseAskedQuestionSummaryAdapter(this, getApplicationContext(), this, options);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void activateShimmer() {
        mPlaceholderForShimmerContainer.setVisibility(View.VISIBLE);
        mFullUserDetailsContainer.setVisibility(View.GONE);
        mPlaceholderForShimmerContainer.startShimmer();
    }

    private void deactivateShimmer() {
        mFullUserDetailsContainer.setVisibility(View.VISIBLE);
        mPlaceholderForShimmerContainer.setVisibility(View.GONE);
        mPlaceholderForShimmerContainer.stopShimmer();
    }

    @Override
    public void startRefreshingAnimation() {

    }

    @Override
    public void stopRefreshingAnimation() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public LinearLayout getmEndOfContent() {
        return mEndOfContent;
    }
}
