package com.example.leakcanary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.squareup.leakcanary.RefWatcher;

public class SecondActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);
        //监听泄漏
        RefWatcher refWatcher = APP.getAppIntance().getRefWatcher();
        if (refWatcher != null) {
            refWatcher.watch(this, "SecondActivity");
        }
    }

    public void jump(View view) {
        finish();
        startActivity(new Intent(this, NewSplashActivity.class));

    }

}
