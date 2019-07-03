package com.hellobrothers.mobileplayer.fragment;

import android.content.Context;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.base.BaseFragment;

public class VideoNetFragment extends BaseFragment {
    public VideoNetFragment(Context context) {
        super(context);
    }

    @Override
    public int setLayoutId() {
        return R.layout.video_net_pager;
    }

    @Override
    public void initData() {

    }
}
