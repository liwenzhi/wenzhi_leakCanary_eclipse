package com.example.leakcanary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;

/**
 * 测试启动页面的内存泄漏
 */
public class NewSplashActivity extends Activity implements View.OnClickListener {

    private RoundProgressBar mRpb_skip_splash;
    private Handler mHandler = new Handler();
    //显示广告页面的时间，5 秒
    private long showTime = 5;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //监听泄漏
        RefWatcher refWatcher = APP.getAppIntance().getRefWatcher();
        if (refWatcher != null) {
            refWatcher.watch(this, "NewSplashActivity");
        }


        initView();
        initListener();
        initData();
    }


    /**
     * 初始化视图
     */
    private void initView() {
        // 跳过Splash按钮
        mRpb_skip_splash = (RoundProgressBar) findViewById(R.id.rpb_skip_splash);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mRpb_skip_splash.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //延迟5000ms跳转到主页面
        handler.sendEmptyMessageDelayed(111, showTime * 1000);
        mRpb_skip_splash.setProgressingTime(showTime * 1000);
        mRpb_skip_splash.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rpb_skip_splash:
                // 直接跳转第二个页面
                mHandler.removeCallbacksAndMessages(null);
                mRpb_skip_splash.stop();
                gotoMyActivity();
                break;
        }
    }


    //创建Handler对象
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 111) {
                gotoMyActivity();
            }

        }
    };


    /**
     * 跳转到第一个界面
     */

    public void gotoMyActivity() {
        finish();
        startActivity(new Intent(NewSplashActivity.this, MyActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        if (mRpb_skip_splash != null) {
            mRpb_skip_splash.stop();
        }

        if (mHandler != null) {
            mRpb_skip_splash.stop();
            mHandler.removeCallbacksAndMessages(null);  //情况Handler中的数据
            mHandler = null;
        }

        super.onDestroy();
    }
}
