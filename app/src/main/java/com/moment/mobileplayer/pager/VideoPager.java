package com.moment.mobileplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import com.moment.mobileplayer.R;
import com.moment.mobileplayer.SystemVideoPlayer;
import com.moment.mobileplayer.base.BasePager;
import com.moment.mobileplayer.domain.MediaItem;
import com.moment.mobileplayer.utils.Utils;

import java.util.ArrayList;




// 本地视频页面
public class VideoPager extends BasePager {

    private ListView lv_video_pager;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;


    private ArrayList<MediaItem> mediaItems;

    private Utils utils;

    public VideoPager(Context context) {
        super(context);
        utils = new Utils();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        lv_video_pager = view.findViewById(R.id.lv_video_pager);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_loading = view.findViewById(R.id.pb_loading);
        // 设置点击事件
        lv_video_pager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MediaItem mediaItem = mediaItems.get(i);
                // 调用系统播放器 --- 隐式意图，通过匹配调用合适的Activity
//                context.startActivity(new Intent().setDataAndType(Uri.parse(mediaItem.getData()), "video/*"));
//                 调用自己写的
//                Intent intent = new Intent(context, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*"); // 文件
//                context.startActivity(intent);
                // 传视频列表
                Intent intent = new Intent(context, SystemVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);
                intent.putExtras(bundle);
                intent.putExtra("position", i);
                context.startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("本地视频数据初始化了........");
        getData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // 主线程
            if (mediaItems != null && mediaItems.size() > 0) {
                tv_nomedia.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);
                // 设置适配器
                lv_video_pager.setAdapter(new VideoPagerAdapter());
            } else {
                tv_nomedia.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
            }

        }
    };

    class VideoPagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
//            ViewHolder viewHolder;
//            if (view == null) {
//                view = View.inflate(context, R.layout.item_video_pager, null);
//                viewHolder = new ViewHolder();
//                viewHolder.tv_name = view.findViewById(R.id.tv_name);
//                viewHolder.tv_duration = view.findViewById(R.id.tv_duration);
//                viewHolder.tv_size = view.findViewById(R.id.tv_size);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
            ViewHolder viewHolder;
            view = View.inflate(context, R.layout.item_video_pager, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = view.findViewById(R.id.tv_name);
            viewHolder.tv_duration = view.findViewById(R.id.tv_duration);
            viewHolder.tv_size = view.findViewById(R.id.tv_size);

            MediaItem mediaItem = mediaItems.get(i);

            viewHolder.tv_name.setText(mediaItem.getName());
            viewHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
            viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));

            return view;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                // 模拟请求网络
//                SystemClock.sleep(2000);
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objects = {
                        MediaStore.Video.Media.DISPLAY_NAME, // 在Sdcard显示的名称
                        MediaStore.Video.Media.DURATION,    // 视频的长度
                        MediaStore.Video.Media.SIZE,        // 视频文件大小
                        MediaStore.Video.Media.DATA         // 视频绝对地址
                };
                Cursor cursor = contentResolver.query(uri, objects, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        // 把视频添加到列表中
                        mediaItems.add(mediaItem);
                    }

                    cursor.close();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}
