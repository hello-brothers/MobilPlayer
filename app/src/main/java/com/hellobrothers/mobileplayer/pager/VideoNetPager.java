package com.hellobrothers.mobileplayer.pager;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hellobrothers.mobileplayer.base.Basepager;

public class VideoNetPager extends Basepager {

    private TextView textView;

    public VideoNetPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("初始化网络视频");
        textView.setText("网络视频");
    }
}