package com.hellobrothers.mobileplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.base.Basepager;

public class VideoPager extends Basepager {

    private TextView textView;

    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.GREEN);
        textView.setTextSize(30);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("初始化本地视频");
        textView.setText("本地视频");

    }
}
