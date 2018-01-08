package com.mn2square.videolistingmvp.mvvm.swipetabfragments.folderlistfragment;

import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.activity.presenter.manager.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.mvvm.repository.VideoListRepository.VideoListRepositoryImpl;
import com.mn2square.videolistingmvp.swipetabfragments.adapters.FolderListAdapter;
import com.mn2square.videolistingmvp.swipetabfragments.folderlistfragment.ObservableFolderList.ObservableExpandableListView;
import com.mn2square.videolistingmvp.swipetabfragments.folderlistfragment.views.FolderListFragmentView;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nitinagarwal on 3/15/17.
 */

public class FolderListFragmentViewModel extends AndroidViewModel {
    public static final String TAG = "FolderListViewModel";
    private static final String SORT_TYPE_PREFERENCE_KEY = "sort_type";
    protected MediatorLiveData<VideoListInfo> mVideoListInfoLiveData;
    private VideoListRepositoryImpl mVideoListRepositoryImpl;

    //need not be live, we will keep it in sync with mVideoListInfoLiveData
    protected ArrayList<String> mFolderNames;

    public FolderListFragmentViewModel(@NonNull Application application) {
        super(application);
        mVideoListInfoLiveData = new MediatorLiveData<>();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application);
        mVideoListRepositoryImpl = VideoListRepositoryImpl.getInstance(application, settings.getInt(SORT_TYPE_PREFERENCE_KEY, 3));

        mVideoListInfoLiveData.addSource(mVideoListRepositoryImpl.getVideoListInfoLiveData(), new Observer<VideoListInfo>() {
            @Override
            public void onChanged(@Nullable VideoListInfo videoListInfo) {
                mFolderNames = new ArrayList<>();
                mFolderNames.addAll(videoListInfo.getFolderListHashMap().keySet());
                Collections.sort(mFolderNames, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        if(lhs.lastIndexOf('/') > 0 && rhs.lastIndexOf('/') >0)
                        {
                            String lhsString = lhs.substring(lhs.lastIndexOf('/') + 1);
                            String rhsString = rhs.substring(rhs.lastIndexOf('/') + 1);
                            return lhsString.compareToIgnoreCase(rhsString);
                        } else {
                            return -1;
                        }
                    }
                });
                mVideoListInfoLiveData.setValue(videoListInfo);
            }
        });
    }
}
