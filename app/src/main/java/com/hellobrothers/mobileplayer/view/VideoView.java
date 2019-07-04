package com.hellobrothers.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class VideoView extends android.widget.VideoView {

    /**
     * 在代码中创建的时候一般用这个方法
     * @param context
     */
    public VideoView(Context context) {
        this(context, null);
    }

    /**
     * 在布局文件中，系统通过构造方法实现该类
     * @param context
     * @param attrs
     */
    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当需要设置样式的时候调用改方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //决定当前视图的大小
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置视频的宽和搞
     * @param videoWidth 指定视频的宽
     * @param videoHeight 指定视频的高
     */
    public void setVideoSize(int videoWidth, int videoHeight){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = videoHeight;
        layoutParams.width = videoWidth;
        setLayoutParams(layoutParams);
    }
}
