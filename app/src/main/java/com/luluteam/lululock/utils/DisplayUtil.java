package com.luluteam.lululock.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by guan on 5/19/17.
 */

public class DisplayUtil {

    private static int SCREENWIDTH;
    private static int SCREENHEIGHT;
    private static int DensityDpi;


    private static String TAG = "DisplayUtil";

    public static int getScreenWidth() {
        return SCREENWIDTH;
    }

    public static int getScreenHeight() {
        return SCREENHEIGHT;
    }

    public static int getDensityDpi() {
        return DensityDpi;
    }


    //=================================================

    /**
     * 获取并保存屏幕大小
     *
     * @param mContext
     */
    public static void saveScreenInfo(Context mContext) {

        if (SCREENWIDTH != 0 && SCREENHEIGHT != 0) {
            return;
        }

        //通过Application获取屏幕信息
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        //通过Activity获取屏幕信息
        //((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metric);
        SCREENWIDTH = metric.widthPixels;     // 屏幕宽度（像素）
        SCREENHEIGHT = metric.heightPixels;   // 屏幕高度（像素）
        DensityDpi = metric.densityDpi;

        Log.d(TAG, "SCREENWIDTH:\t" + SCREENWIDTH +
                "\tSCREENHEIGHT:\t" + SCREENHEIGHT +
                "\tDensityDpi:\t" + DensityDpi);
    }
}
