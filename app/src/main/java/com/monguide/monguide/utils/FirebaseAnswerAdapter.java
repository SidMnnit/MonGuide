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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.monguide.monguide.R;
import com.monguide.monguide.models.Answer;
import com.monguide.monguide.models.QuestionSummary;

public class FirebaseAnswerAdapter extends FirebaseRecyclerAdapter<Answer, AnswerHolder> {

    private Context context;

    public FirebaseAnswerAdapter(Context context, @NonNull FirebaseRecyclerOptions<Answer> options) {
        super(options);
        this.context = context.getApplicationContext();
        // so that once element in recyclerview
        // does not affect other
        // some methods are also overridden at
        // the bottom of the class for the same
        setHasStableIds(true);
    }

    @Override
    protected void onBindViewHolder(@NonNull final AnswerHolder holder, int position, @NonNull Answer answer) {
        // will be deactivated when the profile picture finishes loading
        activateShimmer(holder);

        setUserDetailsInHolder(holder, answer);
        setAnswerDetailsInHolder(holder, answer);
        setListenersInHolder(holder, answer);
    }

    private void activateShimmer(AnswerHolder holder) {
        holder.getmPlaceholderForShimmerContainer().setVisibility(View.VISIBLE);
        holder.getmFullAnswerContainer().setVisibility(View.GONE);
        holder.getmPlaceholderForShimmerContainer().startShimmer();
    }

    private void deactivateShimmer(AnswerHolder holder) {
        holder.getmFullAnswerContainer().setVisibility(View.VISIBLE);
        holder.getmPlaceholderForShimmerContainer().setVisibility(View.GONE);
        holder.getmPlaceholderForShimmerContainer().stopShimmer();
    }

    private void setUserDetailsInHolder(final AnswerHolder holder, Answer answer) {
        // set profile picture
        // this will take time getting from server
        StorageHelper.getReferenceToProfilePictureOfParticularUser(answer.getUid())
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
        DatabaseHelper.getReferenceToParticularUser(answer.getUid())
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

    private void setAnswerDetailsInHolder(AnswerHolder holder, Answer answer) {
        holder.getmTimeStampTextView().setText(answer.getTimestamp());
        holder.getmBodyTextView().setText(answer.getBody());
    }

    private void setListenersInHolder(AnswerHolder holder, Answer answer) {
        // these will be used by listeners in holder class
        holder.setmUID(answer.getUid());
        // set listeners
        holder.setOnClickListenerToOpenUserProfileInFocus();
    }

    @NonNull
    @Override
    public AnswerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnswerHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer, parent, false));
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
