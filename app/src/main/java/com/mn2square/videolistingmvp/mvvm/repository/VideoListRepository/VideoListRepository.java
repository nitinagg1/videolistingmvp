package com.mn2square.videolistingmvp.mvvm.repository.VideoListRepository;

import android.app.LoaderManager;

/**
 * Created by nitinagarwal on 3/7/17.
 */

public interface VideoListRepository {
    void initLoader(LoaderManager loaderManager);
    void getVideosWithNewSorting(int sortType, LoaderManager loaderManager);
}
