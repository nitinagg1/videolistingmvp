package com.mn2square.videolistingmvp.swipetabfragments.ListFragement.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.activity.presenter.VideoListingActivity;
import com.mn2square.videolistingmvp.activity.presenter.VideoUserInteraction;
import com.mn2square.videolistingmvp.swipetabfragments.VideoListFragmentInterface.VideoListFragmentInterface;
import com.mn2square.videolistingmvp.swipetabfragments.ListFragement.views.ListFragmentViewImpl;
import com.mn2square.videolistingmvp.activity.presenter.manager.pojo.VideoListInfo;

/**
 * Created by nitinagarwal on 3/12/17.
 */

public class ListFragmentImpl extends Fragment implements VideoListFragmentInterface {

    ListFragmentViewImpl mListFragmentViewImpl;
    VideoListInfo mVideoListInfo;
    VideoUserInteraction mCallback;
    ObservableScrollViewCallbacks mObservableScrollViewCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mListFragmentViewImpl = new ListFragmentViewImpl(getActivity(), container, inflater);

        mListFragmentViewImpl.getListView().addHeaderView(inflater.inflate(R.layout.padding, mListFragmentViewImpl.getListView(), false));
        return mListFragmentViewImpl.getRootView();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((VideoListingActivity)getActivity()).registerListener(this);
        ((VideoListingActivity)getActivity()).fetchVideoList();
        registerForContextMenu(mListFragmentViewImpl.getListView());

        try
        {
            mCallback = ((VideoListingActivity)getActivity());
        }
        catch (ClassCastException ex) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement VideoUserInteraction");
        }

        try
        {
            mObservableScrollViewCallbacks = ((VideoListingActivity) getActivity()).getVideoListActivityView();

        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException("videolistingactivityview must implement ObservalbleScrollViewCallbacks");
        }


        mListFragmentViewImpl.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedVideo = mVideoListInfo.getVideosList().get(i - 1);
                mCallback.onVideoSelected(selectedVideo);
            }
        });

        mListFragmentViewImpl.getListView().setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                mObservableScrollViewCallbacks.onScrollChanged(scrollY, firstScroll, dragging);
            }

            @Override
            public void onDownMotionEvent() {
                mObservableScrollViewCallbacks.onDownMotionEvent();
            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                mObservableScrollViewCallbacks.onUpOrCancelMotionEvent(scrollState);
            }
        });
    }


    @Override
    public void bindVideoList(VideoListInfo videoListInfo) {
        mVideoListInfo = videoListInfo;
        mListFragmentViewImpl.bindVideoList(videoListInfo.getVideosList(), videoListInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(getUserVisibleHint()) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String selectedVideo = mVideoListInfo.getVideosList().get(info.position - 1); //adjusting for the padding
            mCallback.onVideoLongPressed(selectedVideo, item.getItemId());
            return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        String selectedVideo = mVideoListInfo.getVideosList().get(info.position - 1);
        menu.setHeaderTitle(selectedVideo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_video_long_press, menu);

    }

}
