package com.hellobrothers.mobileplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hellobrothers.mobileplayer.base.Basepager;

public class MyFragment extends Fragment {
    private Basepager basePager;

    public MyFragment() {

    }

    public MyFragment(Basepager basePager) {
        this.basePager = basePager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (basePager!=null){
            return basePager.view;
        }
        return basePager.view;
    }
}
