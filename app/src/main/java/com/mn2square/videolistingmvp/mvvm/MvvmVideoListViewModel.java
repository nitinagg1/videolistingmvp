package com.mn2square.videolistingmvp.mvvm;

import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.mvvm.repository.VideoListRepository.VideoListRepositoryImpl;
import android.app.Application;
import android.app.LoaderManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class MvvmVideoListViewModel extends AndroidViewModel {
    public static final String TAG = "MvvmVideoListViewModel";
    private static final String SORT_TYPE_PREFERENCE_KEY = "sort_type";

    public static final int NAME_ASC = 0;
    public static final int NAME_DESC = 1;
    public static final int DATE_ASC = 2;
    public static final int DATE_DESC = 3;
    public static final int SIZE_ASC = 4;
    public static final int SIZE_DESC = 5;

    public static final int SHARE_VIDEO = 0;
    public static final int DELETE_VIDEO = 1;
    public static final int RENAME_VIDEO = 2;

    public int mSortingType;
    private VideoListRepositoryImpl mVideoListManagerImpl;
    private MediatorLiveData<VideoListInfo> videoListInfoLiveData ;

    public MediatorLiveData<VideoListInfo> getVideoListInfoLiveData() {
        return videoListInfoLiveData;
    }

    public MvvmVideoListViewModel(@NonNull Application application) {
        super(application);
        videoListInfoLiveData = new MediatorLiveData<>();

        //initializing helper classes
        //should use dependency injection for testing
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application);
        mSortingType = settings.getInt(SORT_TYPE_PREFERENCE_KEY, 3);
        mVideoListManagerImpl = VideoListRepositoryImpl.getInstance(application, mSortingType);

        videoListInfoLiveData.addSource(mVideoListManagerImpl.getVideoListInfoLiveData(), new Observer<VideoListInfo>() {
            @Override
            public void onChanged(@Nullable VideoListInfo videoListInfo) {
                Log.d(TAG, "Mediator livev data");
                videoListInfoLiveData.setValue(videoListInfo);
            }
        });
    }

    public void initLoader(LoaderManager loaderManager) {
        mVideoListManagerImpl.initLoader(loaderManager);
    }

    public void onVideoSearched(String searchText) {
        //this will trigger update across everything
        mVideoListManagerImpl.filterVideos(searchText);
//        mVideoListManagerImpl.updateVideoListInfo(videoListInfo);

//                if(mListFragment != null)
//                    mListFragment.bindVideoList(videoListInfo);
//                if(mFolderListFragment != null)
//                    mFolderListFragment.bindVideoList(videoListInfo);
//                if(mSavedListFragment != null)
//                    mListFragment.bindVideoList(videoListInfo);

    }

    public void updateSharedPreferenceAndGetNewList(int sortType, LoaderManager loaderManager) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplication());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SORT_TYPE_PREFERENCE_KEY, sortType);
        editor.apply();
        mVideoListManagerImpl.getVideosWithNewSorting(sortType, loaderManager);
    }

    public void updateForRenameVideo(int id, String newFilePath, String updatedTitle) {
        mVideoListManagerImpl.updateForRenameVideo(id, newFilePath, updatedTitle);
    }

    public void updateForDeleteVideo(int id) {
        mVideoListManagerImpl.updateForDeleteVideo(id);
    }
}
