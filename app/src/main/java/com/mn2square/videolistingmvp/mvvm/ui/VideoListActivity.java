package com.mn2square.videolistingmvp.mvvm.ui;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.mvvm.pojo.VideoRenameEvent;
import com.mn2square.videolistingmvp.utils.longpressmenuoptions.LongPressOptions;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.io.File;

public class VideoListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, TabLayout.OnTabSelectedListener, NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnCloseListener, ObservableScrollViewCallbacks, VideoUserInteraction {
    private View mRootView;
    private SearchView mSearchView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private Context mContext;
    private FloatingActionButton mFabRecondVideoButton;
    private float mPixelDensityFactor;
    Window mWindow;
    int mBaseTranslationY;
    private VideoListViewModel mViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews(this);
        setContentView(mRootView);

        mViewModel = ViewModelProviders.of(this).get(VideoListViewModel.class);
        subscribeToViewModel();
        subscribeToViewModelForFileEvents();
    }

    protected void subscribeToViewModel() {
        mViewModel.getToastMessageLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(VideoListActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getStatusBarColorLiveData().observe(this, new Observer<Pair<Integer, Integer>>() {
            @Override
            public void onChanged(@Nullable Pair<Integer, Integer> colorPair) {
                mAppBarLayout.setBackgroundResource(colorPair.first);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mWindow.setStatusBarColor(mContext.getResources().getColor(colorPair.second));
                }
            }
        });
        mViewModel.getVideoListInfoLiveData().observe(this, new Observer<VideoListInfo>() {
            @Override
            public void onChanged(@Nullable VideoListInfo videoListInfo) {

            }
        });
    }

    private void subscribeToViewModelForFileEvents() {
        mViewModel.getShowDeleteDialogEvent().observe(this, new Observer<Pair<Integer, String>>() {
            @Override
            public void onChanged(@Nullable Pair<Integer, String> fileInfo) {
                final int deleteVideoId = fileInfo.first;
                final String videoPath = fileInfo.second;
                LongPressOptions.deleteFile(VideoListActivity.this, videoPath,
                        deleteVideoId,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mViewModel.deleteVideo(deleteVideoId, videoPath);
                            }
                        }
                );
            }
        });

        mViewModel.getShowRenameDialogEvent().observe(this, new Observer<VideoRenameEvent>() {
            @Override
            public void onChanged(@Nullable final VideoRenameEvent videoRenameEvent) {
                LongPressOptions.renameFile(VideoListActivity.this, videoRenameEvent.getCurrentTitle(),
                        videoRenameEvent.getVideoPath(),
                        videoRenameEvent.getExtension(),
                        videoRenameEvent.getVideoId(),
                        new LongPressOptions.OnConfirmRenameListener() {
                            @Override
                            public void onConfirm(String fileName) {
                                mViewModel.renameVideo(videoRenameEvent, fileName);
                            }
                        }
                );
            }
        });
    }

    private void setupViews(Context context) {
        mContext = context;
        AppCompatActivity appCompatActivity = (AppCompatActivity)context;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mPixelDensityFactor = metrics.densityDpi/160f;
        mRootView = LayoutInflater.from(context).inflate(R.layout.activity_main, null);
        mViewPager = (ViewPager)mRootView.findViewById(R.id.viewpager);
        mViewPager.setCurrentItem(0);
        CharSequence[] titles = {
                context.getResources().getString(R.string.folder_tab_name),
                context.getResources().getString(R.string.list_tab_name),
                context.getResources().getString(R.string.saved_tab_name),
        };
        ViewPagerAdapter viewPagerAdapter =
                new ViewPagerAdapter(appCompatActivity.getSupportFragmentManager(), titles);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setCurrentItem(1);

        mWindow = ((AppCompatActivity) context).getWindow();

        mTabLayout = (TabLayout)mRootView.findViewById(R.id.tablayout);

        mAppBarLayout = (AppBarLayout)mRootView.findViewById(R.id.appBarLayout);

        mTabLayout.setupWithViewPager(mViewPager);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTabLayout.addOnTabSelectedListener(this);
        }

        mToolbar = (Toolbar) mRootView.findViewById(R.id.tool_bar);
        appCompatActivity.setSupportActionBar(mToolbar);
        DrawerLayout drawer = (DrawerLayout) mRootView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                (Activity) context, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) mRootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFabRecondVideoButton = (FloatingActionButton)mRootView.findViewById(R.id.fab_video_record);
        mFabRecondVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext,"fab clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        mViewModel.onNavigationItemSelected(id);

        DrawerLayout drawer = (DrawerLayout) mRootView.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewModel.onTabSelected(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging)
        {
            int toolbarHeight = mToolbar.getHeight();
            float currentHeaderTranslationY = ViewHelper.getTranslationY(mAppBarLayout);
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);

            ViewPropertyAnimator.animate(mAppBarLayout).cancel();

            ViewHelper.setTranslationY(mAppBarLayout, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        }
        else if(scrollState == ScrollState.UP) {
            hideToolbar();
        }
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mAppBarLayout);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mAppBarLayout).cancel();
            ViewPropertyAnimator.animate(mAppBarLayout).translationY(0).setDuration(200).start();
            ViewPropertyAnimator.animate(mFabRecondVideoButton).translationY(0).setDuration(200).start();
        }

        mAppBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mFabRecondVideoButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        //propagateToolbarState(true);
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mAppBarLayout);
        float fabTranslationY = ViewHelper.getTranslationY(mFabRecondVideoButton);
        int toolbarHeight = mToolbar.getHeight();
        int fabButtonBottomMargin = 16;
        fabButtonBottomMargin += 2; //adding 2 dp to make sure the button is hidden completely
        int floatingButtonHeight = mFabRecondVideoButton.getHeight() + (int)(fabButtonBottomMargin  * mPixelDensityFactor);
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mAppBarLayout).cancel();
            ViewPropertyAnimator.animate(mAppBarLayout).translationY(-toolbarHeight).setDuration(200).start();
        }

        if(fabTranslationY != floatingButtonHeight) {
            ViewPropertyAnimator.animate(mFabRecondVideoButton).cancel();
            ViewPropertyAnimator.animate(mFabRecondVideoButton).translationY(floatingButtonHeight).setDuration(200).start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.addSearchBar(menu.findItem(R.id.action_search));

        setSortingOptionChecked(menu);
        return true;
    }

    public void onVideoSearched(String seachText) {
        mViewModel.onVideoSearched(seachText);
    }

    public void addSearchBar(MenuItem searchViewMenuItem) {
        mSearchView = (SearchView) searchViewMenuItem.getActionView();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    private void setSortingOptionChecked(Menu menu) {
        switch (mViewModel.mSortingType) {
            case VideoListViewModel.NAME_ASC:
                menu.findItem(R.id.sort_name_asc).setChecked(true);
                break;
            case VideoListViewModel.NAME_DESC:
                menu.findItem(R.id.sort_name_dsc).setChecked(true);
                break;
            case VideoListViewModel.DATE_ASC:
                menu.findItem(R.id.sort_date_asc).setChecked(true);
                break;
            case VideoListViewModel.DATE_DESC:
                menu.findItem(R.id.sort_date_dsc).setChecked(true);
                break;
            case VideoListViewModel.SIZE_ASC:
                menu.findItem(R.id.sort_size_asc).setChecked(true);
                break;
            case VideoListViewModel.SIZE_DESC:
                menu.findItem(R.id.sort_size_dsc).setChecked(true);
                break;
            default:
                menu.findItem(R.id.sort_date_dsc).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort_name_asc:
                onSortTypeChanged(VideoListViewModel.NAME_ASC);
                item.setChecked(true);
                break;
            case R.id.sort_name_dsc:
                onSortTypeChanged(VideoListViewModel.NAME_DESC);
                item.setChecked(true);
                break;
            case R.id.sort_date_asc:
                onSortTypeChanged(VideoListViewModel.DATE_ASC);
                item.setChecked(true);
                break;
            case R.id.sort_date_dsc:
                onSortTypeChanged(VideoListViewModel.DATE_DESC);
                item.setChecked(true);
                break;
            case R.id.sort_size_asc:
                onSortTypeChanged(VideoListViewModel.SIZE_ASC);
                item.setChecked(true);
                break;
            case R.id.sort_size_dsc:
                onSortTypeChanged(VideoListViewModel.SIZE_DESC);
                item.setChecked(true);
                break;
            default:
                onSortTypeChanged(VideoListViewModel.DATE_DESC);
                item.setChecked(true);
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSortTypeChanged(int sortType) {
        mViewModel.onSortTypeChanged(sortType);
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //call view model on search
        this.onVideoSearched(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //call view model on search
        this.onVideoSearched(newText);
        return true;
    }

    @Override
    public void onVideoSelected(String videoPath) {
        mViewModel.onVideoSelected(videoPath);
    }

    @Override
    public void onVideoLongPressed(final String videoPath, int itemId) {
        switch (itemId) {
            case R.id.long_press_menu_share:
                LongPressOptions.shareFile(this, videoPath);
                break;

            default:
                mViewModel.onVideoLongPressed(videoPath, itemId);
                break;
        }
    }
}
