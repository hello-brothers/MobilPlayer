package com.hellobrothers.mobileplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemVideoPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    /**
     * 进度条更新事件
     */
    private static final int PROGRESS = 1;
    @BindView(R.id.videoview)
    VideoView videoView;
    @BindView(R.id.btn_pause)
    Button btn_pause;
    @BindView(R.id.seekbar_video)
    SeekBar seekBar;
    @BindView(R.id.tx_current_time)
    TextView tv_current_time;
    @BindView(R.id.tv_duration)
    TextView tv_time;
    @BindView(R.id.video_name)
    TextView tv_videoname;
    @BindView(R.id.img_battery)
    ImageView img_battery;
    @BindView(R.id.system_time)
    TextView tv_system_time;
    private MBatteryBroadcastReceivery myBatteryBroadcastReceiver;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS:
                    int currentPosition = videoView.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tv_current_time.setText(utils.stringForTime(currentPosition));
                    //设置系统时间
                    tv_system_time.setText(getSystemTime());
                    //每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
            return false;
        }
    });

    private String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    private void initData() {
        utils = new Utils();
        //更新电池变化
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        myBatteryBroadcastReceiver = new MBatteryBroadcastReceivery();
        registerReceiver(myBatteryBroadcastReceiver, intentFilter);
    }

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        ButterKnife.bind(this);
        initVideo();
        initData();
        //得到播放地址
        Uri uri = getIntent().getData();
        if (uri != null){
            videoView.setVideoURI(uri);
        }
    }

    /**
     * 初始化播放器
     */
    private void initVideo() {
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnCompletionListener(this);
        //设置控制面板
//        videoView.setMediaController(new MediaController(this));
        seekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 播放准备好后调用
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();
        int duration = videoView.getDuration();
        seekBar.setMax(duration);
        tv_time.setText(utils.stringForTime(duration));
        tv_current_time.setText(utils.stringForTime(0));
        handler.sendEmptyMessage(PROGRESS);
    }

    /**
     * 播放出错调用
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * 播放完成后调用
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(this, "播放完成了哟！", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_pause)
    public void controlView(){
        if (videoView.isPlaying()){
            Toast.makeText(this, "暂停", Toast.LENGTH_SHORT).show();
            btn_pause.setBackgroundResource(R.drawable.btn_video_start_select);
            videoView.pause();
        }else {

            Toast.makeText(this, "开始", Toast.LENGTH_SHORT).show();
            btn_pause.setBackgroundResource(R.drawable.btn_video_pause_select);
            videoView.start();
        }
    }

    /**
     * 进度改变就有回调
     * @param seekBar
     * @param progress
     * @param fromUser 用户手动改变为true
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            videoView.seekTo(progress);
        }
    }

    /**
     * 开始触摸 回调
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 触摸离开 回调
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class MBatteryBroadcastReceivery extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            img_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    @Override
    protected void onDestroy() {
        if (myBatteryBroadcastReceiver!=null){
            unregisterReceiver(myBatteryBroadcastReceiver);
            myBatteryBroadcastReceiver = null;

        }
        super.onDestroy();
    }
}
