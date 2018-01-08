package com.mn2square.videolistingmvp.mvvm;

/**
 * Created by nitinagarwal on 4/9/17.
 */

public interface VideoUserInteraction {
    void onVideoSelected(String videoPath);
    void onVideoLongPressed(String videoPath, int itemId);
}
