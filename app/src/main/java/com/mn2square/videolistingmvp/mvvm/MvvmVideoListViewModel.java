package com.mn2square.videolistingmvp.mvvm;

import com.mn2square.videolistingmvp.activity.presenter.manager.VideoListManager;
import com.mn2square.videolistingmvp.activity.presenter.manager.VideoListManagerImpl;
import com.mn2square.videolistingmvp.activity.presenter.manager.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.utils.FolderListGenerator;
import com.mn2square.videolistingmvp.utils.VideoSearch;
import android.app.Application;
import android.app.LoaderManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

public class MvvmVideoListViewModel extends AndroidViewModel implements VideoListManager.VideoListManagerListener {
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

    boolean mIsInSearchMode;
    public int mSortingType;
    private VideoListManagerImpl mVideoListManagerImpl;
    private MutableLiveData<VideoListInfo> videoListInfoLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mSearchText = new MutableLiveData<>();

    public MutableLiveData<VideoListInfo> getVideoListInfoLiveData() {
        return videoListInfoLiveData;
    }

    public MutableLiveData<String> getSearchText() {
        return mSearchText;
    }

    public MvvmVideoListViewModel(@NonNull Application application) {
        super(application);

        //initializing helper classes
        //should use dependency injection for testing
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application);
        mSortingType = settings.getInt(SORT_TYPE_PREFERENCE_KEY, 3);
        mVideoListManagerImpl = new VideoListManagerImpl(application, mSortingType);
        mVideoListManagerImpl.registerListener(this);
    }

    public void initLoader(LoaderManager loaderManager) {
        mVideoListManagerImpl.initLoader(loaderManager);
    }

    @Override
    public void onVideoListUpdate(VideoListInfo videoListInfo) {
        Log.d(TAG, "received onVideoListupdate");
        if (mIsInSearchMode) {
            videoListInfo.setVideosList(VideoSearch.SearchResult(mSearchText.getValue(), videoListInfo.getVideoListBackUp()));
            videoListInfo.setFolderListHashMap(VideoSearch.SearchResult(mSearchText.getValue(),
                    videoListInfo.getFolderListHashMapBackUp()));
        }
        else {
            if(videoListInfo.getVideosList() != null) {
                videoListInfo.getVideosList().clear();
            }
            videoListInfo.getVideosList().addAll(videoListInfo.getVideoListBackUp());

            if(videoListInfo.getFolderListHashMap() != null) {
                videoListInfo.getFolderListHashMap().clear();
            }
            videoListInfo.getFolderListHashMap().putAll(videoListInfo.getFolderListHashMapBackUp());

        }
        videoListInfo.setSavedVideoList(
                FolderListGenerator.getSavedVideoListFromFolderHashMap(
                        videoListInfo.getFolderListHashMap()
                )
        );
        videoListInfoLiveData.setValue(videoListInfo);
    }

    public void onVideoSearched(String searchText) {
        mSearchText.setValue(searchText);
        VideoListInfo videoListInfo = videoListInfoLiveData.getValue();
        if(searchText.trim().equals("")) {
            mIsInSearchMode = false;
            videoListInfo.getVideosList().clear();
            videoListInfo.getVideosList().addAll(videoListInfo.getVideoListBackUp());
            videoListInfo.getFolderListHashMap().clear();
            videoListInfo.getFolderListHashMap().putAll(videoListInfo.getFolderListHashMapBackUp());

        }
        else {
            mIsInSearchMode = true;
            videoListInfo.setVideosList(VideoSearch.SearchResult(searchText, videoListInfo.getVideoListBackUp()));
            videoListInfo.setFolderListHashMap(VideoSearch.SearchResult(searchText,
                    videoListInfo.getFolderListHashMapBackUp()));
        }

        videoListInfo.setSavedVideoList(FolderListGenerator.getSavedVideoListFromFolderHashMap(
                videoListInfo.getFolderListHashMap()));

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
