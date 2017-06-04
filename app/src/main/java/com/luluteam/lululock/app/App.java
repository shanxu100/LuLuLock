package com.luluteam.lululock.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.luluteam.lululock.utils.DisplayUtil;
import com.luluteam.lululock.utils.mqtt.MQTTService;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by guan on 6/1/17.
 */

public class App extends Application {
    private String TAG = "G_App";
    private static Context appContext;

    public static final String uuid=UUID.randomUUID().toString();

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }

    private void initApp()
    {
        appContext=this;

        /**
         * 保存屏幕尺寸
         */
        DisplayUtil.saveScreenInfo(getApplicationContext());

        /**
         * 开启Mqtt连接
         */
        Intent mqttIntent=new Intent(appContext, MQTTService.class);
        startService(mqttIntent);


    }

    public static Context getAppContext() {
        return appContext;
    }

    /**
     * 判断Service是否在运行
     * @param serviceName
     * @return
     */
    public static boolean isServiceWorked( String serviceName) {
        ActivityManager myManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

}
