package com.hellobrothers.mobileplayer.pager;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hellobrothers.mobileplayer.base.Basepager;

public class AudioPager extends Basepager {

    private TextView textView;

    public AudioPager(Context context) {
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
        System.out.println("初始化本地音频");
        textView.setText("本地音频");
    }
}
