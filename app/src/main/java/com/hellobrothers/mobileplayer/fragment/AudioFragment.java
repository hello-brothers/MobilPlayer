package com.hellobrothers.mobileplayer.fragment;

import android.content.Context;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.base.BaseFragment;

public class AudioFragment extends BaseFragment {
    public AudioFragment(Context context) {
        super(context);
    }

    @Override
    public int setLayoutId() {
        return R.layout.audio_fragment;
    }

    @Override
    public void initData() {

    }
}
