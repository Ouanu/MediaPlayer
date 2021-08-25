package com.moment.mobileplayer.utils;

import android.net.TrafficStats;

import java.util.Formatter;
import java.util.Locale;

public class Utils {
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public Utils() {
        // 转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    // 把毫秒转换成： 1:20:30 这样的形式
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是不是网络资源
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri) {
        boolean result = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http")||uri.toLowerCase().startsWith("rtsp")||uri.toLowerCase().startsWith("mms")) {
                result = true;
            }
        }
        return false;
    }

//    private static final String TAG = NetSpeed.class.getSimpleName();


    /**
     * 获取网速
     * @param uid
     * @return
     */
    public String getNetSpeed(int uid) {
        long nowTotalRxBytes = getTotalRxBytes(uid);
//        Log.i(TAG, "nowTotalRxBytes  = " + nowTotalRxBytes);
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return String.valueOf(speed) + " kb/s";
    }

    //getApplicationInfo().uid
    public long getTotalRxBytes(int uid) {
        return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

}
