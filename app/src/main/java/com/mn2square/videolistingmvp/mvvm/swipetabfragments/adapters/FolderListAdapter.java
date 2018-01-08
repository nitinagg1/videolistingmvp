package com.mn2square.videolistingmvp.mvvm.swipetabfragments.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.utils.Converters;
import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.utils.thumbnailutils.CustomImageView;
import com.mn2square.videolistingmvp.utils.thumbnailutils.ThumbnailCreateor;
import com.mn2square.videolistingmvp.utils.thumbnailutils.BitmapCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nitinagarwal on 3/15/17.
 */

public class FolderListAdapter extends BaseExpandableListAdapter {

    Context mContext;
    private Bitmap bmp;

    HashMap<String, List<String>> mFolderListHaspMap;
    ArrayList<String> mFolderNames;
    private VideoListInfo mVideoListInfo;

    public void bindVideoList(HashMap<String, List<String>> folderListHashMap, ArrayList<String> folderNames, VideoListInfo videoListInfo)
    {
        mFolderListHaspMap = folderListHashMap;
        mFolderNames = folderNames;
        mVideoListInfo = videoListInfo;
    }

    public FolderListAdapter(Context context)
    {
        mContext = context;

        int w = 100, h = 100;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        bmp = Bitmap.createBitmap(w, h, conf);
    }

    @Override
    public int getGroupCount() {
        if(mFolderNames == null)
            return 0;
        return mFolderNames.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(mFolderListHaspMap == null)
            return 0;
        return mFolderListHaspMap.get(mFolderNames.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mFolderNames.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return  mFolderListHaspMap.get(mFolderNames.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group_title = (String) getGroup(groupPosition);
        int lastIndexOf = group_title.lastIndexOf("/");
        String lastPartOfTitle= group_title.substring(lastIndexOf+1);
        int videoCount;
        videoCount=getChildrenCount(groupPosition);

        //lastPartOfTitle=lastPartOfTitle + "      " + videoCount;
        if (convertView == null){
            LayoutInflater layoutInflater= (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.
                    inflate(R.layout.tab_folderlist_parent, parent, false);
        }

        TextView parent_txtView= (TextView) convertView.findViewById(R.id.parent_txt);
        TextView folderVideoCount = (TextView) convertView.findViewById(R.id.videoCount);
        TextView folderPath_txtView = (TextView)convertView.findViewById(R.id.folder_path);

        parent_txtView.setText(lastPartOfTitle);
        folderVideoCount.setText(videoCount + "");
        String folderPath =  group_title.substring(0,lastIndexOf + 1);
        folderPath_txtView.setText(folderPath);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String videoFullPath = (String)getChild(groupPosition, childPosition);
        final ViewHolder viewHolder;
        if(convertView == null)
        {
            LayoutInflater layoutInflater= (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= layoutInflater.inflate(R.layout.tab_child,parent,false);
            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) convertView.findViewById(R.id.VideoTitleNew);
            viewHolder.resolution = (TextView) convertView.findViewById(R.id.VideoResolutionNew);
            viewHolder.size = (TextView) convertView.findViewById(R.id.VideoSizeNew);
            viewHolder.thumbnail =(CustomImageView) convertView.findViewById(R.id.VideoThumbnailNew);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.VideoDurationNew);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.title.setText(mVideoListInfo.getVideoTitleHashMap().get(videoFullPath));
        String SizeInMb = Converters.BytesToMb(mVideoListInfo.getVideoSizeHashMap().get(videoFullPath).toString());
        viewHolder.size.setText(SizeInMb);

        int width;
        int height;
        width = mVideoListInfo.getVideoWidthHashMap().get(videoFullPath);
        height = mVideoListInfo.getVideoHeightHashMap().get(videoFullPath);
        viewHolder.resolution.setText(width + "x" + height);


        long durationOfVideoLong = mVideoListInfo.getVideoDurationHashMap().get(videoFullPath);
        viewHolder.duration.setText(String.format("%d:%d:%d", TimeUnit.MILLISECONDS.toHours((long) durationOfVideoLong), TimeUnit.MILLISECONDS.toMinutes((long) durationOfVideoLong) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) durationOfVideoLong)),
                TimeUnit.MILLISECONDS.toSeconds((long) durationOfVideoLong) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) durationOfVideoLong))));


        long videoid = mVideoListInfo.getVideoIdHashMap().get(videoFullPath);
        Bitmap found = BitmapCache.GetInstance().GetBitmapFromMemoryCache(videoFullPath);
        if (found != null)
            viewHolder.thumbnail.setImageBitmap(found);
        else {
            if (ThumbnailCreateor.cancelPotentialWork(videoid, viewHolder.thumbnail)) {
                ThumbnailCreateor.BitmapWorkerTask task = new ThumbnailCreateor.BitmapWorkerTask(viewHolder.thumbnail, mContext.getContentResolver(), videoFullPath);

                ThumbnailCreateor.AsyncDrawable downloadedDrawable = new ThumbnailCreateor.AsyncDrawable(mContext.getResources(), bmp ,task);
                viewHolder.thumbnail.setImageDrawable(downloadedDrawable);
                task.execute(String.valueOf(videoid), videoFullPath);

            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolder {
        CustomImageView thumbnail;
        TextView title;
        TextView resolution;
        TextView size;
        TextView duration;

    }

}
