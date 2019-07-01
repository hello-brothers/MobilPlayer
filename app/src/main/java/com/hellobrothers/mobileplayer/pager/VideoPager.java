package com.hellobrothers.mobileplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.activity.SystemVideoPlayer;
import com.hellobrothers.mobileplayer.adapter.VideoRecyAdapter;
import com.hellobrothers.mobileplayer.base.Basepager;
import com.hellobrothers.mobileplayer.domain.MediaItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPager extends Basepager {

    private View view;
    @BindView(R.id.pb_loading)
    ProgressBar pb_loading;
    @BindView(R.id.tv_no_data)
    TextView tv_noData;
    @BindView(R.id.video_recycler)
    RecyclerView recyclerView;
    private List<MediaItem> medias;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            pb_loading.setVisibility(View.GONE);
            if (medias!=null && medias.size() > 0){
                //设置适配器显示数据
                showVideo();
            }else {
                tv_noData.setVisibility(View.VISIBLE);
            }
            return true;
        }
    });

    private void showVideo() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        VideoRecyAdapter videoAdapter = new VideoRecyAdapter(context, medias);
        videoAdapter.setOnItemClickListener(new VideoRecyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(context.getApplicationContext(), position+"", Toast.LENGTH_SHORT).show();
                //1、本地视频播放器
               /* Intent intent = new Intent();
                intent.setDataAndType(Uri.parse(medias.get(position).getName()), "video/*");
                context.startActivity(intent);*/

               //2、app自带播放器
                Intent intent = new Intent(context, SystemVideoPlayer.class);
                intent.setDataAndType(Uri.parse(medias.get(position).getPath()), "video/*");
                context.startActivity(intent);
            }
        });
        recyclerView.setAdapter(videoAdapter);
    }

    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
       view = View.inflate(context, R.layout.video_pager, null);
       ButterKnife.bind(this, view);
       return view;
    }

    @Override
    public void initData() {
        super.initData();
        System.out.println("初始化本地视频");
        getLocalData();
    }

    //加载本地数据
    //从内容提供者中获取
    private void getLocalData() {
        isGrantExternalRW((Activity)context);
        medias = new ArrayList<>();
        pb_loading.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                SystemClock.sleep(3000);
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard中的名字
                        MediaStore.Video.Media.DURATION,//总时长
                        MediaStore.Video.Media.SIZE,//总大小
                        MediaStore.Video.Media.DATA,//绝对地址
                        MediaStore.Video.Media.ARTIST//作者
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor!= null){
                    while (cursor.moveToNext()){
                        MediaItem mediaItem = new MediaItem();
                        String name = cursor.getString(0);//名字
                        mediaItem.setName(name);
                        Long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        Long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String path = cursor.getString(3);
                        mediaItem.setPath(path);
                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);
                        medias.add(mediaItem);
                    }
                }

                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }
}
