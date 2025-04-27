package com.example.datn.helpers;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.IOException;

public class DownloadWorker extends Worker {

    private static final String CHANNEL_ID = "download_channel";

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String fileUrl = getInputData().getString("pdf_url");
        String filename = getInputData().getString("filename");

        if (fileUrl != null && filename != null) {
            try {
                downloadFile(getApplicationContext(), fileUrl, filename);
                return Result.success();
            } catch (IOException e) {
                Log.e("DownloadWorker", "Download error", e);
                return Result.failure();
            }
        }

        return Result.failure();
    }

    private void downloadFile(Context context, String fileUrl, String filename) throws IOException {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(fileUrl);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("application/pdf")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Android 10 trở lên sẽ lưu vào thư mục Download mặc định
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

            long downloadId = downloadManager.enqueue(request);

            // Tạo NotificationChannel nếu là Android 8 trở lên
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Download Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Tạo và hiển thị Notification khi tải xong
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Download Complete")
                    .setContentText("Tải tài liệu hoàn tất: " + filename)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.notify(1, notification);
            Toast.makeText(context, "Tải xuống hoàn tất. Tài liệu lưu trong thư mục Download.", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("DownloadWorker", "Download error", e);
            throw new IOException("Download failed", e);
        }
    }
}