package com.luluteam.lululock.utils.floatwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.luluteam.lululock.R;
import com.luluteam.lululock.app.App;
import com.luluteam.lululock.utils.DisplayUtil;
import com.luluteam.lululock.utils.mqtt.MessageSender;
import com.luluteam.lululock.view.G_AlertDialog;

import java.lang.reflect.Field;


/**
 * 这是一个简易的悬浮窗Service，只包含一个小窗口
 */
public class FloatWindowSimpleService extends Service {

    private RelativeLayout simple_rl_view;//View的子类
    //private LinearLayout parent_ll;
    private Button lock_btn;
    //private Button unlock_btn;
    private WindowManager windowManager;

    /**
     * 记录系统状态栏的高度
     */
    private int statusBarHeight;

    private enum flagType {Locked, UnLocked}

    ;

    private flagType flag = flagType.UnLocked;


    public FloatWindowSimpleService() {
    }

    @Override
    public void onCreate() {
        initWindowManager(App.getAppContext());

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createFloatView();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        windowManager.removeView(simple_rl_view);
        simple_rl_view = null;

        super.onDestroy();
    }

    private void createFloatView() {

        if (isFloatWindowShowing()) {
            return;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams
                (WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_8888);
        params.x = 0;
        params.y = DisplayUtil.getScreenHeight() / 2;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        simple_rl_view = (RelativeLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_floatwindow_small, null);
        lock_btn = (Button) simple_rl_view.findViewById(R.id.lock_screen_btn);
        windowManager.addView(simple_rl_view, params);

        lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doClick();
            }
        });

        lock_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                params.x = (int) (motionEvent.getRawX() - lock_btn.getMeasuredWidth() / 2);
                params.y = (int) (motionEvent.getRawY() - lock_btn.getMeasuredHeight() / 2 - getStatusBarHeight());
                windowManager.updateViewLayout(simple_rl_view, params);
                return false;
            }
        });
    }


    /**
     * @param context 必须为应用程序的Context.
     */
    private void initWindowManager(Context context) {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    private boolean isFloatWindowShowing() {
        return simple_rl_view != null;
    }


    private void doClick() {
        if (flag == flagType.UnLocked) {
            //执行锁屏操作
            final G_AlertDialog alertDialog = new G_AlertDialog(App.getAppContext());
            alertDialog.setMeesage("您确定要锁屏吗？");
            alertDialog.setCallback(new G_AlertDialog.YesOrNoDialogCallback() {
                @Override
                public void onClickButton(G_AlertDialog.ClickedButton button, String message) {
                    if (button== G_AlertDialog.ClickedButton.POSITIVE)
                    {
                        MessageSender.sendActionLockScreen("TOPIC_LOCK_SCREEN",true);
                        lock_btn.setText("UNLOCK");
                        flag = flagType.Locked;

                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();

        } else if (flag == flagType.Locked) {
            //执行解锁操作
            final G_AlertDialog alertDialog = new G_AlertDialog(App.getAppContext());
            alertDialog.setMeesage("您确定解锁吗？");
            alertDialog.setCallback(new G_AlertDialog.YesOrNoDialogCallback() {
                @Override
                public void onClickButton(G_AlertDialog.ClickedButton button, String message) {
                    if (button== G_AlertDialog.ClickedButton.POSITIVE)
                    {
                        MessageSender.sendActionLockScreen("TOPIC_LOCK_SCREEN",false);
                        lock_btn.setText("LOCK");
                        flag = flagType.UnLocked;

                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();


        }
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

}
