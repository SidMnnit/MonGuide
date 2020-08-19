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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.monguide.monguide.R;
import com.monguide.monguide.home.notifications.NotificationsFragment;
import com.monguide.monguide.models.Notification;
import com.monguide.monguide.models.QuestionSummary;

public class FirebaseNotificationAdapter extends FirebaseRecyclerAdapter<Notification, NotificationHolder> {
    private View view;
    private Context context;

    private NotificationsFragment notificationsFragment;

    public FirebaseNotificationAdapter(NotificationsFragment notificationsFragment, Context context, @NonNull FirebaseRecyclerOptions<Notification> options) {
        super(options);
        this.context = context.getApplicationContext();
        this.notificationsFragment = notificationsFragment;
        // so that once element in recyclerview
        // does not affect other
        // some methods are also overridden at
        // the bottom of the class for the same
        setHasStableIds(true);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationHolder holder, int position, @NonNull Notification notification) {
        notificationsFragment.getmEndOfContent().setVisibility(View.GONE);
        // will be deactivated when the profile picture finishes loading
        activateShimmer(holder);
        String nid = getRef(position).getKey();

        holder.setmNID(nid);

        if(notification.getRead()) {
            holder.getRoot().setBackgroundResource(R.drawable.background_whiteroundcorners);
        } else {
            holder.getRoot().setBackgroundResource(R.drawable.background_answeritem);
        }

        setUserDetailsInHolder(holder, notification);
        setListenersInHolder(holder, notification);
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationHolder(view);
    }

    private void activateShimmer(NotificationHolder holder) {
        holder.getmPlaceholderForShimmerContainer().setVisibility(View.VISIBLE);
        holder.getmFullNotificationContainer().setVisibility(View.GONE);
        holder.getmPlaceholderForShimmerContainer().startShimmer();
    }

    private void deactivateShimmer(NotificationHolder holder) {
        holder.getmFullNotificationContainer().setVisibility(View.VISIBLE);
        holder.getmPlaceholderForShimmerContainer().setVisibility(View.GONE);
        holder.getmPlaceholderForShimmerContainer().stopShimmer();
    }

    private void setUserDetailsInHolder(final NotificationHolder holder, Notification notification) {
        // set profile picture
        // this will take time getting from server
        StorageHelper.getReferenceToProfilePictureOfParticularUser(notification.getUid())
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
        DatabaseHelper.getReferenceToParticularUser(notification.getUid())
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

    private void setListenersInHolder(NotificationHolder holder, Notification notification) {
        // these will be used by listeners in holder class
        holder.setmUID(notification.getUid());
        holder.setmQID(notification.getQid());
        // set listeners
        holder.setOnClickListenerToOpenUserProfileInFocus();
        holder.setOnClickListenerToOpenFullQuestion();
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
