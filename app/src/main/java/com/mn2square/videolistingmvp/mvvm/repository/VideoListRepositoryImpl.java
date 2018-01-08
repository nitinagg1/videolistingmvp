package com.mn2square.videolistingmvp.mvvm.repository;

import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.utils.FolderListGenerator;
import com.mn2square.videolistingmvp.utils.VideoSearch;

import android.app.LoaderManager;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import static com.mn2square.videolistingmvp.mvvm.MvvmVideoListViewModel.DATE_ASC;
import static com.mn2square.videolistingmvp.mvvm.MvvmVideoListViewModel.DATE_DESC;
import static com.mn2square.videolistingmvp.mvvm.MvvmVideoListViewModel.NAME_ASC;
import static com.mn2square.videolistingmvp.mvvm.MvvmVideoListViewModel.NAME_DESC;
import static com.mn2square.videolistingmvp.mvvm.MvvmVideoListViewModel.SIZE_ASC;
import static com.mn2square.videolistingmvp.mvvm.MvvmVideoListViewModel.SIZE_DESC;

public class VideoListRepositoryImpl implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int URL_LOADER_EXTERNAL = 0;
    private String mSearchText = "";
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
    private int mSortingPreference;
    private static VideoListRepositoryImpl sInstance;

    public static VideoListRepositoryImpl getInstance(Context context, int sortingPreference) {
        if (sInstance == null) {
            synchronized (VideoListRepositoryImpl.class) {
                if (sInstance == null) {
                    sInstance = new VideoListRepositoryImpl(context, sortingPreference);
                }
            }
        }
        return sInstance;
    }

    private VideoListRepositoryImpl(Context context, int sortingPreference) {
        mContext = context;
        mSortingPreference = sortingPreference;

        mVideoListInfoLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<VideoListInfo> getVideoListInfoLiveData() {
        return mVideoListInfoLiveData;
    }

    public void initLoader(LoaderManager loaderManager) {
        loaderManager.initLoader(URL_LOADER_EXTERNAL, null, this);
        mVideoListInfo = new VideoListInfo();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (mSortingPreference) {
            case NAME_ASC:
                return new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS_OF_INTEREST, null, null,
                        MediaStore.Video.Media.DISPLAY_NAME + " ASC");
            case NAME_DESC:
                return new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS_OF_INTEREST, null, null,
                        MediaStore.Video.Media.DISPLAY_NAME + " DESC");
            case DATE_ASC:
                return new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS_OF_INTEREST, null, null,
                        MediaStore.Video.Media.DATE_ADDED + " ASC");
            case DATE_DESC:
                return new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS_OF_INTEREST, null, null,
                        MediaStore.Video.Media.DATE_ADDED + " DESC");
            case SIZE_ASC:
                return new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS_OF_INTEREST, null, null,
                        MediaStore.Video.Media.SIZE + " ASC");
            case SIZE_DESC:
                return new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS_OF_INTEREST, null, null,
                        MediaStore.Video.Media.SIZE + " DESC");
            default:
                return new CursorLoader(mContext, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, COLUMNS_OF_INTEREST, null, null,
                        MediaStore.Video.Media.DATE_ADDED + " DESC");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        if(cursor != null)
        {
            updateVideoList(cursor);

            FolderListGenerator.generateFolderHashMap(mVideoListInfo.getVideoListBackUp(),
                    mVideoListInfo.getFolderListHashMapBackUp());

            //fire update
            updateVideoListInfo(mVideoListInfo, mSearchText);
        }
    }

    /**
     * Triggers update on the mVideoListInfoLiveData
     * @param videoListInfo
     */
    public void updateVideoListInfo(VideoListInfo videoListInfo, String filter) {
        mVideoListInfo = videoListInfo;
        mSearchText = filter;

        if(filter.trim().equals("")) {
            videoListInfo.getVideosList().clear();
            videoListInfo.getVideosList().addAll(videoListInfo.getVideoListBackUp());
            videoListInfo.getFolderListHashMap().clear();
            videoListInfo.getFolderListHashMap().putAll(videoListInfo.getFolderListHashMapBackUp());
        } else {
            videoListInfo.setVideosList(VideoSearch.SearchResult(filter, videoListInfo.getVideoListBackUp()));
            videoListInfo.setFolderListHashMap(VideoSearch.SearchResult(filter,
                    videoListInfo.getFolderListHashMapBackUp()));
        }

        videoListInfo.setSavedVideoList(FolderListGenerator.getSavedVideoListFromFolderHashMap(
                videoListInfo.getFolderListHashMap()));

        mVideoListInfoLiveData.setValue(videoListInfo);
    }

    public void filterVideos(String text) {
        updateVideoListInfo(mVideoListInfo, text);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    
    private void updateVideoList(Cursor cursor)
    {
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

    public void getVideosWithNewSorting(int sortType, LoaderManager loaderManager) {
        mSortingPreference = sortType;
        loaderManager.restartLoader(URL_LOADER_EXTERNAL, null, this);
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
