package com.mn2square.videolistingmvp.mvvm.ui.folderlistfragment;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.mvvm.ui.VideoUserInteraction;
import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.mvvm.ui.MvvmVideoListActivity;
import com.mn2square.videolistingmvp.mvvm.ui.adapters.FolderListAdapter;
import com.mn2square.videolistingmvp.mvvm.ui.folderlistfragment.ObservableFolderList.ObservableExpandableListView;

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
import android.widget.ExpandableListView;


public class FolderListFragmentImpl extends Fragment {
    View mFragmentFolderListView;
    ObservableExpandableListView mExpandableListView;
    FolderListAdapter mFolderListAdapter;

    FolderListFragmentViewModel mFolderListFragmentViewModel;
    VideoUserInteraction mCallback;
    ObservableScrollViewCallbacks mObservableScrollViewCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFragmentFolderListView = inflater.inflate(R.layout.tab_folderlist, container, false);
        mExpandableListView = (ObservableExpandableListView) mFragmentFolderListView.findViewById(R.id.expandablelistview);
        mFolderListAdapter = new FolderListAdapter(getActivity());
        mExpandableListView.setAdapter(mFolderListAdapter);

        mExpandableListView.addHeaderView(inflater.inflate(R.layout.padding, mExpandableListView, false));
        return mFragmentFolderListView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFolderListFragmentViewModel = ViewModelProviders.of(this).get(FolderListFragmentViewModel.class);
        subscribeToViewModel(mFolderListFragmentViewModel);
        registerForContextMenu(mExpandableListView);
        try {
            mCallback = ((MvvmVideoListActivity)getActivity());

        } catch (ClassCastException ex) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement VideoUserInteraction");
        }

        try {
            mObservableScrollViewCallbacks = (MvvmVideoListActivity) getActivity();
        } catch (ClassCastException ex) {
            throw new ClassCastException("MvvmVideoListActivity must implement ObservalbleScrollViewCallbacks");
        }

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String selectedVideo = mFolderListFragmentViewModel.mVideoListInfoLiveData.getValue()
                        .getFolderListHashMap().get(mFolderListFragmentViewModel.mFolderNames.get(i)).get(i1);
                mCallback.onVideoSelected(selectedVideo);
                return false;
            }
        });

        mExpandableListView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
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

    private void subscribeToViewModel(FolderListFragmentViewModel viewModel) {
        viewModel.mVideoListInfoLiveData.observe(this, new Observer<VideoListInfo>() {
            @Override
            public void onChanged(@Nullable VideoListInfo videoListInfo) {
                mFolderListAdapter.bindVideoList(videoListInfo.getFolderListHashMap(), mFolderListFragmentViewModel.mFolderNames, videoListInfo);
                mFolderListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
            int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
            String selectedVideo = mFolderListFragmentViewModel.mVideoListInfoLiveData.getValue()
                    .getFolderListHashMap().get(mFolderListFragmentViewModel.mFolderNames.get(group)).get(child);
            mCallback.onVideoLongPressed(selectedVideo, item.getItemId());
            return true;
        }
        return false;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            return;
        }
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
        String selectedVideo = mFolderListFragmentViewModel.mVideoListInfoLiveData.getValue()
                .getFolderListHashMap().get(mFolderListFragmentViewModel.mFolderNames.get(group)).get(child);

        menu.setHeaderTitle(selectedVideo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_video_long_press, menu);
    }
}
