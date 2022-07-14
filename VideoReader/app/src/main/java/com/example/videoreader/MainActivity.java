package com.example.videoreader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import com.util.ImageUtils;

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



    private void  initView(){

        mImageView = findViewById(R.id.iv_preview);
        mBtnStartPlay = findViewById(R.id.btn_start_play);
        mBtnStopPlay = findViewById(R.id.btn_stop_play);
        mBtnStartRecord = findViewById(R.id.btn_start_record);
        mBtnStopRecord = findViewById(R.id.btn_stop_record);

        mBtnStartPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (videoSenderManager.prepareVideo()) {
                    videoSenderManager.startStream("rtmp://10.180.90.38:1935/live/bbb");
                }

                //VideoPresenter.getInstance().startReadVideo("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"/*"/sdcard/testvideo.mp4"*/,30);
                //VideoPresenter.getInstance().startReadVideo("rtsp://192.168.1.249:8554", 30);
                VideoPresenter.getInstance().startReadVideo("rtmp://10.180.90.38:1935/live/aaa", 30);


            }
        });

        mBtnStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VideoPresenter.getInstance().stopReadVideo();
            }
        });


        mBtnStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VideoPresenter.getInstance().startRecordVideo("/sdcard/recordvideo.avi");

            }
        });


        mBtnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VideoPresenter.getInstance().stopRecordVideo();

            }
        });
     }



    public void regVideoCallback(){

        mBitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
        VideoPresenter.getInstance().regVideoCallback(new VideoPresenter.IVideoCallback() {
            @Override
            public void onImageShow() {
                Log.d(TAG, "onImageShow<..");
                showImage();
            }

        }, mBitmap);

    }


    private void showImage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                videoSenderManager.inputYUVData(new Frame(ImageUtils.bitmapToNv21(mBitmap, 480, 640), 0, false, ImageFormat.NV21));

                mImageView.setImageBitmap(mBitmap);
            }
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {



        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
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


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("");
            builder.setMessage("您确定要退出APP?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }



}
