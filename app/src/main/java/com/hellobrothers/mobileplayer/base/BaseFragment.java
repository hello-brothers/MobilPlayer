package com.hellobrothers.mobileplayer.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobrothers.mobileplayer.R;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    public Context context;

    public BaseFragment() {
    }

    public BaseFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setLayoutId(), null);
        ButterKnife.bind(this, view);
        initData();
        return view;

    }

    /**
     * 设置fragment的关联xml
     * @return
     */
    public abstract int setLayoutId();

    //当界面有数据需要处理的时候调用
    public void initData(){

    }
}
