package com.example.datn.activity.main;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.datn.R;
import com.example.datn.helpers.DownloadWorker;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.net.URLEncoder;

public class ViewDocumentActivity extends AppCompatActivity {
    private WebView pdfview;
    private FloatingActionButton btnDownload, btnShare;
    private ProgressBar progressBar;
    private long downloadId;
    private BroadcastReceiver onDownloadComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        pdfview = findViewById(R.id.viewPdf);
        btnDownload = findViewById(R.id.downloadButton);
        btnShare = findViewById(R.id.shareButton);
        progressBar = findViewById(R.id.progressBar);

        initializeWebView();
        setupDownloadButton();
        setupShareButton();
        registerDownloadReceiver();
    }

    private void initializeWebView() {
        pdfview.getSettings().setJavaScriptEnabled(true);

        String filename = getIntent().getStringExtra("title");
        String fileUrl = getIntent().getStringExtra("pdf");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.progress_layout, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView txtTitle = dialogView.findViewById(R.id.txtTitle);
        txtTitle.setText("Đang mở tài liệu: " + filename);
        dialog.show();

        pdfview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }
        });

        loadPdfFile(fileUrl);
    }

    private void loadPdfFile(String fileUrl) {
        try {
            String encodedUrl = URLEncoder.encode(fileUrl, "UTF-8");
            pdfview.loadUrl("https://docs.google.com/gview?embedded=true&url=" + encodedUrl);
        } catch (Exception ex) {
            Log.e("ViewDocumentActivity", "Error loading PDF", ex);
            Toast.makeText(this, "Lỗi tải tài liệu", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDownloadButton() {
        btnDownload.setOnClickListener(v -> {
            String filename = getIntent().getStringExtra("title");
            String fileUrl = getIntent().getStringExtra("pdf");

            // Cung cấp dữ liệu cho Worker
            Data inputData = new Data.Builder()
                    .putString("pdf_url", fileUrl)
                    .putString("filename", filename)
                    .build();

            OneTimeWorkRequest downloadWorkRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                    .setInputData(inputData)
                    .build();

            // Bắt đầu công việc tải xuống
            WorkManager.getInstance(this).enqueue(downloadWorkRequest);

            Toast.makeText(ViewDocumentActivity.this, "Đang tải tài liệu...", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupShareButton() {
        btnShare.setOnClickListener(v -> {
            String filename = getIntent().getStringExtra("title");
            String fileUrl = getIntent().getStringExtra("pdf");
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, fileUrl);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, filename);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ File PDF"));
        });
    }

    private void registerDownloadReceiver() {
        onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    // Tắt ProgressBar khi tải xong
                    progressBar.setVisibility(View.GONE);

                    // Thay đổi thông báo từ Snackbar sang Toast
                    Toast.makeText(ViewDocumentActivity.this, "Tải xuống hoàn tất. Tài liệu lưu trong thư mục Download.", Toast.LENGTH_LONG).show();
                }
            }
        };

    }

    private void downloadFile(Context context, String pdfLink, String fileName) {
        try {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(pdfLink);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(fileName)
                    .setMimeType("application/pdf")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Android 10 trở lên luôn lưu vào Download
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            downloadId = downloadManager.enqueue(request);

//            progressBar.setVisibility(View.VISIBLE);  // Hiển thị ProgressBar khi bắt đầu tải
            Toast.makeText(context, "Đang tải tài liệu...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải xuống PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ViewDocumentActivity", "Download error", e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
