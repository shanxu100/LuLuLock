package com.luluteam.lululock.activity;

import android.content.Intent;
import android.os.Bundle;

import com.luluteam.lululock.R;
import com.luluteam.lululock.service.LockScreenService;
import com.luluteam.lululock.utils.floatwindow.FloatWinPermissionActivity;
import com.luluteam.lululock.utils.lock_screen.DeviceManager;

public class MainActivity extends BaseActivity {

    //public static String publishTopic=""

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        init();
        finish();

    }

    private void init()
    {
        //申请悬浮窗的权限,申请成功之后开启悬浮窗
        Intent floatWinIntent=new Intent(this,FloatWinPermissionActivity.class);
        startActivity(floatWinIntent);

        //申请管理员的角色，执行锁屏操作
        DeviceManager.getInstance().activeAdmin(this);

        //开启守护进程
        Intent lockScreenIntent=new Intent(this, LockScreenService.class);
        startService(lockScreenIntent);
    }
}
