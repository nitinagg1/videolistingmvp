package com.mn2square.videolistingmvp.activity.presenter.manager;

import com.mn2square.videolistingmvp.activity.presenter.manager.pojo.VideoListInfo;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;

/**
 * Created by nitinagarwal on 3/7/17.
 */

public interface VideoListManager {
    interface VideoListManagerListener {
        void onVideoListUpdate(VideoListInfo videoListInfo);
    }

    void initLoader(LoaderManager loaderManager);
    void getVideosWithNewSorting(int sortType, LoaderManager loaderManager);
    void registerListener(VideoListManagerListener videoListManagerListener);

    void unRegisterListener();

}
