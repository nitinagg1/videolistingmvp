package com.mn2square.videolistingmvp.mvvm.pojo;


public class VideoRenameEvent {
    private String mCurrentTitle;
    private String mVideoPath;
    private String mExtension;
    private int mVideoId;

    public VideoRenameEvent(String currentTitle, String videoPath, String extension, int videoId) {
        mCurrentTitle = currentTitle;
        mVideoPath = videoPath;
        mExtension = extension;
        mVideoId = videoId;
    }

    public String getCurrentTitle() {
        return mCurrentTitle;
    }

    public void setCurrentTitle(String currentTitle) {
        mCurrentTitle = currentTitle;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String videoPath) {
        mVideoPath = videoPath;
    }

    public String getExtension() {
        return mExtension;
    }

    public void setExtension(String extension) {
        mExtension = extension;
    }

    public int getVideoId() {
        return mVideoId;
    }

    public void setVideoId(int videoId) {
        mVideoId = videoId;
    }
}
