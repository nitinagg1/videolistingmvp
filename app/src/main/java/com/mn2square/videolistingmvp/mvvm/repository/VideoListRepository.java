package com.mn2square.videolistingmvp.mvvm.repository;

import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.utils.AppExecutors;
import com.mn2square.videolistingmvp.utils.FolderListGenerator;
import com.mn2square.videolistingmvp.utils.VideoSearch;

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.WorkerThread;

import static com.mn2square.videolistingmvp.mvvm.ui.VideoListViewModel.DATE_ASC;
import static com.mn2square.videolistingmvp.mvvm.ui.VideoListViewModel.DATE_DESC;
import static com.mn2square.videolistingmvp.mvvm.ui.VideoListViewModel.NAME_ASC;
import static com.mn2square.videolistingmvp.mvvm.ui.VideoListViewModel.NAME_DESC;
import static com.mn2square.videolistingmvp.mvvm.ui.VideoListViewModel.SIZE_ASC;
import static com.mn2square.videolistingmvp.mvvm.ui.VideoListViewModel.SIZE_DESC;

public class VideoListRepository extends ContentObserver {
    private String mSearchText = "";
    private AppExecutors mAppExecutors;
    private static final String[] COLUMNS_OF_INTEREST = new String[] {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_ADDED
    };
    private Context mContext;
    private VideoListInfo mVideoListInfo;
    private MutableLiveData<VideoListInfo> mVideoListInfoLiveData;
    private static VideoListRepository sInstance;
    private Cursor mCursor;
    private int mSortingPref;

    public static VideoListRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (VideoListRepository.class) {
                if (sInstance == null) {
                    sInstance = new VideoListRepository(context);
                }
            }
        }
        return sInstance;
    }

    private VideoListRepository(Context context) {
        super(new Handler(Looper.myLooper()));
        mContext = context;
        mAppExecutors = AppExecutors.getInstance();
        mVideoListInfoLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<VideoListInfo> getVideoListInfoLiveData() {
        return mVideoListInfoLiveData;
    }

    public void initVideoList(int sortingPreference) {
        mSortingPref = sortingPreference;
        mVideoListInfo = new VideoListInfo();
        fetchVideoList(sortingPreference);
    }


    private void fetchVideoList(final int sortingPreference) {
        mAppExecutors.diskIO().execute(new Runnable() {
            @WorkerThread
            @Override
            public void run() {
                mCursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        COLUMNS_OF_INTEREST,
                        null,
                        null,
                        getSortOrder(sortingPreference));
                mCursor.registerContentObserver(VideoListRepository.this);
                onLoadFinished(mCursor);
            }
        });
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        mCursor.unregisterContentObserver(this);
        fetchVideoList(mSortingPref);
    }

    private String getSortOrder(int sortingPreference) {
        switch (sortingPreference) {
            case NAME_ASC:
                return MediaStore.Video.Media.DISPLAY_NAME + " ASC";
            case NAME_DESC:
                return MediaStore.Video.Media.DISPLAY_NAME + " DESC";
            case DATE_ASC:
                return MediaStore.Video.Media.DATE_ADDED + " ASC";
            case DATE_DESC:
                return MediaStore.Video.Media.DATE_ADDED + " DESC";
            case SIZE_ASC:
                return MediaStore.Video.Media.SIZE + " ASC";
            case SIZE_DESC:
                return MediaStore.Video.Media.SIZE + " DESC";
            default:
                return MediaStore.Video.Media.DATE_ADDED + " DESC";
        }
    }

    private void onLoadFinished(Cursor cursor) {
        updateVideoList(cursor);
        FolderListGenerator.generateFolderHashMap(mVideoListInfo.getVideoListBackUp(),
                mVideoListInfo.getFolderListHashMapBackUp());
        //fire update
        updateVideoListInfo(mVideoListInfo, mSearchText);
    }

    /**
     * Triggers update on the mVideoListInfoLiveData
     * @param videoListInfo
     */
    public void updateVideoListInfo(VideoListInfo videoListInfo, String filter) {
        mVideoListInfo = videoListInfo;
        mSearchText = filter;

        if(filter.trim().equals("")) {
            // If no filter
            videoListInfo.getVideosList().clear();
            videoListInfo.getVideosList().addAll(videoListInfo.getVideoListBackUp());
            videoListInfo.getFolderListHashMap().clear();
            videoListInfo.getFolderListHashMap().putAll(videoListInfo.getFolderListHashMapBackUp());
        } else {
            // Filter present
            videoListInfo.setVideosList(VideoSearch.SearchResult(filter, videoListInfo.getVideoListBackUp()));
            videoListInfo.setFolderListHashMap(VideoSearch.SearchResult(filter,
                    videoListInfo.getFolderListHashMapBackUp()));
        }

        videoListInfo.setSavedVideoList(FolderListGenerator.getSavedVideoListFromFolderHashMap(
                videoListInfo.getFolderListHashMap()));

        // Update on UIThread
        mVideoListInfoLiveData.postValue(videoListInfo);
    }

    public void filterVideos(String text) {
        updateVideoListInfo(mVideoListInfo, text);
    }


    private void updateVideoList(Cursor cursor) {
        mVideoListInfo.clearAll();
        cursor.moveToFirst();
        int coloumnIndexUri = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        int coloumnIndex;
        for (int i = 0; i < cursor.getCount(); i++) {
            mVideoListInfo.getVideoListBackUp().add(cursor.getString(coloumnIndexUri));
            coloumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            mVideoListInfo.getVideoIdHashMap().put(cursor.getString(coloumnIndexUri), cursor.getInt(coloumnIndex));
            coloumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            mVideoListInfo.getVideoTitleHashMap().put(cursor.getString(coloumnIndexUri), cursor.getString(coloumnIndex));
            coloumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
            mVideoListInfo.getVideoHeightHashMap().put(cursor.getString(coloumnIndexUri), cursor.getInt(coloumnIndex));
            coloumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
            mVideoListInfo.getVideoWidthHashMap().put(cursor.getString(coloumnIndexUri), cursor.getInt(coloumnIndex));
            coloumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            mVideoListInfo.getVideoDurationHashMap().put(cursor.getString(coloumnIndexUri), cursor.getInt(coloumnIndex));
            coloumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            mVideoListInfo.getVideoSizeHashMap().put(cursor.getString(coloumnIndexUri), cursor.getInt(coloumnIndex));
            cursor.moveToNext();
        }
    }

    public void getVideosWithNewSorting(int sortPreference) {
        // Re-fetch video-list
        fetchVideoList(sortPreference);
    }

    public void updateForDeleteVideo(int id) {
        mContext.getContentResolver().delete(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Video.Media._ID+ "=" + id, null);
    }

    public void updateForRenameVideo(int id, String newFilePath, String updatedTitle) {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(MediaStore.Video.Media.DATA, newFilePath);
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, updatedTitle);
        mContext.getContentResolver().update(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues,
                MediaStore.Video.Media._ID + "=" + id, null);
    }
}
