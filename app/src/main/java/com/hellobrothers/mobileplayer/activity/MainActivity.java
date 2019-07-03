package com.hellobrothers.mobileplayer.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.base.BaseFragment;
import com.hellobrothers.mobileplayer.fragment.AudioFragment;
import com.hellobrothers.mobileplayer.fragment.AudioNetFragmnet;
import com.hellobrothers.mobileplayer.fragment.VideoFragment;
import com.hellobrothers.mobileplayer.fragment.VideoNetFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    @BindView(R.id.rg_tag_bottom)
    RadioGroup rg_bottom;
    //当前位置
    private int position;
    //存放viewpager
//    private List<Basepager> pagers;

    //存放fragment
    private List<BaseFragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        pagers = new ArrayList<>();
//        pagers.add(new VideoPager(this));
//        pagers.add(new AudioPager(this));
//        pagers.add(new VideoNetPager(this));
//        pagers.add(new AudioNetPager(this));

        //创建fragment
        fragments = new ArrayList<>();
        fragments.add(new VideoFragment(this));
        fragments.add(new VideoNetFragment(this));
        fragments.add(new AudioFragment(this));
        fragments.add(new AudioNetFragmnet(this));

        rg_bottom.setOnCheckedChangeListener(new MyOncheckedChangeListener());
        rg_bottom.check(R.id.rb_video);
    }

    class MyOncheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                default:
                    position = -1;
                case R.id.rb_video:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
            }
            initFragment();
        }
    }

    public void initFragment() {
        //创建FragmentManager
        FragmentManager manager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction transaction = manager.beginTransaction();
        //替换
//        transaction.replace(R.id.fl_pager, new MyFragment(getBasePager()));
        BaseFragment bf = fragments.get(position);
        if (bf!=null){
//            bf.initData();
            transaction.replace(R.id.fl_pager, bf);
        }
        //提交事务
        transaction.commit();

    }

//    private Basepager getBasePager() {
//        Basepager basepager = pagers.get(position);
//        if (basepager!=null && !basepager.isInit){
//            basepager.initData();
//            basepager.isInit = true;
//        }
//        return basepager;
//    }
}
