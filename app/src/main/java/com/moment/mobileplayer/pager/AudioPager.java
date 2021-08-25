package com.moment.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.moment.mobileplayer.SystemVideoPlayer;
import com.moment.mobileplayer.base.BasePager;


// 本地音频页面
public class AudioPager extends BasePager {

    private TextView textView;

    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
//        textView.setText("我是本地音频");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SystemVideoPlayer.class).setData(Uri.parse("https://okjx.cc/?url=https://www.meipai.com/media/1206023510")));
            }
        });
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("本地音频数据初始化了........");
        textView.setText("我是本地音频");
    }
}
