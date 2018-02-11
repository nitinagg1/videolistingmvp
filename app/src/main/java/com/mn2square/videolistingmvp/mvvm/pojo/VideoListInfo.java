package com.mn2square.videolistingmvp.mvvm.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nitinagarwal on 3/7/17.
 */

public class VideoListInfo {

    private ArrayList<String> mVideoList;
    private ArrayList<String> mVideoListBackUp;
    private List<String> mSavedVideoList;
    private HashMap<String, List<String>> mFolderListHashMapBackUp;
    private HashMap<String, List<String>> mFolderListHashMap;
    private HashMap<String, Integer> mVideoIdHashMap;
    private HashMap<String, String>  mVideoTitleHashMap;
    private HashMap<String, Integer> mVideoSizeHashMap;
    private HashMap<String, Integer> mVideoDurationHashMap;
    private HashMap<String, Integer> mVideoHeightHashMap;
    private HashMap<String, Integer> mVideoWidthHashMap;

    public VideoListInfo()
    {
        mVideoList = new ArrayList<>();
        mVideoListBackUp = new ArrayList<>();
        mFolderListHashMap = new HashMap<>();
        mFolderListHashMapBackUp = new HashMap<>();
        mVideoIdHashMap = new HashMap<>();
        mVideoTitleHashMap = new HashMap<>();
        mVideoDurationHashMap = new HashMap<>();
        mVideoHeightHashMap = new HashMap<>();
        mVideoWidthHashMap = new HashMap<>();
        mVideoSizeHashMap = new HashMap<>();
    }

    public void clearAll()
    {
        mVideoListBackUp.clear();
        mVideoList.clear();
        mVideoIdHashMap.clear();
        mVideoSizeHashMap.clear();
        mVideoWidthHashMap.clear();
        mVideoHeightHashMap.clear();
        mVideoDurationHashMap.clear();
        mVideoTitleHashMap.clear();
        mFolderListHashMapBackUp.clear();
        mFolderListHashMap.clear();
    }

    public ArrayList<String> getVideosList() {
        return mVideoList;
    }

    public void setVideosList(ArrayList<String> videosList) {
        mVideoList = videosList;
    }

    public HashMap<String, Integer> getVideoIdHashMap() {
        return mVideoIdHashMap;
    }

    public void setVideoIdHashMap(HashMap<String, Integer> videoIdHashMap) {
        mVideoIdHashMap = videoIdHashMap;
    }

    public HashMap<String, String> getVideoTitleHashMap() {
        return mVideoTitleHashMap;
    }

    public void setVideoTitleHashMap(HashMap<String, String> videoTitleHashMap) {
        mVideoTitleHashMap = videoTitleHashMap;
    }

    public HashMap<String, Integer> getVideoSizeHashMap() {
        return mVideoSizeHashMap;
    }

    public void setVideoSizeHashMap(HashMap<String, Integer> videoSizeHashMap) {
        mVideoSizeHashMap = videoSizeHashMap;
    }

    public HashMap<String, Integer> getVideoDurationHashMap() {
        return mVideoDurationHashMap;
    }

    public void setVideoDurationHashMap(HashMap<String, Integer> videoDurationHashMap) {
        mVideoDurationHashMap = videoDurationHashMap;
    }

    public HashMap<String, Integer> getVideoHeightHashMap() {
        return mVideoHeightHashMap;
    }

    public void setVideoHeightHashMap(HashMap<String, Integer> videoHeightHashMap) {
        mVideoHeightHashMap = videoHeightHashMap;
    }

    public HashMap<String, Integer> getVideoWidthHashMap() {
        return mVideoWidthHashMap;
    }

    public void setVideoWidthHashMap(HashMap<String, Integer> videoWidthHashMap) {
        mVideoWidthHashMap = videoWidthHashMap;
    }

    public ArrayList<String> getVideoListBackUp() {
        return mVideoListBackUp;
    }

    public void setVideoListBackUp(ArrayList<String> videoListBackUp) {
        mVideoListBackUp = videoListBackUp;
    }

    public HashMap<String, List<String>> getFolderListHashMapBackUp() {
        return mFolderListHashMapBackUp;
    }

    public void setFolderListHashMapBackUp(HashMap<String, List<String>> folderListHashMapBackUp) {
        mFolderListHashMapBackUp = folderListHashMapBackUp;
    }

    public HashMap<String, List<String>> getFolderListHashMap() {
        return mFolderListHashMap;
    }

    public void setFolderListHashMap(HashMap<String, List<String>> folderListHashMap) {
        mFolderListHashMap = folderListHashMap;
    }

    public List<String> getSavedVideoList() {
        return mSavedVideoList;
    }

    public void setSavedVideoList(List<String> savedVideoList) {
        mSavedVideoList = savedVideoList;
    }

}
