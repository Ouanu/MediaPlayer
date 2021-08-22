package com.moment.mobileplayer;

import android.app.AlertDialog;
import android.content.*;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.moment.mobileplayer.domain.MediaItem;
import com.moment.mobileplayer.utils.Utils;
import com.moment.mobileplayer.view.VitamioVideoView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class VitamioVideoPlayer extends AppCompatActivity implements View.OnClickListener {

    // 进度更新
    private static final int PROGRESS = 0;
    /**
     * 隐藏播放器控制面板
     */
    public static final int HIDE_MEDIACONTROLLER = 2;
    /**
     * 默认播放
     */
    private static final int DEFAULT_SCREEN = 3;
    /**
     * 全屏播放
     */
    private static final int FULL_SCREEN = 4;
    /**
     * 获取网速
     */
    private static final int NET_SPEED = 5;
    private VitamioVideoView videoView;
    private Uri uri;

    private LinearLayout llTop;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarSound;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private ImageView iv_battery;

    private Button btn_voice;
    private Button btn_switch_player;
    private Button btn_video_exit;
    private Button btn_video_pre;
    private Button btn_video_start_pause;
    private Button btn_video_next;
    private Button btn_video_switch_screen;

    private Utils utils;
    private BatteryReceiver batteryReceiver;
    private RelativeLayout rl_controller;
    private RelativeLayout rl_loading;
    private LinearLayout ll_buffer;

    private TextView tv_buffer_netSpeed;
    private TextView tv_loading_netSpeed;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:

                    //得到当前的播放进度
                    int currentPosition = (int) videoView.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    // 更新系统时间
                    tvTime.setText(getSystemTime());

                    //设置缓存的效果
                    if (isNetUris) {
                        int buffer = videoView.getBufferPercentage();//0-100
                        int totalBuffer = seekbarVideo.getMax() * buffer;
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    int buffer = currentPosition - prePosition;
                    if (videoView.isPlaying()) {
                        if (buffer < 500) {
                            ll_buffer.setVisibility(View.VISIBLE);
                        } else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

                    prePosition = currentPosition;

                    //每一秒更新一次
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_MEDIACONTROLLER:
                    //隐藏控制面板
                    hideAndShowMediaController();
                    break;
                case NET_SPEED:
                    //获取网速
                    String netSpeed = utils.getNetSpeed(VitamioVideoPlayer.this.getApplicationInfo().uid);
                    tv_buffer_netSpeed.setText("缓冲中..." + netSpeed);
                    tv_loading_netSpeed.setText("正在加载中..." + netSpeed);
                    handler.sendEmptyMessageDelayed(NET_SPEED, 1000);
            }
        }
    };
    private ArrayList<MediaItem> mediaItems;
    private int position;
    //1.定义手势识别器
    private GestureDetector detector;
    private Object default_screen;

    /**
     * 屏幕宽高
     */
    private int screenWidth;
    private int screenHeight;

    /**
     * 视频本身的宽和高
     */
    private int videoWidth;
    private int videoHeight;

    private AudioManager audioManager;
    /**
     * 当前音量值
     */
    private int currentVolume;
    /**
     * 创建最大音量
     */
    private int maxVolume;
    /**
     * 是否是静音
     */
    private boolean isMute = false;
    private boolean isNetUris = false;

    private int prePosition = 0;


    /**
     * 调用系统时间
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamio_video_player);
        //初始化解码器
        Vitamio.isInitialized(getApplicationContext());

        initData();
        llTop = findViewById(R.id.ll_top);
        tvName = findViewById(R.id.tv_name);
        tvTime = findViewById(R.id.tv_time);
        iv_battery = findViewById(R.id.iv_battery);
        btn_voice = findViewById(R.id.btn_voice);
        btn_voice.setOnClickListener(this);
        seekbarSound = findViewById(R.id.seekbar_sound);
        btn_switch_player = findViewById(R.id.btn_switch_player);
        btn_switch_player.setOnClickListener(this);
        llBottom = findViewById(R.id.ll_bottom);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        seekbarVideo = findViewById(R.id.seekbar_video);
        tvDuration = findViewById(R.id.tv_duration);
        btn_video_exit = findViewById(R.id.btn_video_exit);
        btn_video_exit.setOnClickListener(this);
        btn_video_pre = findViewById(R.id.btn_video_pre);
        btn_video_pre.setOnClickListener(this);
        btn_video_start_pause = findViewById(R.id.btn_video_start_pause);
        btn_video_start_pause.setOnClickListener(this);
        btn_video_next = findViewById(R.id.btn_video_next);
        btn_video_next.setOnClickListener(this);
        btn_video_switch_screen = findViewById(R.id.btn_video_switch_screen);
        btn_video_switch_screen.setOnClickListener(this);
        rl_controller = findViewById(R.id.rl_controller);
        rl_loading = findViewById(R.id.rl_loading);
        ll_buffer = findViewById(R.id.ll_buffer);

        tv_buffer_netSpeed = findViewById(R.id.tv_buffer_netSpeed);
        tv_loading_netSpeed = findViewById(R.id.tv_loading_netSpeed);

        /**
         * 设置音量最大值
         */
        seekbarSound.setMax(maxVolume);
        //设置默认值
        seekbarSound.setProgress(currentVolume);

        videoView = findViewById(R.id.videoView);
        getData();
        setData();

        // 控制面板
