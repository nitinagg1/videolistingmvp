package com.mn2square.videolistingmvp.mvvm.ui;

import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.mvvm.repository.VideoListRepository;
import com.mn2square.videolistingmvp.utils.SingleLiveEvent;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

public class VideoListViewModel extends AndroidViewModel {
    public static final String TAG = "VideoListViewModel";
    private static final String SORT_TYPE_PREFERENCE_KEY = "sort_type";

    public static final int NAME_ASC = 0;
    public static final int NAME_DESC = 1;
    public static final int DATE_ASC = 2;
    public static final int DATE_DESC = 3;
    public static final int SIZE_ASC = 4;
    public static final int SIZE_DESC = 5;

    public int mSortingType;
    private Context mContext;
    private VideoListRepository mVideoListManagerImpl;
    private MediatorLiveData<VideoListInfo> videoListInfoLiveData ;
    private SingleLiveEvent<String> mToastMessageLiveData;
    private SingleLiveEvent<Pair<Integer, Integer>> mStatusBarColorLiveData;


    public MediatorLiveData<VideoListInfo> getVideoListInfoLiveData() {
        return videoListInfoLiveData;
    }

    public SingleLiveEvent<String> getToastMessageLiveData() {
        return mToastMessageLiveData;
    }

    public SingleLiveEvent<Pair<Integer, Integer>> getStatusBarColorLiveData() {
        return mStatusBarColorLiveData;
    }

    public VideoListViewModel(@NonNull Application application) {
        super(application);
        mContext = application;
        videoListInfoLiveData = new MediatorLiveData<>();
        mStatusBarColorLiveData = new SingleLiveEvent<>();
        mToastMessageLiveData = new SingleLiveEvent<>();

        //initializing helper classes
        //should use dependency injection for testing
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application);
        mSortingType = settings.getInt(SORT_TYPE_PREFERENCE_KEY, 3);
        mVideoListManagerImpl = VideoListRepository.getInstance(application);

        videoListInfoLiveData.addSource(mVideoListManagerImpl.getVideoListInfoLiveData(), new Observer<VideoListInfo>() {
            @Override
            public void onChanged(@Nullable VideoListInfo videoListInfo) {
                Log.d(TAG, "Mediator livev data");
                videoListInfoLiveData.setValue(videoListInfo);
            }
        });
        mVideoListManagerImpl.initVideoList(mSortingType);
    }

    public void onVideoSearched(String searchText) {
        //this will trigger update across everything
        mVideoListManagerImpl.filterVideos(searchText);
    }

    public void onSortTypeChanged(int sortType) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplication());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SORT_TYPE_PREFERENCE_KEY, sortType);
        editor.apply();
        mVideoListManagerImpl.getVideosWithNewSorting(sortType);
    }

    public void updateForRenameVideo(int id, String newFilePath, String updatedTitle) {
        mVideoListManagerImpl.updateForRenameVideo(id, newFilePath, updatedTitle);
    }

    public void updateForDeleteVideo(int id) {
        mVideoListManagerImpl.updateForDeleteVideo(id);
    }

    public void onNavigationItemSelected(int id) {
        if (id == R.id.nav_settings) {
            mToastMessageLiveData.setValue("settings Clicked");
        } else if (id == R.id.nav_gallery) {
            mToastMessageLiveData.setValue("gallery Clicked");
        } else if (id == R.id.nav_help) {
            mToastMessageLiveData.setValue("hep Clicked");
        } else if (id == R.id.nav_share) {
            mToastMessageLiveData.setValue("share Clicked");
        } else if (id == R.id.nav_buy_pro) {
            mToastMessageLiveData.setValue("buy pro Clicked");
        }
    }


    public void onTabSelected(int i) {
        switch (i) {
            case 0:
                mStatusBarColorLiveData.setValue(new Pair<Integer, Integer>(
                        R.color.black_dialog_header,
                        R.color.transparent_black));
                break;
            case 1:
                mStatusBarColorLiveData.setValue(new Pair<Integer, Integer>(
                        R.color.colorPrimary,
                        R.color.colorPrimaryDark)
                );
                break;
            case 2:
                mStatusBarColorLiveData.setValue(new Pair<Integer, Integer>(
                        R.color.primary,
                        R.color.primaryDark)
                );
                break;
            default:
                mStatusBarColorLiveData.setValue(new Pair<Integer, Integer>(R.color.black_dialog_header, R.color.transparent_black));
        }
    }

    public void onVideoSelected(String videoPath) {
        mToastMessageLiveData.setValue(videoPath + "clicked");
    }
}
