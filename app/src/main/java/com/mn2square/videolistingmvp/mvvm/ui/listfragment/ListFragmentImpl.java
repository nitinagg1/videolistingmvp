package com.mn2square.videolistingmvp.mvvm.ui.listfragment;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.mvvm.ui.VideoUserInteraction;
import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.mvvm.ui.MvvmVideoListActivity;
import com.mn2square.videolistingmvp.mvvm.ui.adapters.VideoListAdapter;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import static com.mn2square.videolistingmvp.mvvm.ui.MvvmVideoListViewModel.TAG;

public class ListFragmentImpl extends Fragment {
    View mFragemntVideoListView;
    VideoListAdapter mVideoListAdapter;
    ObservableListView mListView;
    ObservableScrollViewCallbacks mObservableScrollViewCallbacks;
    ListFragmentViewModel mListFragmentViewModel;
    VideoUserInteraction mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragemntVideoListView = inflater.inflate(R.layout.tab_videolist, container, false);
        mVideoListAdapter = new VideoListAdapter(getActivity(), R.layout.tab_child);
        mListView = (ObservableListView) mFragemntVideoListView.findViewById(R.id.ListView);
        mListView.setAdapter(mVideoListAdapter);
        mListView.addHeaderView(inflater.inflate(R.layout.padding, mListView, false));
        return mFragemntVideoListView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListFragmentViewModel = ViewModelProviders.of(this).get(ListFragmentViewModel.class);
        subscribeToViewModel(mListFragmentViewModel);

        registerForContextMenu(mListView);

        try {
            mCallback = ((MvvmVideoListActivity)getActivity());
        } catch (ClassCastException ex) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement VideoUserInteraction");
        }
        try {
            mObservableScrollViewCallbacks = (MvvmVideoListActivity) getActivity();
        } catch (ClassCastException ex) {
            throw new ClassCastException("videolistingactivityview must implement ObservalbleScrollViewCallbacks");
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedVideo = mListFragmentViewModel.mVideoListInfoLiveData.getValue().getVideosList().get(i - 1);
                mCallback.onVideoSelected(selectedVideo);
            }
        });

        mListView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
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

    private void subscribeToViewModel(ListFragmentViewModel viewModel) {
        viewModel.mVideoListInfoLiveData.observe(this, new Observer<VideoListInfo>() {
            @Override
            public void onChanged(@Nullable VideoListInfo videoListInfo) {
                Log.d(TAG, "bindVideoList");
                Log.d(TAG, videoListInfo.toString());
                Log.d(TAG, videoListInfo.getVideosList().toString());

                mVideoListAdapter.bindVideoList(videoListInfo.getVideosList(), videoListInfo);
                mVideoListAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //TODO: should handle the selectedVideo using View-ViewModel dance
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        String selectedVideo = mListFragmentViewModel.mVideoListInfoLiveData
                .getValue().getVideosList().get(info.position - 1);
        menu.setHeaderTitle(selectedVideo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_video_long_press, menu);

    }

     @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String selectedVideo = mListFragmentViewModel.mVideoListInfoLiveData.getValue().getVideosList().get(info.position - 1); //adjusting for the padding
            mCallback.onVideoLongPressed(selectedVideo, item.getItemId());
            return true;
        }
        return false;
    }

}
