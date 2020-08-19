package com.monguide.monguide.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.monguide.monguide.R;
import com.monguide.monguide.home.feed.FeedFragment;
import com.monguide.monguide.home.notifications.NotificationsFragment;
import com.monguide.monguide.profile.ViewProfileActivity;
import com.monguide.monguide.question.AddQuestionActivity;

public class HomeActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigationView;

    private FloatingActionButton mAddQuestionFloatingActionButton;

    private Fragment mFeedFragment;
    private Fragment mNotificationFragment;

    private FragmentManager mFragmentManager;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.activity_home_bottomnavigationview_menuitem_home :
                    loadFragment(mFeedFragment);
                    break;
                case R.id.activity_home_bottomnavigationview_menuitem_notifications:
                    loadFragment(mNotificationFragment);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.activity_home_toolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // open user profile
                        startActivity(new Intent(HomeActivity.this, ViewProfileActivity.class));
                        return true;
                    }
                });

        mAddQuestionFloatingActionButton = findViewById(R.id.activity_home_addquestionfloatingactionbutton);
        mAddQuestionFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddQuestionActivity();
            }
        });
        mFeedFragment = new FeedFragment();
        mNotificationFragment = new NotificationsFragment();

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(R.id.activity_home_fragmentcontainer, mNotificationFragment)
                .hide(mNotificationFragment).commit();
        mFragmentManager.beginTransaction()
                .add(R.id.activity_home_fragmentcontainer, mFeedFragment)
                .hide(mFeedFragment).commit();
        loadFragment(mFeedFragment);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_home_bottomnavigationview);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomNavigationView.setSelectedItemId(R.id.activity_home_bottomnavigationview_menuitem_home);
    }

    private void startAddQuestionActivity() {
        startActivity(new Intent(HomeActivity.this, AddQuestionActivity.class));
    }

    private void loadFragment(Fragment fragment) {
        if(fragment == mFeedFragment) {
            mFragmentManager.beginTransaction().hide(mNotificationFragment).show(mFeedFragment).commit();
        } else {
            mFragmentManager.beginTransaction().hide(mFeedFragment).show(mNotificationFragment).commit();
        }
    }

}
