package com.mn2square.videolistingmvp.mvvm.ui.savedlistfragment;

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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class SavedListFragmentImpl extends Fragment {
    VideoListAdapter mSavedListAdapter;
    View mSavedVideoListView;
    ObservableListView mSavedListView;
    SavedListViewModel mSavedListViewModel;
    VideoUserInteraction mCallback;
    ObservableScrollViewCallbacks mObservableScrollViewCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mSavedVideoListView = inflater.inflate(R.layout.tab_videolist, container, false);
        mSavedListAdapter = new VideoListAdapter(getActivity(), R.layout.tab_child);
        mSavedListView = (ObservableListView) mSavedVideoListView.findViewById(R.id.ListView);
        mSavedListView.setAdapter(mSavedListAdapter);

        mSavedListView.addHeaderView(inflater.inflate(R.layout.padding, mSavedListView, false));
        return mSavedVideoListView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSavedListViewModel = ViewModelProviders.of(this).get(SavedListViewModel.class);
        subscribeToViewModel(mSavedListViewModel);
        registerForContextMenu(mSavedListView);

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

        mSavedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedVideo = mSavedListViewModel.mVideoListInfoLiveData.getValue()
                        .getSavedVideoList().get(i - 1); // adjust for padding
                mCallback.onVideoSelected(selectedVideo);
            }
        });

        mSavedListView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
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

    private void subscribeToViewModel(SavedListViewModel viewModel) {
        viewModel.mVideoListInfoLiveData.observe(this, new Observer<VideoListInfo>() {
            @Override
            public void onChanged(@Nullable VideoListInfo videoListInfo) {
                mSavedListAdapter.bindVideoList(videoListInfo.getSavedVideoList(), videoListInfo);
                mSavedListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String selectedVideo = mSavedListViewModel.mVideoListInfoLiveData.getValue()
                    .getSavedVideoList().get(info.position - 1);
            mCallback.onVideoLongPressed(selectedVideo, item.getItemId());
            return true;
        }
        return false;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        String selectedVideo = mSavedListViewModel.mVideoListInfoLiveData.getValue()
                .getSavedVideoList().get(info.position);
        menu.setHeaderTitle(selectedVideo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_video_long_press, menu);

    }

}
