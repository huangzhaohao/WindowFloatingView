package com.hzh.windowfloatingview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("resume22");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("ACTION_ADD_FLOATVIEW"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d("stop22");
    }
}
