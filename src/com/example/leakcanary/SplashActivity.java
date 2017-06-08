package com.example.leakcanary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;

/**
 * 测试启动页面的内存泄漏
 */
public class SplashActivity extends Activity implements View.OnClickListener {
    private StartMainTask mStartMainTask;
    private RoundProgressBar mRpb_skip_splash;
    private Handler mHandler = new Handler();
    private long progressingTime = 5000; // splash界面显示时长


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //监听泄漏
        RefWatcher refWatcher = APP.getAppIntance().getRefWatcher();
        if (refWatcher != null) {
            refWatcher.watch(this, "SplashActivity");
        }
        // 创建跳转主界面任务
        mStartMainTask = new StartMainTask();

        initView();
        initListener();
        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler != null && mStartMainTask != null) {
            mHandler.postDelayed(mStartMainTask, progressingTime);
            mRpb_skip_splash.setProgressingTime(progressingTime);
            mRpb_skip_splash.start();
        }
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rpb_skip_splash:
                // 直接跳转第二个页面
                mHandler.removeCallbacks(mStartMainTask);
                mRpb_skip_splash.stop();
                gotoSecondActivity();
                break;
        }
    }


    /**
     * 启动界面跳转任务
     */
    class StartMainTask implements Runnable {
        @Override
        public void run() {
            gotoSecondActivity();
        }
    }


    /**
     * 跳转到第二个界面
     */

    public void gotoSecondActivity() {
        finish();
        startActivity(new Intent(SplashActivity.this, SecondActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null && mStartMainTask != null) {
            mRpb_skip_splash.stop();
            mHandler.removeCallbacks(mStartMainTask);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null && mStartMainTask != null) {
            mRpb_skip_splash.stop();
            mHandler.removeCallbacks(mStartMainTask);
        }


    }
}
