package com.baidao.downloadapk;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;

public class ApkDownloadService extends Service {
    private static final String TAG = "DownloadService";
    private static final String ACTION = "action";
    private static final String APK_URL = "apk_url";

    private static final int ACTION_UNKNOWN = -1;
    private static final int ACTION_DOWNLOAD = 0;
    private Long taskId;
    private DownloadManager downloadManager;
    public static String URL_YUE_GUI_BAO = "http://www.95049.net/ruanjian/dstrader.apk";
    private String apkName;

    public ApkDownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Intent getDownloadIntent(Context context, String apkUrl) {
        Intent intent = new Intent(context, ApkDownloadService.class);
        intent.putExtra(ACTION, ACTION_DOWNLOAD);
        intent.putExtra(APK_URL, apkUrl);
        return intent;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int action = intent.getIntExtra(ACTION, ACTION_UNKNOWN);
            if (action == ACTION_DOWNLOAD) {
                String apkUrl = intent.getStringExtra(APK_URL);
                if (!TextUtils.isEmpty(apkUrl)) {
                    String[] arr = apkUrl.split("/");
                    apkName = arr[arr.length - 1];
                    downloadAPK(apkUrl, apkName);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //使用系统下载器下载
    private void downloadAPK(String apkUrl, String apkDir) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setAllowedOverRoaming(true);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString =
                mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(apkUrl));
        request.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkDir);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        taskId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Toast.makeText(getApplicationContext(), "正在通知栏下载中...", Toast.LENGTH_SHORT).show();
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(taskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.i(TAG, ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.i(TAG, ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.i(TAG, ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i(TAG, ">>>下载完成");
                    //下载完成安装APK
                    String downloadPath = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator
                            + apkName;
                    installAPK(new File(downloadPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.e(TAG, ">>>下载失败");
                    stopSelf();

                    break;
            }
        }
    }

    //下载到本地后执行安装
    protected void installAPK(File file) {
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag,后面解释
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===destroyed===");
        unregisterReceiver(receiver);
    }
}
