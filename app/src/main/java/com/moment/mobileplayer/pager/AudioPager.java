package com.moment.mobileplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
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
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("本地音频数据初始化了........");
        textView.setText("我是本地音频");
    }
}
