package com.mn2square.videolistingmvp.mvvm.ui.folderlistfragment;

import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.mvvm.repository.VideoListRepository;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by nitinagarwal on 3/15/17.
 */

public class FolderListFragmentViewModel extends AndroidViewModel {
    public static final String TAG = "FolderListViewModel";
    private static final String SORT_TYPE_PREFERENCE_KEY = "sort_type";
    protected MediatorLiveData<VideoListInfo> mVideoListInfoLiveData;
    private VideoListRepository mVideoListRepositoryImpl;

    //need not be live, we will keep it in sync with mVideoListInfoLiveData
    protected ArrayList<String> mFolderNames;

    public FolderListFragmentViewModel(@NonNull Application application) {
        super(application);
        mVideoListInfoLiveData = new MediatorLiveData<>();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(application);
        mVideoListRepositoryImpl = VideoListRepository.getInstance(application, settings.getInt(SORT_TYPE_PREFERENCE_KEY, 3));

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
