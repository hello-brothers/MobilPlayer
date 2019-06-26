package com.hellobrothers.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * 四个pager的基类
 */
public abstract class Basepager {
    public  Context context;
    public  View view;
    public boolean isInit;

    public Basepager(Context context){
        this.context = context;
        this.view = initView();
    }

    //抽象方法 子类必须实现 返回一个视图对象
    public abstract View initView();


    //当界面有数据需要处理的时候调用
    public void initData() {

    }
}
