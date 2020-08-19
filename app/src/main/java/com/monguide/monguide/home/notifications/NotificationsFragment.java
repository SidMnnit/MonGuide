package com.monguide.monguide.home.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.monguide.monguide.R;
import com.monguide.monguide.models.Answer;
import com.monguide.monguide.models.Notification;
import com.monguide.monguide.utils.DatabaseHelper;
import com.monguide.monguide.utils.FirebaseAnswerAdapter;
import com.monguide.monguide.utils.FirebaseNotificationAdapter;
import com.monguide.monguide.utils.FirebaseQuestionSummaryAdapter;

public class NotificationsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private FirebaseNotificationAdapter mAdapter;

    private LinearLayout mEndOfContent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_notifications, container, false);

        mRecyclerView = inflatedView.findViewById(R.id.fragment_notifications_recyclerview);

        mEndOfContent = inflatedView.findViewById(R.id.container_endofcontent);

        setupRecyclerViewWithAdapter();

        return inflatedView;
    }

    private void setupRecyclerViewWithAdapter() {
        LinearLayoutManager linearLayoutForRecyclerView = new LinearLayoutManager(this.getContext());
        // to get latest notifications first
        linearLayoutForRecyclerView.setReverseLayout(true);
        linearLayoutForRecyclerView.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutForRecyclerView);
        mRecyclerView.setItemViewCacheSize(50);
        Query baseQuery = DatabaseHelper.getReferenceToNotificationsOfParticularUser(
                FirebaseAuth.getInstance().getUid()
        );
        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>()
                .setQuery(baseQuery, Notification.class)
                .build();
        mAdapter = new FirebaseNotificationAdapter(this, this.getContext(), options);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    public LinearLayout getmEndOfContent() {
        return mEndOfContent;
    }
}

