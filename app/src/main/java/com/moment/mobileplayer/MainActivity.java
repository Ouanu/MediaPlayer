package com.moment.mobileplayer;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RadioGroup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.moment.mobileplayer.base.BasePager;
import com.moment.mobileplayer.fragment.MyFragment;
import com.moment.mobileplayer.pager.AudioPager;
import com.moment.mobileplayer.pager.NetAudioPager;
import com.moment.mobileplayer.pager.NetVideoPager;
import com.moment.mobileplayer.pager.VideoPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;

    private ArrayList<BasePager> basePagers;

    private Fragment fragment;

    // 页面的位置
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rg_main = findViewById(R.id.rg_main);

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));   // 本地视频
        basePagers.add(new AudioPager(this));   // 本地音乐
        basePagers.add(new NetVideoPager(this));// 网络视频
        basePagers.add(new NetAudioPager(this));// 网络音乐

        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.rb_video);
        setFragment();
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i) {
                default://本地视频
                    position = 0;
                    break;
                case R.id.rb_audio://本地音乐
                    position = 1;
                    break;
                case R.id.rb_net_video://网络视频
                    position = 2;
                    break;
                case R.id.rb_net_audio://网络音乐
                    position = 3;
                    break;
            }
            setFragment();
        }

    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();//得到fragmentManger
        FragmentTransaction ft = fm.beginTransaction();//开启事务

        BasePager basePager = getBasePager();
        fragment = new MyFragment(basePager);
        ft.replace(R.id.fl_main, fragment);
        ft.commit(); // 提交
    }

    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData) {
            basePager.isInitData = true;
            basePager.initData();
        }
        return basePager;
    }

}