package com.vvautotest.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.vvautotest.R;
import com.vvautotest.activities.HomeActivity;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;


public class UploadVideoService extends Service {

    private Handler h;
    private Runnable r;
    NotificationManager manager;
    NotificationCompat.Builder builder;
    private boolean firstTime = true;
    private boolean isCompleted = false;

    public static String ACTION_CANCEL = "actionCancelAlarm";

    public final int Notification_Id = 501;

    int counter = 0;
    String tag = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification updateNotification(String percentage, boolean isVibrate) {

        String info = percentage + "";
        Context context = getApplicationContext();

        if (firstTime)
        {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            createNotificationChannel(manager, isVibrate);
            PendingIntent action = PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), PendingIntent.FLAG_IMMUTABLE);
            Intent cancelIntent = new Intent(this, CancelAlarmReceiver.class);
            cancelIntent.setAction(ACTION_CANCEL);
            PendingIntent cancelPendingIntent =
                    PendingIntent.getBroadcast(this, Notification_Id,
                            cancelIntent, PendingIntent.FLAG_IMMUTABLE);

            builder.setContentIntent(action)
                    .setContentTitle("Upload Video File")
                    .setOnlyAlertOnce(true)
                    .setTicker(info)
                    // .setVibrate(isVibrate ? value : new long[] {0})
                    .setContentText(info)
                    .setSmallIcon(R.drawable.notification)
                    .addAction(R.drawable.logo, "Cancel", cancelPendingIntent)
                    //   .setOngoing(true)
                    .build();

            firstTime = false;
        } if(isCompleted)
        {
            PendingIntent action = PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(action)
                    .setContentTitle("Upload Video File")
                    .setOnlyAlertOnce(true)
                    .setTicker(info)
                    // .setVibrate(isVibrate ? value : new long[] {0})
                    .setContentText(info)
                    .setSmallIcon(R.drawable.notification)
                    //   .setOngoing(true)
                    .build();
        }else
        {
            builder.setContentTitle("Upload Video File")
                   .setContentText(info);
        }

        Notification notification = builder.build();
        // long notificationCode = System.currentTimeMillis(); // будет генерироваться отдельная строка для каждого уведомления
        long notificationCode = Notification_Id; // решил обновлять одно и то же уведомление
        manager.notify((int) notificationCode, notification);

        return notification;
    }

    // cоздание канала уведомлений
    private static final String CHANNEL_ID = "upload_channel";

    private void createNotificationChannel(NotificationManager manager, boolean isVibrate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "VideoUploadChannel", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("My notification channel description");
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try
        {
            String data = intent.getStringExtra("data");
            if (intent.getAction().contains("start")) {
                uploadVideo(data);
            } else {
                stopForeground(true);
                stopSelf();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            stopSelf();
        }


        return Service.START_STICKY;
    }

    private void uploadVideo(String requestjson){
        try
        {
            startForeground(Notification_Id, updateNotification("Video Uploading Started", false));
            JSONObject j = new JSONObject(requestjson);
            String FileData = j.getString("FileData");
            String FileName = j.getString("FileName");
            tag = FileName;
            String FileExt = j.getString("FileExt");
            String CreatedByUserID = j.getString("CreatedByUserID");
            String FolderPath = j.getString("FolderPath");
            AndroidNetworking.upload(ServerConfig.PDF_Upload_Video_URL)
                    .addMultipartFile("FileData",new File(FileData))
                    .addMultipartParameter("FolderPath",FolderPath)
                    .addMultipartParameter("FileName",FileName)
                    .addMultipartParameter("FileExt", FileExt)
                    .addMultipartParameter("CreatedByUserID", CreatedByUserID)
                    .setTag(FileName)
                    .setPriority(Priority.HIGH)
                    .setExecutor(Executors.newSingleThreadExecutor())
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            int percent = (int)(100.0*(double)bytesUploaded/totalBytes + 0.5);
                            startForeground(Notification_Id, updateNotification("Uploading " + AppUtils.bytesIntoHumanReadable(bytesUploaded)
                                    + "/" + AppUtils.bytesIntoHumanReadable(totalBytes) + " " + FileName+ "." + FileExt, false));
                        //    mDialog.setProgress(percent);
                          /*  mDialog.setMessage("Uploading " + AppUtils.bytesIntoHumanReadable(bytesUploaded)
                            + "/" + AppUtils.bytesIntoHumanReadable(bytesUploaded) + " " + finalfileName+ "." + fileExtention);
                */            //    L.printError("Progress : " + bytesUploaded);
                            // do anything with progress
                        }
                    })
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                //            L.printError("");
//                            Toast.makeText(context, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
//                            getGalleryFolders();
                            isCompleted = true;
                            startForeground(Notification_Id, updateNotification("File Uploaded Successfully", false));
                            L.printError("Response : " + response);
                            stopSelf();
                        }

                        @Override
                        public void onError(ANError anError) {
                            stopSelf();
                        //    L.printError(anError.getErrorBody());
//                            Toast.makeText(context, "Unable to upload file", Toast.LENGTH_SHORT).show();
                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public class CancelAlarmReceiver extends BroadcastReceiver {

        public CancelAlarmReceiver() {}

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CANCEL)) {
                try {
                    L.printError("Action Cancel Clicked");
                    AndroidNetworking.forceCancel(tag);
                    stopSelf();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}