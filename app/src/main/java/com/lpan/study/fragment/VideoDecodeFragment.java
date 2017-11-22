package com.lpan.study.fragment;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.lpan.study.context.AppContext;
import com.lpan.study.fragment.base.BaseFragment;
import com.lpan.study.model.FFmpegVideoInfo;
import com.lpan.study.utils.Utils;
import com.lpan.study.view.TextureVideoView;
import com.lpan.study.view.UnclickSeekBar;
import com.lpan.R;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lpan on 2017/2/7.
 */

public class VideoDecodeFragment extends BaseFragment implements View.OnClickListener, TextureVideoView.OnStateChangeListener, SeekBar.OnSeekBarChangeListener {

    private TextureVideoView mTextureVideoView;

    private ImageView mPlayButton;

    private UnclickSeekBar mSeekBar;

    private TextView mTimeRecord;

    private TextView mTransform;

    private TextView mHfilp;

    private TextView mAddWaterMask;

    private static final String PATH = "video.mp4";

    private static final String PATH2 = "front_record_video.mp4";

    private static final String IMAGE = "image.png";

    private static final String URL = "http://svideo.spriteapp.com/video/2016/0703/7b5bc740-4134-11e6-ac2b-d4ae5296039d_wpd.mp4";

    private static final String PICTURE = "avatar.png";

    private static final String SIZE_1920_1080 = "1920:1080";

    private static final String SIZE_1280_720 = "1280:720";

    private static final String SIZE_480_640 = "480:640";

    private static final String SIZE_360_640 = "360:640";

    private String mVideoPath;

    private FFmpegVideoInfo mFFmpegVideoInfo;


    private Timer mTimer;

    private TimerTask mTimerTask;

    static final Handler mHandler = new Handler(Looper.getMainLooper()) {
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_video_play;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FFmpeg.getInstance(AppContext.getContext()).loadBinary(null);
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTextureVideoView != null && mTextureVideoView.isPlaying()) {
            mTextureVideoView.stop();
        }

