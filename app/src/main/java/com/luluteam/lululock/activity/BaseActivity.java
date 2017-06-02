package com.luluteam.lululock.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import java.io.Serializable;

/**
 * Created by guan on 5/26/17.
 */

public class BaseActivity extends Activity implements Serializable {


    private CustomBuilder customBuilder = new CustomBuilder();

    protected String TAG = this.getClass().getSimpleName();
    protected Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;

    }

    protected CustomBuilder getCustomBuilder() {
        return customBuilder;
    }

    /**
     * 根据CustomBuilder中的设置项，开始设置Activity
     */
    private void customActivity() {
        if (customBuilder.mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (customBuilder.isSetStatusBar) {
            steepStatusBar();
        }
        if (!customBuilder.isAllowScreenRoate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 开始设置：[沉浸状态栏]
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    /**
     * 内部类：用于标识Activity的设置项
     */
    public final class CustomBuilder {
        /**
         * 是否沉浸状态栏
         **/
        private boolean isSetStatusBar = true;
        /**
         * 是否允许全屏
         **/
        private boolean mAllowFullScreen = true;
        /**
         * 是否允许旋转屏幕
         **/
        private boolean isAllowScreenRoate = false;

        /**
         * [是否允许全屏]
         *
         * @param allowFullScreen
         */
        public CustomBuilder setAllowFullScreen(boolean allowFullScreen) {
            this.mAllowFullScreen = allowFullScreen;
            return this;
        }

        /**
         * [是否设置沉浸状态栏]
         *
         * @param isSetStatusBar
         */
        public CustomBuilder setSteepStatusBar(boolean isSetStatusBar) {
            this.isSetStatusBar = isSetStatusBar;
            return this;
        }

        /**
         * [是否允许屏幕旋转]
         *
         * @param isAllowScreenRoate
         */
        public CustomBuilder setScreenRoate(boolean isAllowScreenRoate) {
            this.isAllowScreenRoate = isAllowScreenRoate;
            return this;
        }


        public void build() {
            customActivity();
        }


    }
}