//        videoView.setMediaController(new MediaController(this));

        setListener();
        handler.sendEmptyMessage(NET_SPEED);

    }


    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            videoView.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            isNetUris = utils.isNetUri(mediaItem.getData());
        } else if (uri != null) {
            videoView.setVideoURI(uri);
            tvName.setText(uri.toString());
            isNetUris = utils.isNetUri(uri.toString());
        }

        setButtonState();

        //设置不锁屏
        videoView.setKeepScreenOn(true);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void getData() {
        uri = getIntent().getData();   //得到一个地址：文件浏览器，浏览器，相册
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0); //列表中的位置
    }

    private void setListener() {
        // 播放准备好了调用此方法
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

//                mediaPlayer.setLooping(true);
                videoWidth = mediaPlayer.getVideoWidth();
                videoHeight = mediaPlayer.getVideoHeight();
                // 1.得到视频的总时长和SeekBar.setMax();
                int duration = (int) videoView.getDuration();
                seekbarVideo.setMax(duration);

                // 设置总时长
                tvDuration.setText(utils.stringForTime(duration));

                //  2.发消息更新
                handler.sendEmptyMessage(PROGRESS);

                videoView.start();
//                mediaPlayer.setLooping(true);

                hideAndShowMediaController();
                setVideoType(DEFAULT_SCREEN);

                //隐藏加载页面
                rl_loading.setVisibility(View.GONE);
            }
        });
        // 播放出错调用此方法
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                Toast.makeText(SystemVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
//                1.播放不支持的视频格式--跳转到万能播放器继续播放
//                2.播放网络视频的过程中--网络中断，重新播放
//                3.视频文件中间部分有缺损--把下载模块解决掉
                showErrorDialog();
                return true;
            }
        });
        // 播放完成回调此方法
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(VitamioVideoPlayer.this, "播放完成", Toast.LENGTH_SHORT).show();
                setPlayNext();
            }
        });

        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        seekbarSound.setOnSeekBarChangeListener(new SoundOnSeekBarChangeListener());

        //设置监听卡
//        videoView.setOnInfoListener(new MyOnInfoListener());
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("播放视频出错了，请检查网络或视频是否有缺损！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        }).setCancelable(false).show();
    }

    /**
     * 是否全屏播放
     */
    private boolean isFullScreen = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVolume --;
            updateVolumeProgress(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 5000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume ++;
            updateVolumeProgress(currentVolume);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setVideoType(int type) {
        switch (type) {
            case FULL_SCREEN:

                videoView.setVideoSize(screenWidth, screenHeight);
                isFullScreen = true;

                btn_video_switch_screen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);
                break;
            case DEFAULT_SCREEN:

                // 真实视频宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                /**
                 * 要播放视频的宽和高
                 */
                int height = screenHeight;
                int width = screenWidth;

                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    // for compatibility, we adjust size based on aspect ratio
                    if (mVideoWidth * height < width * mVideoHeight) {
                        //Log.i("@@@", "image too wide, correcting");
                        width = height * mVideoWidth / mVideoHeight;
                    } else if (mVideoWidth * height > width * mVideoHeight) {
                        //Log.i("@@@", "image too tall, correcting");
                        height = width * mVideoHeight / mVideoWidth;
                    }

                    videoView.setVideoSize(width, height);
                }
                btn_video_switch_screen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);

                isFullScreen = false;
                break;
        }
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当进度更新的时候回调此方法
         *
         * @param seekBar
         * @param progress 当前进度
         * @param fromUser 是否是由用户引起
         */

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//                videoView.seekTo(seekBar.getProgress());