        FFmpeg.getInstance(AppContext.getContext()).killRunningProcesses();
        FFmpeg.clearInstance();
    }

    private FFmpegVideoInfo getVideoMsg(String content) {
        FFmpegVideoInfo videoInfo = new FFmpegVideoInfo();
        String[] split = content.split("\n");
        for (String str : split) {
            if (str.contains("Stream") && str.contains("Video:")) {
                String[] split1 = str.split(", ");
                for (String s : split1) {
                    if (s.endsWith("kb/s")) {
                        String[] split2 = s.split(" kb/s");
                        videoInfo.setBitrate(Integer.parseInt(split2[0]));
                    }
                    if (s.contains("x")) {
                        //1080x1920 [SAR 1:1 DAR 9:16]
                        // 或者1080x1920
                        // 或者Stream #0:0(eng): Video: h264 (High) (avc1 / 0x31637661)
                        if (s.contains(" ")) {
                            String[] split2 = s.split(" ");
                            for (String s1 : split2) {
                                if (s1.matches("[\\d]{3,4}x[\\d]{3,4}")) {
                                    String[] xes = s1.split("x");
                                    videoInfo.setWidth(Integer.parseInt(xes[0]));
                                    videoInfo.setHeight(Integer.parseInt(xes[1]));
                                }
                            }
                        } else {
                            if (s.matches("[\\d]{3,4}x[\\d]{3,4}")) {
                                String[] xes = s.split("x");
                                videoInfo.setWidth(Integer.parseInt(xes[0]));
                                videoInfo.setHeight(Integer.parseInt(xes[1]));
                            }
                        }
                    }
                }
            }
            if (str.contains("Duration:")) {
                // Duration: 00:00:05.31, start: 0.000000, bitrate: 17222 kb/s
                String[] duration = str.split(", ");
                if (!TextUtils.isEmpty(duration[0]) && duration[0].contains("Duration")) {
                    String[] time = duration[0].split(": ");
                    String[] time2 = time[1].split(":");

                    double hour = Double.parseDouble(time2[0]);
                    double minute = Double.parseDouble(time2[1]);
                    double second = Double.parseDouble(time2[2]);

                    double total = (hour * 60 + minute) * 60 + second;
                    videoInfo.setDuration(total);
                }

            }

            if (str.contains("rotate")) {
                String[] split1 = str.split(": ");
                videoInfo.setRotate(Integer.parseInt(split1[1]));
            }
        }
        android.util.Log.d("lp-test", videoInfo.toString());
        return videoInfo;
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);

        mTextureVideoView = (TextureVideoView) view.findViewById(R.id.textureview);
        mSeekBar = (UnclickSeekBar) view.findViewById(R.id.seekbar);
        mPlayButton = (ImageView) view.findViewById(R.id.play_button);
        mTimeRecord = (TextView) view.findViewById(R.id.duration);
        mTransform = (TextView) view.findViewById(R.id.button1);

        mHfilp = (TextView) view.findViewById(R.id.hflip);
        mAddWaterMask = (TextView) view.findViewById(R.id.add_water_mask);
        mTextureVideoView.setVideoMode(TextureVideoView.CENTER_MODE);

        mTextureVideoView.setOnStateChangeListener(this);
        mPlayButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mTransform.setOnClickListener(this);
        mHfilp.setOnClickListener(this);
        mAddWaterMask.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private void initTimeTask() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //设置时间
                        mTimeRecord.setText(String.valueOf(Utils.converLongTimeToStr(mTextureVideoView.getCurrentPosition()) + " / " + Utils.converLongTimeToStr(mTextureVideoView.getDuration())));
                        //进度条
                        int progress = mTextureVideoView.getCurrentPosition();
                        mSeekBar.setProgress(progress);
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private void destroyTimeTask() {
        if (mTimer != null && mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public File getVideoFileDir() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" );
        if (dir != null && dir.exists()) {
            return dir;
        }
        return null;
    }

    public String getVideoPath() {
        return getVideoFileDir().getAbsolutePath() + File.separator + PATH;
    }

    private void analysisVideo(final String path) {
        String[] cmd = new String[]{"-i", path};
        try {
            FFmpeg.getInstance(AppContext.getContext()).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d("lp-test", "   onSuccess" + message);
                }

                @Override
                public void onProgress(String message) {
                    Log.d("lp-test", "   onProgress" + message);

                }

                @Override
                public void onFailure(String message) {
                    Log.d("lp-test", "   onFailure" + message);
                    mFFmpegVideoInfo = getVideoMsg(message);
                    transformVideo(mFFmpegVideoInfo, path);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    private void hflipVideo(String path) {
        String out = "/storage/emulated/0/Jiemoapp/111.mp4";
        String[] cmd = new String[]{"-i", path, "-vf", "hflip", "-preset", "superfast", out};
//        String[] cmd = new String[]{"-filters"};
        final long startTime = System.currentTimeMillis();
        try {
            FFmpeg.getInstance(AppContext.getContext()).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d("VideoRecordFragment", "onSuccess----" + message);

                }

                @Override
                public void onProgress(String message) {
                    Log.d("VideoRecordFragment", "onProgress----" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.d("VideoRecordFragment", "onFailure----" + message);
                }

                @Override
                public void onStart() {
                    Log.d("VideoRecordFragment", "onStart----");
                }

                @Override
                public void onFinish() {
                    long cost = System.currentTimeMillis() - startTime;
                    Log.d("VideoRecordFragment", "onFinish----cost= " + cost);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("VideoRecordFragment", "FFmpegCommandAlreadyRunningException----" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void transformVideo(FFmpegVideoInfo fFmpegVideoInfo, String path) {
        if (fFmpegVideoInfo == null) {
            return;
        }

        File out = new File(getVideoFileDir(), System.currentTimeMillis() + ".mp4");
        final long startTime = System.currentTimeMillis();
        String[] cmd = null;

        int width = fFmpegVideoInfo.getWidth();
        int height = fFmpegVideoInfo.getHeight();
        int rotate = fFmpegVideoInfo.getRotate();
        int bitrate = fFmpegVideoInfo.getBitrate();

        String vf = "";
        String vb = "";
        if (width > 960) {
            if (rotate == 90 || rotate == 270) {
                vf = "scale=-2:960";
            } else {
                vf = "scale=960:-2";
            }
        } else if (height > 720) {
            if (rotate == 90 || rotate == 270) {
                vf = "scale=720:-2";
            } else {
                vf = "scale=-2:720";
            }
        }

        if (bitrate > 800) {
            vb = "800k";
        }
        if (TextUtils.isEmpty(vf) && TextUtils.isEmpty(vb)) {
            Log.d("lp-test", "transformVideo  1  " + fFmpegVideoInfo.toString());
            return;
        } else if (!TextUtils.isEmpty(vf) && !TextUtils.isEmpty(vb)) {
            cmd = new String[]{"-i", path, "-vf", vf, "-vb", vb, "-r", "24", "-preset", "superfast", "-ab", "96k", "-fs", "10M", out.getAbsolutePath(), "-debug", "v"};
            Log.d("lp-test", "transformVideo  2  " + fFmpegVideoInfo.toString());


        } else if (!TextUtils.isEmpty(vf) && TextUtils.isEmpty(vb)) {
            cmd = new String[]{"-i", path, "-vf", vf, "-r", "24", "-preset", "superfast", "-ab", "96k", "-fs", "10M", out.getAbsolutePath(), "-debug", "v"};
            Log.d("lp-test", "transformVideo  3  " + fFmpegVideoInfo.toString());


        } else if (TextUtils.isEmpty(vf) && !TextUtils.isEmpty(vb)) {
            cmd = new String[]{"-i", path, "-vb", vb, "-r", "24", "-preset", "superfast", "-ab", "96k", "-fs", "10M", out.getAbsolutePath(), "-debug", "v"};
            Log.d("lp-test", "transformVideo  4  " + fFmpegVideoInfo.toString());

        }

        try {
            FFmpeg.getInstance(AppContext.getContext()).execute(cmd, new FFmpegExecuteResponseHandler() {

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(AppContext.getContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                    Log.d("lp-test", "   onSuccess  " + message);

                }

                @Override
                public void onProgress(String message) {
                    Log.d("lp-test", "   onProgress  " + message);

                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(AppContext.getContext(), "onFailure", Toast.LENGTH_SHORT).show();
                    Log.d("lp-test", "   onFailure  " + message);

                }

                @Override
                public void onStart() {
                    Toast.makeText(AppContext.getContext(), "onStart", Toast.LENGTH_SHORT).show();
                    Log.d("lp-test", "   onStart  ");

                }

                @Override
                public void onFinish() {
                    long costTime = System.currentTimeMillis() - startTime;

                    Log.d("lp-test", "   onFinish  cost time   " + Utils.converLongTimeToStr(costTime));

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play_button:
                if (mTextureVideoView.isPlaying()) {
                    mTextureVideoView.pause();
                } else {
                    String local = getVideoPath();
                    mTextureVideoView.setPathFromAssets();
//                    mTextureVideoView.setPath(local, false, 1);
                    mPlayButton.setVisibility(View.GONE);
                }

                break;

            case R.id.button1:
                analysisVideo(getVideoPath());
                break;

            case R.id.hflip:
                hflipVideo(getVideoFileDir().getAbsolutePath() + File.separator + PATH2);
                break;

            case R.id.add_water_mask:
                addWatermask(getVideoFileDir().getAbsolutePath() + File.separator + PATH, getVideoFileDir().getAbsolutePath() + File.separator + IMAGE);
                break;
        }
    }

    private void addWatermask(String videoPath, String imagePath) {
        final File out = new File(getVideoFileDir(), System.currentTimeMillis() + ".mp4");

        String[] cmd = new String[]{"-i", videoPath, "-i", imagePath, "-filter_complex", "overlay=0:0", "-preset", "superfast", "-movflags", "-faststart", "-b:v", "1024k", "-g", "30", out.getAbsolutePath()};
        try {
            final long time = SystemClock.elapsedRealtime();
            FFmpeg.getInstance(AppContext.getContext())
                    .execute(cmd, new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {

                            Log.i("lp-test", "onSuccess       message = " + message + "   cost = " + (SystemClock.elapsedRealtime() - time));
                            Log.i("lp-test", "onSuccess       path = " + out.getAbsolutePath());

                        }

                        @Override
                        public void onProgress(String message) {
                            Log.i("lp-test", "onProgress       message = " + message);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.i("lp-test", "onFailure       message = " + message);
                        }

                        @Override
                        public void onStart() {
                            Log.i("lp-test", "onStart");
                        }

                        @Override
                        public void onFinish() {
                            Log.i("lp-test", "onFinish" + "   cost = " + (SystemClock.elapsedRealtime() - time));
                            FFmpeg.clearInstance();
                        }
                    });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
            Log.i("lp-test", "onSuccess       message = " + e.getMessage());
        }

    }

    @Override
    public void onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mTextureVideoView.pause();
        destroyTimeTask();
    }

    @Override
    public void onBuffering() {

    }

    @Override
    public void onPlaying() {
        initTimeTask();
        mSeekBar.setMax(mTextureVideoView.getDuration());
        mPlayButton.setVisibility(View.GONE);

    }

    @Override
    public void onSeek(int max, int progress) {
    }

    @Override
    public void onTextureViewAvaliable() {
    }

    @Override
    public void playFinish() {
        mTextureVideoView.pause();
        mPlayButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onVideoSizeChanged(int vWidth, int vHeight) {
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTextureVideoView.getLayoutParams();
////        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTextureVideoView.getLayoutParams();
//        params.width = getResources().getDisplayMetrics().widthPixels;
//        params.height = (int) ((float) params.width / (float) vWidth * (float) vHeight);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int after = seekBar.getProgress();
        int duration = mTextureVideoView.getDuration();
        double percent = after * 0.01;
        int msec = (int) (percent * duration);
        Log.d("lp-test", " -- msec=" + msec);
        mTimeRecord.setText(String.valueOf(Utils.converLongTimeToStr(mTextureVideoView.getCurrentPosition()) + " / " + Utils.converLongTimeToStr(mTextureVideoView.getDuration())));

        mTextureVideoView.seekTo(after);
        if (!mTextureVideoView.isPlaying()) {
            mTextureVideoView.start();
            mPlayButton.setVisibility(View.GONE);
        }
    }
}