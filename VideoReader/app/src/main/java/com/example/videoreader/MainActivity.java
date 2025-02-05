package com.example.videoreader;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.videolibrary.VideoPresenter;
import com.pedro.encoder.Frame;
import com.pedro.rtmp.utils.ConnectCheckerRtmp;
import com.rtmp.sender.VideoSenderManager;
import com.rtmp.sender.ImageUtils;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";


    private final static int WIDTH = 1280;
    private final static int HEIGHT = 720;

    private ImageView mImageView;
    private Button mBtnStartPlay;
    private Button mBtnStopPlay;
    private Button mBtnStartRecord;
    private Button mBtnStopRecord;


    private Bitmap mBitmap = null;

    private VideoSenderManager videoSenderManager = new VideoSenderManager(new ConnectCheckerRtmp() {
        @Override
        public void onConnectionStartedRtmp(@NonNull String rtmpUrl) {

        }

        @Override
        public void onConnectionSuccessRtmp() {

        }

        @Override
        public void onConnectionFailedRtmp(@NonNull String reason) {

        }

        @Override
        public void onNewBitrateRtmp(long bitrate) {

        }

        @Override
        public void onDisconnectRtmp() {

        }

        @Override
        public void onAuthErrorRtmp() {

        }

        @Override
        public void onAuthSuccessRtmp() {

        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        initView();
        regVideoCallback();

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_A:
                startStream();
                break;
            case KeyEvent.KEYCODE_B:
                stopStream();
                break;
            default:
                break;
        }

        return super.onKeyUp(keyCode, event);
    }

    private void startStream() {
        if (videoSenderManager.prepareVideo()) {
            videoSenderManager.startStream("rtmp://10.180.90.38:1935/live/bbb");
        }

        //VideoPresenter.getInstance().startReadVideo("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"/*"/sdcard/testvideo.mp4"*/,30);
        //VideoPresenter.getInstance().startReadVideo("rtsp://192.168.1.249:8554", 30);
        VideoPresenter.getInstance().startReadVideo("rtmp://10.180.90.38:1935/live/aaa", 30);
    }

    private void stopStream() {
        VideoPresenter.getInstance().stopReadVideo();
        videoSenderManager.stopStream();
    }

    private void  initView(){

        mImageView = findViewById(R.id.iv_preview);
        mBtnStartPlay = findViewById(R.id.btn_start_play);
        mBtnStopPlay = findViewById(R.id.btn_stop_play);
        mBtnStartRecord = findViewById(R.id.btn_start_record);
        mBtnStopRecord = findViewById(R.id.btn_stop_record);

        mBtnStartPlay.setOnClickListener(v -> startStream());

        mBtnStopPlay.setOnClickListener(v -> stopStream());


        mBtnStartRecord.setOnClickListener(v -> VideoPresenter.getInstance().startRecordVideo("/sdcard/recordVideo.avi"));


        mBtnStopRecord.setOnClickListener(v -> VideoPresenter.getInstance().stopRecordVideo());
     }



    public void regVideoCallback(){

        mBitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
        VideoPresenter.getInstance().regVideoCallback(() -> {
            Log.d(TAG, "onImageShow<..");
            videoSenderManager.inputYUVData(new Frame(ImageUtils.bitmapToNv21(mBitmap, 480, 640), 0, false, ImageFormat.NV21));
            showImage();
        }, mBitmap);

    }


    private void showImage(){
        runOnUiThread(() -> {
            mImageView.setImageBitmap(mBitmap);
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

    }




    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBitmap != null){
            mBitmap.recycle();
        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage("您确定要退出APP?");
            builder.setPositiveButton("确定", (dialog, which) -> finish());

            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.show();*/
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



}
