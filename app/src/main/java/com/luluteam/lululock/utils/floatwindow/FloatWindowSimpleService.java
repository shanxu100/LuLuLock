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
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    /**
     * 误差
     */
    private float error = 10;


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


        lock_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();

                if (action == MotionEvent.ACTION_DOWN) {

                    // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                    xInView = motionEvent.getX();
                    yInView = motionEvent.getY();
                    xDownInScreen = motionEvent.getRawX();
                    yDownInScreen = motionEvent.getRawY() - getStatusBarHeight();
                    xInScreen = motionEvent.getRawX();
                    yInScreen = motionEvent.getRawY() - getStatusBarHeight();

                } else if (action == MotionEvent.ACTION_MOVE) {
                    xInScreen = motionEvent.getRawX();
                    yInScreen = motionEvent.getRawY() - getStatusBarHeight();
                    // 手指移动的时候更新小悬浮窗的位置
                    params.x = (int) (xInScreen - xInView);
                    params.y = (int) (yInScreen - yInView);
                    windowManager.updateViewLayout(simple_rl_view, params);

                } else if (action == MotionEvent.ACTION_UP) {
                    // 如果手指离开屏幕时，xDownInScreen和xInScreen相等(或在误差允许范围内)，
                    // 且yDownInScreen和yInScreen相等(或在误差允许范围内)，则视为触发了单击事件。
                    if (Math.abs(xInScreen - xDownInScreen) <= error
                            && Math.abs(yInScreen - yDownInScreen) <= error) {
                        doClick();
                    }
                }

                return true;
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
                    if (button == G_AlertDialog.ClickedButton.POSITIVE) {
                        MessageSender.sendActionLockScreen("TOPIC_LOCK_SCREEN", true);
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
                    if (button == G_AlertDialog.ClickedButton.POSITIVE) {
                        MessageSender.sendActionLockScreen("TOPIC_LOCK_SCREEN", false);
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
