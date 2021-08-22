package com.moment.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import io.vov.vitamio.widget.VideoView;


/**
 * 自定义VideoView
 */
public class VitamioVideoView extends VideoView {
    public VitamioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);  //保持测量结果
    }

    /**
     * 设置视频的画面大小
     * @param width 设置视频的宽
     * @param height  设置视频的高
     */
    public void setVideoSize(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }
}
