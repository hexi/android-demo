package com.baidao.downloadapk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void download(View view) {
        Intent intent = ApkDownloadService.getDownloadIntent(this, ApkDownloadService.URL_YUE_GUI_BAO);
        startService(intent);
    }
}
