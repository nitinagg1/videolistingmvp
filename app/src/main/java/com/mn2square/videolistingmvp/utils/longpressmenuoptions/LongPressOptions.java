package com.mn2square.videolistingmvp.utils.longpressmenuoptions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mn2square.videolistingmvp.R;

public class LongPressOptions {
    public static interface OnConfirmRenameListener {
        void onConfirm(String filename);
    }

    public static void deleteFile(final Context context, final String selectedVideoDelete, final int id,
                                  final View.OnClickListener onClickListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Confirm Delete...");
        alertDialog.setMessage("Are you sure you want to Delete:\n\n" + selectedVideoDelete);
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onClick(null);
            }
        });
        alertDialog.show();
    }

    public static void renameFile(final Context context, final String selectedVideoTitleForRename, final String selectedVideoRenamePath,
                                  final String extensionValue, final int id,
                                  final OnConfirmRenameListener onConfirmRenameListener
    ) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater li = LayoutInflater.from(context);
        View renameVideoView = li.inflate(R.layout.rename_video, null);
        final EditText input = (EditText)renameVideoView.findViewById(R.id.rename_edit_text);
        input.setText(selectedVideoTitleForRename);

        alert.setView(renameVideoView);
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onConfirmRenameListener.onConfirm(input.getText().toString());
            }
        });

        alert.show();

    }

    public static void shareFile(final Context context, final String selectedVideoShare) {
        MediaScannerConnection.scanFile(context, new String[] { selectedVideoShare },

                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra("VSMP", "https://play.google.com/store/apps/details?id=" + context.getPackageName());
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(Intent.createChooser(shareIntent,
                                context.getString(R.string.share_text)));

                    }
                });

    }

}
