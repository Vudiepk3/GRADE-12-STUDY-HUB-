package com.example.datn.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.datn.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.net.URLEncoder;

public class ViewDocumentActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    WebView pdfview;
    FloatingActionButton btnDownload,btnShare;
    ProgressBar progressBar;
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

    // Khởi tạo WebView để hiển thị PDF từ URL
    @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
    private void initializeWebView() {
        pdfview.getSettings().setJavaScriptEnabled(true);

        String filename = getIntent().getStringExtra("title");
        String fileUrl = getIntent().getStringExtra("pdf");

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewDocumentActivity.this);
        builder.setCancelable(false);
        // Inflate layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress_layout, null);
        builder.setView(dialogView); // Đặt layout cho dialog
        AlertDialog dialog = builder.create(); // Tạo dialog // Lấy TextView từ layout và thiết lập nội dung của nó
        TextView txtTitle = dialogView.findViewById(R.id.txtTitle);
        txtTitle.setText("Đang mở tài liệu: "+ filename);
        dialog.show();

        pdfview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dialog.dismiss();
            }
        });

        pdfview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.e("WebView", consoleMessage.message());
                return true;
            }
        });

        loadPdfFile(fileUrl);
    }

    // Load file PDF từ URL vào WebView sử dụng Google Docs Viewer
    private void loadPdfFile(String fileUrl) {
        try {
            String encodedUrl = URLEncoder.encode(fileUrl, "UTF-8");
            pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + encodedUrl);
        } catch (Exception ex) {
            Log.e("LoadPdFile","Error");
            Toast.makeText(this, "Lỗi tải tài liệu", Toast.LENGTH_SHORT).show();
        }
    }

    // Thiết lập nút Download để tải xuống PDF
    public void setupDownloadButton() {
        btnDownload.setOnClickListener(v -> {
            String filename = getIntent().getStringExtra("title");
            String fileUrl = getIntent().getStringExtra("pdf");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                downloadFile(this, fileUrl, filename);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    public void setupShareButton() {
        btnShare.setOnClickListener(v ->{
            String filename = getIntent().getStringExtra("title");
            String fileUrl = getIntent().getStringExtra("pdf");
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, fileUrl);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, filename);
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ File PDF"));
        });
    }

    // Đăng ký BroadcastReceiver để nhận thông báo khi tải xuống hoàn tất
    @SuppressLint("NewApi")
    private void registerDownloadReceiver() {
        onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    progressBar.setVisibility(android.view.View.GONE); // Ẩn progressBar

                    // Lấy đường dẫn đến file đã tải xuống
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    Uri downloadUri = downloadManager.getUriForDownloadedFile(downloadId);
                    if (downloadUri != null) {
                        // Hiển thị Snackbar "Download completed" trước
                        Snackbar.make(findViewById(android.R.id.content), "Hoàn Thành Tải Xuống.Tài Liệu Được Lưu Thư Mục FileGrade12 (Bộ nhớ Điện Thoại) hoặc Mục Tài Liệu", Snackbar.LENGTH_LONG)
                                .addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                        super.onDismissed(snackbar, event);
                                        // Sau khi Snackbar biến mất, hiển thị Toast với đường dẫn đến file

                                    }
                                })
                                .show();
                    }
                }
            }
        };

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
    }

    // Phương thức để tải xuống file PDF
    private void downloadFile(Context context, String pdfLink, String fileName) {
        try {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(pdfLink);
            // Tạo thư mục "FileGrade12" trong bộ nhớ ngoài (thay vì thư mục Downloads)
            File directory = new File(Environment.getExternalStorageDirectory(), "FileGrade12");
            if (!directory.exists()) {
                boolean isDirectoryCreated = directory.mkdirs(); // Tạo thư mục và kiểm tra kết quả
                if (isDirectoryCreated) {
                    Log.d("ViewPDFActivity", "Thư mục được tạo:: " + directory.getAbsolutePath());
                } else {
                    Log.e("ViewPDFActivity", "Tạo thư mục không thành công: " + directory.getAbsolutePath());
                    //Toast.makeText(context, "Error creating directory.", Toast.LENGTH_SHORT).show();
                    return; // Ngưng thực hiện nếu không thể tạo thư mục
                }
            }

            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(fileName)
                    .setMimeType("application/pdf")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                            "FileGrade12", // Tạo thư mục trực tiếp trong bộ nhớ ngoài
                            File.separator + fileName
                    );

            downloadId = downloadManager.enqueue(request); // Lưu ID tải xuống

            progressBar.setVisibility(android.view.View.VISIBLE); // Hiển thị progressBar
            Toast.makeText(context, "Đang Tải Dữ Liệu...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải xuống PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý khi người dùng trả lời yêu cầu cấp quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, bắt đầu tải xuống file
                String filename = getIntent().getStringExtra("title");
                String fileurl = getIntent().getStringExtra("pdf");
                downloadFile(this, fileurl, filename);
            } else {
                // Quyền bị từ chối, hiển thị thông báo cho người dùng
                Toast.makeText(this, "Quyền bị từ chối.Không thể tải xuống file.Hãy cấp quyền vào cài đặt", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hủy đăng ký BroadcastReceiver khi Activity bị hủy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }
}