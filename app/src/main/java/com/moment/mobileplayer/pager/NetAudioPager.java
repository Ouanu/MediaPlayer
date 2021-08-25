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


// 网络音频页面
public class NetAudioPager extends BasePager {

    private TextView textView;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
//        textView.setText("我是网络音频");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SystemVideoPlayer.class);
                intent.setDataAndType(Uri.parse("https://www.bilibili.com/video/BV1KU4y1E733?spm_id_from=333.851.b_62696c695f7265706f72745f646f756761.41"), "video/*");
                context.startActivity(intent);
            }
        });
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("网络音频数据初始化了........");
        textView.setText("我是网络音频");
    }
}
