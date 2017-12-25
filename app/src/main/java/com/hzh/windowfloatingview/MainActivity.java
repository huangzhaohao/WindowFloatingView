package com.hzh.windowfloatingview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.hzh.windowfloatingview.floatwindowpermission.FloatWindowUtils;

import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;

    private float mStartX, mStartY;
    private long mDownTime, mUpTime;
    private View mView;
    private boolean isFloatViewNotAdded = true;
    private int mCanMoveHeight;
    private int mCanMoveWidth;
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("ACTION_ADD_FLOATVIEW")){
                    showFloatView(MainActivity.this);
                }else if (intent.getAction().equals("ACTION_REMOVE_FLOATVIEW")){
                    removeFloatView(MainActivity.this);
                }
            }
        };
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_ADD_FLOATVIEW");
        filter.addAction("ACTION_REMOVE_FLOATVIEW");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver,filter);
        initView();
    }

    private void initView() {
        wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mView = LayoutInflater.from(this).inflate(R.layout.kefu, null);
        wmParams = new WindowManager.LayoutParams(
                DensityUtil.dp2px(this,52),
                DensityUtil.dp2px(this,52),
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );
        if(Build.VERSION.SDK_INT > 24) {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mCanMoveWidth = getResources().getDisplayMetrics().widthPixels / 2 - DensityUtil.dip2px(MainActivity.this, 26);
        mCanMoveHeight = (getResources().getDisplayMetrics().heightPixels - StatusBarCompat.getStatusBarHeight(this)) / 2 - DensityUtil.dip2px(MainActivity.this, 26);
        wmParams.x = mCanMoveWidth;
        wmParams.y = mCanMoveHeight;
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击悬浮窗", Toast.LENGTH_SHORT).show();
            }
        });
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 当前值以屏幕左上角为原点
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        mDownTime = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        wmParams.x += event.getRawX() - mStartX;
                        wmParams.y += event.getRawY() - mStartY;
                        if (wmParams.y < -mCanMoveHeight){
                            wmParams.y = -mCanMoveHeight;
                        }
                        if (wmParams.y > mCanMoveHeight){
                            wmParams.y = mCanMoveHeight;
                        }
                        wm.updateViewLayout(mView, wmParams);
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        wmParams.x = mCanMoveWidth;
                        mStartX = wmParams.x;
                        wm.updateViewLayout(mView, wmParams);
                        mUpTime = System.currentTimeMillis();
                        return mUpTime-mDownTime>200;
                }

                // 消耗触摸事件
                return false;
            }
        });
    }

    public void showFloatView(Context context){
        LogUtils.d("show");
        LogUtils.d("isFloatViewNotAdded="+isFloatViewNotAdded);
        if (FloatWindowUtils.checkPermission(this)){
            if (isFloatViewNotAdded){
                wm.addView(mView, wmParams);
                isFloatViewNotAdded = false;
            }
        }else{
            FloatWindowUtils.applyPermission(this);
        }
    }
    public void removeFloatView(Context context){
        if (!isFloatViewNotAdded){
            wm.removeView(mView);
            isFloatViewNotAdded = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("resume11");
        showFloatView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d("stop11");
        removeFloatView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("destory");
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    public void onClick(View view) {
        startActivity(new Intent(MainActivity.this,Main2Activity.class));
    }
}
