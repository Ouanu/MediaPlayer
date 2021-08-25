package com.moment.test;


import android.os.Bundle;

import android.webkit.*;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class WebPlayer extends AppCompatActivity {
    private WebView webView;
    private FrameLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_webview);

        webView = findViewById(R.id.wv_mywv);
        mLayout = findViewById(R.id.fl_video);


        webView.loadUrl("https://jx.quanmingjiexi.com/?url=https://www.bilibili.com/video/BV1KU4y1E733?spm_id_from=333.851.b_62696c695f7265706f72745f646f756761.41");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });

    }

}
