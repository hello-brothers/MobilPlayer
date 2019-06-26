package com.hellobrothers.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hellobrothers.mobileplayer.R;

public class TitleBar extends LinearLayout implements View.OnClickListener {

    private  Context context;
    private View search;
    private View game;
    private View record;

    //代码中实例化调用
    public TitleBar(Context context) {
        this(context, null);
    }

    //xml中使用该控件时，通过该方法实例化
   public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //当需要设置样式的时候
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    //加载控件完后调用
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        search = getChildAt(1);
        game = getChildAt(2);
        record = getChildAt(3);
        search.setOnClickListener(this);
        game.setOnClickListener(this);
        record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_search:
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_record:
                Toast.makeText(context, "播放记录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