//            videoView.seekTo(progress);
        }

        /**
         * 当手指触碰SeekBar的时候回调此方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
//            videoView.seekTo(seekBar.getProgress());
        }

        /**
         * 当手指松开SeekBar的时候回调此方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            videoView.seekTo(seekBar.getProgress());
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 5000);
        }
    }

    private void initData() {
        //实例化AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        // 得到屏幕的高和宽
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        screenWidth = displayMetrics.widthPixels;
//        screenHeight = displayMetrics.heightPixels;


        utils = new Utils();
        // 注册监听电量广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, intentFilter);

        //实例化手势识别器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
//                Toast.makeText(SystemVideoPlayer.this, "长按", Toast.LENGTH_SHORT).show();
                startAndPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "双击", Toast.LENGTH_SHORT).show();
                if (isFullScreen) {
                    setVideoType(DEFAULT_SCREEN);
                } else {
                    setVideoType(FULL_SCREEN);
                }
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "单击", Toast.LENGTH_SHORT).show();
                hideAndShowMediaController();
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    /**
     * 媒体播放器控制显示或隐藏
     */
    private void hideAndShowMediaController() {
        if (rl_controller.getVisibility() == View.VISIBLE) {
            rl_controller.setVisibility(View.GONE);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        } else {
            rl_controller.setVisibility(View.VISIBLE);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 5000);
        }
    }

    /**
     * 获取电量信息
     */
    class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0); // 电量 0-100
            // 主线程
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            iv_battery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            iv_battery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            iv_battery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            iv_battery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            iv_battery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            iv_battery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            iv_battery.setImageResource(R.drawable.ic_battery_100);
        } else {
            iv_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_voice) {
            isMute = !isMute;
            updateVolumeProgress(currentVolume);
        } else if (view.getId() == R.id.btn_switch_player) {

        } else if (view.getId() == R.id.btn_video_exit) {
            finish();
        } else if (view.getId() == R.id.btn_video_pre) {
            setPlayPre();
        } else if (view.getId() == R.id.btn_video_next) {
            setPlayNext();
        } else if (view.getId() == R.id.btn_video_switch_screen) {
            if (isFullScreen) {
                setVideoType(DEFAULT_SCREEN);
            } else {
                setVideoType(FULL_SCREEN);
            }
        } else if (view.getId() == R.id.btn_video_start_pause) {
            startAndPause();
        }
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 5000);
    }

    private void startAndPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
            btn_video_start_pause.setBackgroundResource(R.drawable.btn_video_play_selector);
        } else {
            videoView.start();
            btn_video_start_pause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private void setPlayPre() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放上一个
            position--;
            if (position >= 0) {

                MediaItem mediaItem = mediaItems.get(position);
                videoView.setVideoPath(mediaItem.getData()); //设置播放地址
                tvName.setText(mediaItem.getName());
                isNetUris = utils.isNetUri(uri.toString());

                setButtonState();
                rl_loading.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setPlayNext() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放下一个
            position++;
            if (position < mediaItems.size()) {

                MediaItem mediaItem = mediaItems.get(position);
                videoView.setVideoPath(mediaItem.getData()); //设置播放地址
                tvName.setText(mediaItem.getName());
                isNetUris = utils.isNetUri(uri.toString());

                setButtonState();

                if (position == mediaItem.getSize() - 1) {
                    Toast.makeText(VitamioVideoPlayer.this, "这已经是最后一个视频了", Toast.LENGTH_SHORT).show();
                }
                rl_loading.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        } else if (uri != null) {
            //停止播放
            finish();
        }
    }

    /**
     * 设置上一个和下一个按钮状态
     */
    private void setButtonState() {

        // 播放列表
        if (mediaItems != null && mediaItems.size() > 0) {

            if (position == 0) {//第一个视频
                btn_video_pre.setEnabled(false);
                btn_video_next.setEnabled(true);
                btn_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
                btn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
            } else if (position == mediaItems.size() - 1) { //最后一个视频
                btn_video_next.setEnabled(false);
                btn_video_pre.setEnabled(true);
                btn_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                btn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
            } else {
                btn_video_next.setEnabled(true);
                btn_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
                btn_video_pre.setEnabled(true);
                btn_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            }
        } else if (uri != null) {
            btn_video_next.setEnabled(false);
            btn_video_next.setBackgroundResource(R.drawable.btn_next_gray);
            btn_video_pre.setEnabled(false);
            btn_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
        } else {
            Toast.makeText(this, "没有播放地址", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }
        super.onDestroy();
    }

    private float startY;
    private float touchRang;
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 3. 把事件给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下时记录一个初始值
                startY = event.getY();
                touchRang = Math.min(screenHeight, screenWidth);
                mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE:
                //来到新坐标
                float endY = event.getY();
                //计算偏移量
                float distanceY = startY - endY;
                //屏幕滑动的距离 / 总距离 = 改变的声音 / 最大音量
                float changeVolume = (distanceY / touchRang) * maxVolume;

                //最终的声音 = 原来的声音 + 改变的声音
                float volume = Math.min(Math.max(mVol + changeVolume,0), maxVolume);

                if (changeVolume != 0) {
                    updateVolumeProgress((int) volume);
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 5000);

                break;
        }

        return super.onTouchEvent(event);
    }

    class SoundOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                updateVolumeProgress(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 5000);
        }
    }

    /**
     * 根据传入值修改音量
     *
     * @param progress
     */
    private void updateVolumeProgress(int progress) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        seekbarSound.setProgress(progress); // 设置seekBar进度
        currentVolume = progress;
        if (progress <= 0) {
            isMute = true;
        } else {
            isMute = false;
        }
    }


    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START: //开始卡了，拖动卡了
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://卡结束了，拖动卡结束
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    }
}
