package com.monguide.monguide.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.monguide.monguide.R;
import com.monguide.monguide.models.QuestionSummary;
import com.monguide.monguide.profile.ViewProfileActivity;
import com.monguide.monguide.utils.interfaces.Refreshable;

public class FirebaseAskedQuestionSummaryAdapter<T extends Refreshable> extends FirebaseRecyclerPagingAdapter<Boolean, QuestionSummaryHolder> {

    private View view;
    private T ref;
    private Context context;

    private ViewProfileActivity viewProfileActivity;

    public FirebaseAskedQuestionSummaryAdapter(ViewProfileActivity viewProfileActivity, Context context, T ref, @NonNull DatabasePagingOptions<Boolean> options) {
        super(options);
        this.ref = ref;
        this.context = context.getApplicationContext();
        this.viewProfileActivity = viewProfileActivity;
        // so that once element in recyclerview
        // does not affect other
        // some methods are also overridden at
        // the bottom of the class for the same
        setHasStableIds(true);
    }

    @Override
    protected void onBindViewHolder(@NonNull final QuestionSummaryHolder holder, int position, @NonNull Boolean aux) {
        viewProfileActivity.getmEndOfContent().setVisibility(View.GONE);
        final String qid = getRef(position).getKey();
        // will be deactivated when the profile picture finishes loading
        activateShimmer(holder);
        // fetch question corresponding to qid
        DatabaseHelper.getReferenceToParticularQuestion(qid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        QuestionSummary questionSummary = dataSnapshot.getValue(QuestionSummary.class);
                        // fill holder
                        setUserDetailsInHolder(holder, questionSummary);
                        setQuestionDetailsInHolder(holder, questionSummary);
                        setQuestionStatsInHolder(holder, questionSummary, qid);
                        setListenersInHolder(holder, questionSummary, qid);
                        setUpvoteDownvoteButtonState(holder, questionSummary);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void setUpvoteDownvoteButtonState(QuestionSummaryHolder holder, QuestionSummary questionSummary) {
        if(questionSummary.getUpvoters() == null ||
                !questionSummary.getUpvoters().containsKey(FirebaseAuth.getInstance().getUid())) {
            // set upvote button in unclicked state
            holder.setUpvoteButtonInUnclickedState();
        } else {
            // set upvote button in clicked state
            holder.setUpvoteButtonInClickedState();
        }
        if(questionSummary.getDownvoters() == null ||
                !questionSummary.getDownvoters().containsKey(FirebaseAuth.getInstance().getUid())) {
            // set downvote button in unclicked state
            holder.setDownvoteButtonInUnclickedState();
        } else {
            // set downvote button in clicked state
            holder.setDownvoteButtonInClickedState();
        }
    }

    private void activateShimmer(QuestionSummaryHolder holder) {
        holder.getmPlaceholderForShimmerContainer().setVisibility(View.VISIBLE);
        holder.getmFullQuestionSummaryContainer().setVisibility(View.GONE);
        holder.getmPlaceholderForShimmerContainer().startShimmer();
    }

    private void deactivateShimmer(QuestionSummaryHolder holder) {
        holder.getmFullQuestionSummaryContainer().setVisibility(View.VISIBLE);
        holder.getmPlaceholderForShimmerContainer().setVisibility(View.GONE);
        holder.getmPlaceholderForShimmerContainer().stopShimmer();
    }

    private void setUserDetailsInHolder(final QuestionSummaryHolder holder, QuestionSummary questionSummary) {
        // set profile picture
        // this will take time getting from server
        StorageHelper.getReferenceToProfilePictureOfParticularUser(questionSummary.getUid())
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                    String url = task.getResult().toString();
                    Glide.with(context)
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.getmProfilePictureImageView().setImageDrawable(resource);
                                    deactivateShimmer(holder);
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });
            }
        });
        // set name of user that created question
        DatabaseHelper.getReferenceToParticularUser(questionSummary.getUid())
                .child("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.getValue(String.class);
                        holder.getmUserNameTextView().setText(name);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void setQuestionDetailsInHolder(QuestionSummaryHolder holder, QuestionSummary questionSummary) {
        holder.getmTimeStampTextView().setText(questionSummary.getTimestamp());
        holder.getmTitleTextView().setText(questionSummary.getTitle());
        holder.getmBodyTextView().setText(questionSummary.getBody());
    }

    private void setListenersInHolder(QuestionSummaryHolder holder, QuestionSummary questionSummary, String qid) {
        // these will be used by listeners in holder class
        holder.setmUID(questionSummary.getUid());
        holder.setmQID(qid);
        // set listeners
        holder.setOnClickListenerToOpenUserProfileInFocus();
        holder.setOnClickListenerToOpenFullQuestion();
        holder.setOnClickListenerToUpvoteButton();
        holder.setOnclickListenerToDownvoteButton();
    }

    private void setQuestionStatsInHolder(@NonNull final QuestionSummaryHolder holder, @NonNull QuestionSummary questionSummary, String qid) {
        // load them statically and then add listeners
        // for dynamic updation as well
        holder.getmUpvoteCountTextView().setText(String.valueOf(questionSummary.getUpvoteCount()));
        holder.getmDownVoteCountTextView().setText(String.valueOf(questionSummary.getDownvoteCount()));
        holder.getmAnswerCountTextView().setText(String.valueOf(questionSummary.getAnswerCount()));
        DatabaseHelper.getReferenceToParticularQuestion(qid)
                .child("upvoteCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Integer.class) != null)
                            holder.getmUpvoteCountTextView()
                                .setText(String.valueOf(dataSnapshot.getValue(Integer.class)));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
        DatabaseHelper.getReferenceToParticularQuestion(qid)
                .child("downvoteCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Integer.class) != null)
                            holder.getmDownVoteCountTextView()
                                .setText(String.valueOf(dataSnapshot.getValue(Integer.class)));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
        DatabaseHelper.getReferenceToParticularQuestion(qid)
                .child("answerCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Integer.class) != null)
                            holder.getmAnswerCountTextView()
                                    .setText(String.valueOf(dataSnapshot.getValue(Integer.class)));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADING_INITIAL:
                ref.startRefreshingAnimation();
                break;
            case LOADING_MORE:
                break;
            case LOADED:
                ref.stopRefreshingAnimation();
                break;
            case ERROR:
                retry();
                break;
        }
    }

    @NonNull
    @Override
    public QuestionSummaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_questionsummary, parent, false);
        return new QuestionSummaryHolder(view);
    }


    // overriding these two so that one item in recyclerview
    // does not affect other
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}