package com.hellobrothers.mobileplayer.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.hellobrothers.mobileplayer.base.BaseFragment;
import com.hellobrothers.mobileplayer.domain.MediaItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class VideoFragment extends BaseFragment {
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


    public VideoFragment() {
        super();

    }

    public VideoFragment(Context context) {
        super(context);
    }

    @Override
    public int setLayoutId() {
        return R.layout.video_pager;
    }

    @Override
    public void initData() {
        System.out.println("初始化本地视频");
        VideoFragmentPermissionsDispatcher.getLocalDataWithPermissionCheck(this);
    }


    //加载本地数据
    //从内容提供者中获取
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE})
    public void getLocalData() {
//        isGrantExternalRW((Activity)context);

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
                cursor.close();

                handler.sendEmptyMessage(1);
            }
        }).start();
    }

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

                //2、app自带播放器 一个播放地址
//                Intent intent = new Intent(context, SystemVideoPlayer.class);
//                intent.setDataAndType(Uri.parse(medias.get(position).getPath()), "video/*");
//                context.startActivity(intent);

                Intent intent = new Intent(context, SystemVideoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("medialist", (Serializable) medias);
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
        recyclerView.setAdapter(videoAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        VideoFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
