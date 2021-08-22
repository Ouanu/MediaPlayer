package com.moment.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.media.MediaPlayer;

import android.widget.MediaController;
import android.widget.TextView;
import com.moment.mobileplayer.SystemVideoPlayer;
import com.moment.mobileplayer.base.BasePager;
import com.moment.mobileplayer.view.VideoView;


// 网络视频页面
public class NetVideoPager extends BasePager {

    private TextView textView;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
//        textView.setText("我是网络视频");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(30);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SystemVideoPlayer.class);
                intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4"), "video/*");
//                intent.setDataAndType(Uri.parse("mnt/mp4/190204084208765161.mp4"), "video/*");

                context.startActivity(intent);
            }
        });
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("网络视频数据初始化了........");
        textView.setText("我是网络视频");
    }
}
