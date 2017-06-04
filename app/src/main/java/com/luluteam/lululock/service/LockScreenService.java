package com.luluteam.lululock.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.luluteam.lululock.R;
import com.luluteam.lululock.app.App;
import com.luluteam.lululock.utils.floatwindow.FloatWindowSimpleService;
import com.luluteam.lululock.utils.mqtt.MQTTService;

import java.util.Timer;
import java.util.TimerTask;

public class LockScreenService extends Service {

    private NotificationCompat.Builder mBuilder;
    private Notification notification;
    private int ID_LLLOCK = 63889;
    private Timer timer = null;
    private boolean isTimerRunning =false;

    String TAG = "LockScreenService";

    public LockScreenService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification();
        if (!isTimerRunning)
        {
            startProtect();
        }
        startForeground(ID_LLLOCK, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isTimerRunning=false;


    }

    private void createNotification() {
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_app_launcher);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_launcher));
            mBuilder.setContentTitle("LL锁屏");//设置标题
            //mBuilder.setContentText("正在运行……");//设置详细内容
            mBuilder.setTicker("LL锁屏：正在运行……");//首先弹出来的，用于提醒的一行小字
            mBuilder.setWhen(System.currentTimeMillis());//设置时间
            //mBuilder.setProgress(0, 0, false);//设置进度条
        }

        notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;//不自动清除
        //notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;//前台服务标记
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

    }

    private void startProtect() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new ProtectTask(), 10000, 2000);
        isTimerRunning =true;
    }

    private class ProtectTask extends TimerTask {

        @Override
        public void run() {
            if (!App.isServiceWorked(MQTTService.class.getName())) {
                Log.e(TAG, "MQTTService is not running. try to run again");
                mBuilder.setContentText("MQTTService is not running");
                startForeground(ID_LLLOCK, mBuilder.build());
                Intent intent = new Intent(LockScreenService.this, MQTTService.class);
                startService(intent);
            } else if (!App.isServiceWorked(FloatWindowSimpleService.class.getName())) {
                Log.e(TAG, "FloatWindowSimpleService is not running. try to run again");
                mBuilder.setContentText("FloatWindowSimpleService is not running");
                startForeground(ID_LLLOCK, mBuilder.build());
                Intent intent = new Intent(LockScreenService.this, FloatWindowSimpleService.class);
                startService(intent);
            } else {
                mBuilder.setContentText("All Services are running");
                startForeground(ID_LLLOCK, mBuilder.build());
            }

        }
    }


}
