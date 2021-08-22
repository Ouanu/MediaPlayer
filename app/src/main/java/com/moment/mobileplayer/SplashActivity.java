package com.moment.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends Activity {

    private Handler handler = new Handler();
    private boolean isStart = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 在主线程执行
                startMainActivity();
            }
        }, 5000);
    }

    private void startMainActivity() {
        if (!isStart) {
            Log.e("SP", "in startMainActivity");
            startActivity(new Intent(this, MainActivity.class));
            isStart = true;
            // 关闭启动页面
            finish();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}