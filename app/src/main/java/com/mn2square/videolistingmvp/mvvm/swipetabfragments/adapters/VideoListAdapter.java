package com.mn2square.videolistingmvp.mvvm.swipetabfragments.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mn2square.videolistingmvp.R;
import com.mn2square.videolistingmvp.mvvm.pojo.VideoListInfo;
import com.mn2square.videolistingmvp.utils.Converters;
import com.mn2square.videolistingmvp.utils.thumbnailutils.CustomImageView;
import com.mn2square.videolistingmvp.utils.thumbnailutils.ThumbnailCreateor;
import com.mn2square.videolistingmvp.utils.thumbnailutils.BitmapCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nitinagarwal on 3/12/17.
 */

public class VideoListAdapter extends ArrayAdapter<String> {


    private Context mContext;
    private int mVideoListingLayout;
    Bitmap bmp;
    private List<String> videos;
    private VideoListInfo mVideoListInfo;

    public VideoListAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mVideoListingLayout = resource;
    }

    @Override
    public int getCount() {
        if(videos != null)
            return videos.size();
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String videoFullPath = (String) videos.get(position);
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(mVideoListingLayout, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.VideoTitleNew);
            viewHolder.resolution = (TextView) convertView.findViewById(R.id.VideoResolutionNew);
            viewHolder.size = (TextView) convertView.findViewById(R.id.VideoSizeNew);
            viewHolder.thumbnail =(CustomImageView) convertView.findViewById(R.id.VideoThumbnailNew);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.VideoDurationNew);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(videoFullPath);

        String titleValue = mVideoListInfo.getVideoTitleHashMap().get(videoFullPath);
        int durationValue = mVideoListInfo.getVideoDurationHashMap().get(videoFullPath);
        int sizeValue = mVideoListInfo.getVideoSizeHashMap().get(videoFullPath);
        int widthValue = mVideoListInfo.getVideoWidthHashMap().get(videoFullPath);
        int heightValue = mVideoListInfo.getVideoHeightHashMap().get(videoFullPath);
        String size = Converters.BytesToMb(String.valueOf(sizeValue)) ;
        String resolution = widthValue + "X" + heightValue;
        viewHolder.title.setText(titleValue);
        viewHolder.size.setText(size);
        viewHolder.duration.setText(String.format("%d:%d:%d", TimeUnit.MILLISECONDS.toHours((long) durationValue), TimeUnit.MILLISECONDS.toMinutes((long) durationValue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) durationValue)),
                TimeUnit.MILLISECONDS.toSeconds((long) durationValue) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) durationValue))));
        viewHolder.resolution.setText(resolution);

        long videoid = mVideoListInfo.getVideoIdHashMap().get(videoFullPath);
        Bitmap found = BitmapCache.GetInstance().GetBitmapFromMemoryCache(videoFullPath);
        if (found != null)
            viewHolder.thumbnail.setImageBitmap(found);
        else {
            if (ThumbnailCreateor.cancelPotentialWork(videoid, viewHolder.thumbnail)) {
                ThumbnailCreateor.BitmapWorkerTask task = new ThumbnailCreateor.BitmapWorkerTask(viewHolder.thumbnail, mContext.getContentResolver(), videoFullPath);

                ThumbnailCreateor.AsyncDrawable downloadedDrawable = new ThumbnailCreateor.AsyncDrawable(mContext.getResources(), bmp ,task);
                viewHolder.thumbnail.setImageDrawable(downloadedDrawable);
                task.execute(String.valueOf(videoid),videoFullPath);

            }
        }
        return convertView;
    }

    public void bindVideoList(List<String> videoList, VideoListInfo videoListInfo) {
        videos = videoList;
        mVideoListInfo = videoListInfo;
    }

    private class ViewHolder {
        CustomImageView thumbnail;
        TextView title;
        TextView resolution;
        TextView size;
        TextView duration;

    }
}