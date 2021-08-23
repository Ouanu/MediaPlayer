package com.moment.mobileplayer.pager;

import android.content.Context;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.media.MediaPlayer;

import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.moment.mobileplayer.R;
import com.moment.mobileplayer.SystemVideoPlayer;
import com.moment.mobileplayer.base.BasePager;
import com.moment.mobileplayer.domain.MediaItem;
import com.moment.mobileplayer.utils.URL;
import com.moment.mobileplayer.view.VideoView;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;


// 网络视频页面
public class NetVideoPager extends BasePager {

    private ListView lv_video_pager;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;


    private ArrayList<MediaItem> mediaItems;
    private MyNetVideoAdapter adapter;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_pager, null);
        lv_video_pager = view.findViewById(R.id.lv_video_pager);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_loading = view.findViewById(R.id.pb_loading);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("网络视频数据初始化了........");

        getDataFromNet();

    }

    /**
     * 联网请求数据
     */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(URL.NET_VIDEO_URL);
        // params.setSslSocketFactory(...); // 如果需要自定义SSL
//        params.addQueryStringParameter("wd", "xUtils");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("NetVideo", "联网请求成功"+result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("NetVideo", "联网请求失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String json) {
        //解析数据:
        // 1.手动解析（系统接口）
        //2.用第三方解析工具：gson 和 fast_json
        parseJson(json);

        //设置适配器
        adapter = new MyNetVideoAdapter();
        lv_video_pager.setAdapter(adapter);

    }

    class MyNetVideoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_net_video_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_pic = convertView.findViewById(R.id.iv_pic);
                viewHolder.tv_title = convertView.findViewById(R.id.tv_title);
                viewHolder.tv_desc = convertView.findViewById(R.id.tv_desc);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到对应的String
            MediaItem mediaItem = mediaItems.get(position);
            viewHolder.tv_title.setText(mediaItem.getName());
            viewHolder.tv_desc.setText(mediaItem.getDesc());

            //请求图片：xUtils或者Glide
//            x.image().bind(viewHolder.iv_pic, mediaItem.getImageUrl());

            Glide.with(convertView).load(mediaItem.getImageUrl()).into(viewHolder.iv_pic);


            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_pic;
        TextView tv_title;
        TextView tv_desc;
    }

    private void parseJson(String json) {
        try {
            mediaItems = new ArrayList<>();
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.optJSONArray("data");
//            JSONArray jsonArray = object.getJSONArray("douga");//不好，易崩溃

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                if (jsonObject != null) {
                    MediaItem mediaItem = new MediaItem();
                    //以下为b站的key格式，按需更改
                    //图片
                    String pic = jsonObject.optString("pic");
                    mediaItem.setImageUrl("pic");
                    //视频链接
                    String short_link = jsonObject.optString("short_link");
                    mediaItem.setImageUrl(short_link);
                    //视频标题
                    String title = jsonObject.optString("title");
                    mediaItem.setName(title);
                    //视频简介
                    String dynamic = jsonObject.optString("description");
                    mediaItem.setDesc(dynamic);

                    mediaItems.add(mediaItem);//添加到集合中，也可放存取数据前
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
