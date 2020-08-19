package com.monguide.monguide.home.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.firebase.database.Query;
import com.monguide.monguide.R;
import com.monguide.monguide.models.QuestionSummary;
import com.monguide.monguide.utils.DatabaseHelper;
import com.monguide.monguide.utils.FirebaseQuestionSummaryAdapter;
import com.monguide.monguide.utils.interfaces.Refreshable;

public class FeedFragment extends Fragment implements Refreshable {
    private RecyclerView mRecyclerView;
    private FirebaseQuestionSummaryAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_feed, container, false);

        mSwipeRefreshLayout = inflatedView.findViewById(R.id.fragment_feed_swiperefereshlayout);

        mRecyclerView = inflatedView.findViewById(R.id.fragment_feed_recyclerview);
        setupRecyclerViewWithAdapter();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorYellow);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
            }
        });

        return inflatedView;
    }

    private void setupRecyclerViewWithAdapter() {
        LinearLayoutManager linearLayoutForRecyclerView = new LinearLayoutManager(getContext());
        // to get latest post first
        linearLayoutForRecyclerView.setReverseLayout(true);
        linearLayoutForRecyclerView.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutForRecyclerView);
        mRecyclerView.setItemViewCacheSize(50);
        Query baseQuery = DatabaseHelper.getReferenceToAllQuestions();
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();
        DatabasePagingOptions<QuestionSummary> options = new DatabasePagingOptions.Builder<QuestionSummary>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, QuestionSummary.class)
                .build();
        mAdapter = new FirebaseQuestionSummaryAdapter(this.getContext(), this, options);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void startRefreshingAnimation() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    public void stopRefreshingAnimation() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
