package com.hellobrothers.mobileplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.domain.MediaItem;
import com.hellobrothers.mobileplayer.utils.Utils;
import com.hellobrothers.mobileplayer.view.VideoView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemVideoPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    /**
     * 进度条更新事件
     */
    private static final int PROGRESS = 1;
    private static final int HIDE_MEDIACONTROL = 2;
    @BindView(R.id.videoview)
    VideoView videoView;
    @BindView(R.id.btn_pause)
    Button btn_pause;
    @BindView(R.id.seekbar_video)
    SeekBar video_seekBar;
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
    @BindView(R.id.btn_video_next)
    Button btn_next;
    @BindView(R.id.btn_video_pre)
    Button btn_pre;
    @BindView(R.id.media_controller)
    RelativeLayout rl_media_controller;
    @BindView(R.id.btn_video_switch)
    Button btn_video_switch;
    @BindView(R.id.seekbar_voice)
    SeekBar voice_seekBar;
    //手势识别器
    private GestureDetector detector;

    //声音管理
    private AudioManager am;
    //当前声音
    private int currentAudio;
    //最大声音
    private int maxVolume;
    //是否静音
    private boolean isMute = true;


    /**
     * 屏幕宽与高
     */
    private int screenWidth, screenHeight;

    /**
     *视频的宽与高
     */
    private int videoHeight, videoWidth;
    /**
     * 创建屏幕类型tag
     */
    private int SCREEN_TYPE;
    private final int FULL_SCREEN = 1;
    private final int DEFAULT_SCREEN = 2;

    /**
     * 用于互动屏幕改变音量
     */
    private float startY;
    private float moveRange;
    //当前音量
    private int curVol;


    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        /**
         * 单击屏幕
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            controlMediaControllerVisible();
            return super.onSingleTapConfirmed(e);
        }

        /**
         * 长按屏幕
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            controlStartAndPause();
        }

        /**
         * 双击屏幕
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            if (SCREEN_TYPE == FULL_SCREEN){
//                setVideoType(DEFAULT_SCREEN);
//                SCREEN_TYPE = DEFAULT_SCREEN;
//            }else if (SCREEN_TYPE == DEFAULT_SCREEN){
//                setVideoType(FULL_SCREEN);
//                SCREEN_TYPE = FULL_SCREEN;
//            }
            setVideoType();
            return super.onDoubleTap(e);
        }
    };


    private void setVideoType(int type) {
        switch (type){
            case FULL_SCREEN:
                videoView.setVideoSize(screenWidth, screenHeight);
                Toast.makeText(this, "全屏", Toast.LENGTH_SHORT).show();
                btn_video_switch.setBackgroundResource(R.drawable.btn_video_switch_screen_default_select);
                SCREEN_TYPE = FULL_SCREEN;
                break;
            case DEFAULT_SCREEN:
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                int height = screenHeight;
                int width = screenWidth;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                Toast.makeText(this, "默认", Toast.LENGTH_SHORT).show();
                videoView.setVideoSize(width, height);
                btn_video_switch.setBackgroundResource(R.drawable.btn_video_switch_screen_full_select);
                SCREEN_TYPE = DEFAULT_SCREEN;
                break;
        }
    }


    private MBatteryBroadcastReceivery myBatteryBroadcastReceiver;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case HIDE_MEDIACONTROL:
                    controlMediaControllerVisible();
                    break;
                case PROGRESS:
                    int currentPosition = videoView.getCurrentPosition();
                    video_seekBar.setProgress(currentPosition);
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
    /**
     * 单个视频播放地址
     */
    private Uri uri;
    /**
     * 视频列表
     */
    private ArrayList<MediaItem> medialist;
    /**
     * 播放视频的位置
     */
    private int position;

    private String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }



    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        ButterKnife.bind(this);
        initVideo();
        initData();
        getData();
        setData();
        updatecontrolStartAndPause();
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
        video_seekBar.setOnSeekBarChangeListener(this);
        voice_seekBar.setOnSeekBarChangeListener(new MyVoiceSeekBarChangeListener());
        detector = new GestureDetector(this, gestureListener);
        /**
         * 得到屏幕的宽和高
         */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;


    }

    /**
     * 初始化数据 --> 广告接收者 得到电池的系统通知
     */
    private void initData() {
        utils = new Utils();
        //更新电池变化
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        myBatteryBroadcastReceiver = new MBatteryBroadcastReceivery();
        registerReceiver(myBatteryBroadcastReceiver, intentFilter);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentAudio= am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 通过intent得到需要播放的数据
     */
    private void getData() {
        //得到播放地址
        uri = getIntent().getData();
        medialist = (ArrayList<MediaItem>) getIntent().getSerializableExtra("medialist");
        position = getIntent().getIntExtra("position", 0);

    }

    /**
     * 设置播放数据
     */
    private void setData() {
        voice_seekBar.setMax(maxVolume);
        voice_seekBar.setProgress(currentAudio);
        if (medialist!=null&&medialist.size()>0){
            MediaItem item = medialist.get(position);
            videoView.setVideoPath(item.getPath());
            tv_videoname.setText(item.getName());
        }else if (uri != null){
            videoView.setVideoURI(uri);
            tv_videoname.setText(uri.toString());
        }else {
            Toast.makeText(this, "小哥哥没有传递数据", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.btn_pause, R.id.btn_video_next, R.id.btn_video_pre, R.id.btn_video_switch, R.id.btn_voice})
    public void onViewClicked(View view) {
        handler.removeMessages(HIDE_MEDIACONTROL);
        switch (view.getId()) {
            case R.id.btn_pause:
                controlStartAndPause();
            break;
            case R.id.btn_video_next:
                nextVideo();
                break;
            case R.id.btn_video_pre:
                preVideo();
                break;
            case R.id.btn_video_switch:
                setVideoType();
                break;
            case R.id.btn_voice:
                isMute = isMute == false ? true : false;
                updateVoice(currentAudio, isMute);
                break;
        }
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROL, 4000);
    }

    /**
     * 静音
     */
    private void muteVoice() {

    }

    /**
     * 暂停开始事件
     */
//    @OnClick(R.id.btn_pause)
    public void controlStartAndPause(){
        if (videoView.isPlaying()){
            btn_pause.setBackgroundResource(R.drawable.btn_video_start_select);
            videoView.pause();
        }else {
            btn_pause.setBackgroundResource(R.drawable.btn_video_pause_select);
            videoView.start();
        }
    }

    /**
     * 下一个视频播放
     */
//    @OnClick(R.id.btn_video_next)
    public void nextVideo(){
        if (medialist!=null&&medialist.size()>0){
            position = (position+1) < medialist.size() ? position + 1 :  position;
            updateVideoView();
        }
    }

    /**
     * 播放上一个视频
     */
    @OnClick(R.id.btn_video_pre)
    public void preVideo(){
        if (medialist != null && medialist.size() > 0){
            position = (position - 1) < 0 ? 0 : position-1;
            updateVideoView();
        }
    }

    /**
     * 更新播放视频以及视频名称
     */
    private void updateVideoView() {
        MediaItem item = medialist.get(position);
        videoView.setVideoPath(item.getPath());
        tv_videoname.setText(item.getName());
        updatecontrolStartAndPause();
    }

    /**
     * 更新视频播放器的next和pre按钮
     */
    private void updatecontrolStartAndPause() {
        btn_next.setBackgroundResource(position == medialist.size()-1 ? R.drawable.btn_next_gray : R.drawable.btn_video_next_select);
        btn_next.setEnabled(position == medialist.size()-1 ? false : true);

        btn_pre.setBackgroundResource(position == 0 ? R.drawable.btn_pre_gray : R.drawable.btn_video_pre_select);
        btn_pre.setEnabled(position == 0 ? false : true);

    }

    /**
     * 设置视频全屏还是默认
     */
    private void setVideoType() {
        setVideoType(SCREEN_TYPE == FULL_SCREEN ? DEFAULT_SCREEN : FULL_SCREEN);
    }


    /**
     * 播放准备好后调用
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        //得到视频的实际的宽与高
        videoHeight = videoView.getHeight();
        videoWidth = videoView.getWidth();
        //开始
        videoView.start();
        int duration = videoView.getDuration();
        video_seekBar.setMax(duration);
        tv_time.setText(utils.stringForTime(duration));
        tv_current_time.setText(utils.stringForTime(0));
        handler.sendEmptyMessage(PROGRESS);
//        videoView.setVideoSize(200, 100);
        SCREEN_TYPE = DEFAULT_SCREEN;
        setVideoType(SCREEN_TYPE);
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
        nextVideo();
    }


    /**
     * 进度改变就有回调
     * @param video_seekBar
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
        handler.removeMessages(HIDE_MEDIACONTROL);
    }

    /**
     * 触摸离开 回调
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROL, 4000);
    }

    /**
     * 自定义BroadcastReceiver
     */
    private class MBatteryBroadcastReceivery extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            img_battery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /**
     * 控制播放器的控制栏显示与隐藏
     */
    private void controlMediaControllerVisible() {
        rl_media_controller.setVisibility(rl_media_controller.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (rl_media_controller.getVisibility() == View.VISIBLE){
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROL, 4000);
        }else if (rl_media_controller.getVisibility() == View.GONE){
            handler.removeMessages(HIDE_MEDIACONTROL);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                moveRange = Math.min(screenHeight, screenWidth);
                curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                float distance = startY - moveY;
                float changeVol = distance * maxVolume / moveRange;
                if (changeVol != 0){
                    isMute = false;
                    updateVoice((int) (changeVol+curVol), isMute);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);

    }

    /**
     * 销毁注销相关数据
     */
    @Override
    protected void onDestroy() {
        if (myBatteryBroadcastReceiver!=null){
            unregisterReceiver(myBatteryBroadcastReceiver);
            myBatteryBroadcastReceiver = null;

        }
        super.onDestroy();
    }

    private class MyVoiceSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                isMute = false;
                updateVoice(progress, isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROL, 4000);
        }
    }

    private void updateVoice(int progress, boolean isMute) {
        if (isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            voice_seekBar.setProgress(0);
        }else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            voice_seekBar.setProgress(progress);
            currentAudio = progress;
        }

    }
}
