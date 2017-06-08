package com.example.leakcanary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;


/**
 * 内存泄漏测试
 */
public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        //监听泄漏
        RefWatcher refWatcher = APP.getAppIntance().getRefWatcher();
        if (refWatcher != null) {
            refWatcher.watch(this, "MyActivity");
        }
    }


    public void jump(View view) {

        finish();
        startActivity(new Intent(this, SplashActivity.class));

    }
}
