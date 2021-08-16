package com.laizhenghuo.media;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Build.VERSION_CODES.P;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView nextiv, playiv, preiv, albumIv;
    TextView singertv, songtv;
    RecyclerView musicRv;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    //数据源
    List<LocalMusicBean> mDatas;
    private LocalMusicAdapter adapter;
    //记录当前正在播放的音乐的位置
    int currentPlayPosition = -1;
    int currentPausePositionInSong = -0;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    int currentPositionOfSong;
    private Timer timer;
    String song;
    String singer;
    private ObjectAnimator animator;
    RecyclerView mRecyclerView;
    private AudioManager audioManager;
    private boolean PauseByMe=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer = new MediaPlayer();
        mDatas = new ArrayList<>();
        initView();
        //创建适配器
        adapter = new LocalMusicAdapter(this, mDatas);
        musicRv.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);
        //加载本地数据源
        loadlocalMusicData();
        //设置每一项的点击事件
        setEventListener();
        registerHeadsetPlugReceiver();

    }


    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                pauseMusic();
            }
        }

    };
    public void setEventListener() {
        //设置每一项的点击事件
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                LocalMusicBean musicBean = mDatas.get(position);
                playMusicInMusicBean(musicBean);
                currentPlayPosition = position;
                song = musicBean.getSong();
                singer = musicBean.getSinger();
//                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
//                intent.putExtra("position",currentPlayPosition);
//                intent.putExtra("song",song);
//                intent.putExtra("singer",singer);
//                startActivity(intent);
            }
        });
    }


    public void playMusicInMusicBean(LocalMusicBean musicBean) {
        //根据传入对象播放音乐
        //设置底部显示的歌手名和歌曲名
        singertv.setText(musicBean.getSinger());
        songtv.setText(musicBean.getSong());
        stopMusic();
        //重置多媒体播放器
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            String albumArt = musicBean.getAlbumArt();
            Log.i("lsh123", "playMusicInMusicBean: albumpath==" + albumArt);
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            Log.i("lsh123", "playMusicInMusicBean: bm==" + bm);
            albumIv.setImageResource(R.mipmap.album);
            PlayMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*点击播放按钮播放音乐，或者从暂停的地方开始播放
     */
    public void PlayMusic() {
        //播放音乐的函数
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            //判断是否播放状态
            if (currentPausePositionInSong == 0) {
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    animator.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    animator.resume();
                }
            }
            seekBar.setMax(mediaPlayer.getDuration());
            timer = new Timer();
            timer.schedule(new TimerTask() {
                Runnable updateUI = new Runnable() {
                    @Override
                    public void run() {

                    }
                };

                public void run() {
                    if (!isSeekBarChanging) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        currentPositionOfSong = mediaPlayer.getCurrentPosition();
                        runOnUiThread(updateUI);
                    }
                }
            }, 0, 50);
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();
                }
            });
            playiv.setImageResource(R.mipmap.z);
            PauseByMe=false;
        }
    }

    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.pause();
                playiv.setImageResource(R.mipmap.bf);
            }

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = false;
            mediaPlayer.seekTo(seekBar.getProgress());
            currentPausePositionInSong = seekBar.getProgress();
            PlayMusic();
            playiv.setImageResource(R.mipmap.z);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.resume();
            }
        }
    }


    public void next() {
        currentPlayPosition = new Random().nextInt(mDatas.size());
        LocalMusicBean nextBean = mDatas.get(currentPlayPosition);
        playMusicInMusicBean(nextBean);

    }

    public void pre() {
        currentPlayPosition = new Random().nextInt(mDatas.size());
        currentPlayPosition = currentPlayPosition - 1;
        LocalMusicBean preBean = mDatas.get(currentPlayPosition);
        playMusicInMusicBean(preBean);

    }

    public void pauseMusic() {
        //暂停音乐的函数
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.pause();
            }
            playiv.setImageResource(R.mipmap.bf);
            PauseByMe=true;
        }
    }
    public void pauseMusicByFocus() {
        //暂停音乐的函数
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animator.pause();
            }
            playiv.setImageResource(R.mipmap.bf);
        }
    }

    public void stopMusic() {
        //停止音乐的函数
        if (mediaPlayer != null) {
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            playiv.setImageResource(R.mipmap.bf);
        }

    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        timer.cancel();
        timer = null;
        mediaPlayer = null;
        super.onDestroy();
        stopMusic();
    }

    public void loadlocalMusicData() {
        //获取权限
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasReadSmsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasReadSmsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        /*加载本地存储当中的音乐MP3文件到集合当中
        1.获取ContentResolver对象
        */
        ContentResolver resolver = getContentResolver();
        //获取本地音乐存储的地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //查询地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        //4.遍历cursor对象
        int id = 1;
        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String sid = String.valueOf(id);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String album_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String albumArt = getAlbumArt(album_id);
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(duration));
            //将一行中的数据封装到对象中
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time, path, size, albumArt);
            if (size.longValue() > 1000 * 800) {
                id++;
                mDatas.add(bean);
            }
        }
        //提示adapter更新
        adapter.notifyDataSetChanged();
    }

    private String getAlbumArt(String album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = this.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + album_id),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChange = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    //当其他应用申请焦点之后又释放焦点会触发此回调
                    //可重新播放音乐
                    Log.e("lai", "用了这个AUDIOFOCUS_GAIN方法");
                    if (!mediaPlayer.isPlaying()&&!PauseByMe==true){
                        PlayMusic();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //长时间丢失焦点,当其他应用申请的焦点为 AUDIOFOCUS_GAIN 时，
                    //会触发此回调事件，例如播放 QQ 音乐，网易云音乐等
                    //通常需要暂停音乐播放，若没有暂停播放就会出现和其他音乐同时输出声音
                    //释放焦点，该方法可根据需要来决定是否调用
                    //若焦点释放掉之后，将不会再自动获得
                    Log.e("lai", "用了这个AUDIOFOCUS_LOSS方法");
                    Log.e("lai", String.valueOf(mediaPlayer.getCurrentPosition()));
                    audioManager.abandonAudioFocus(mAudioFocusChange);
                    pauseMusic();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //短暂性丢失焦点，当其他应用申请 AUDIOFOCUS_GAIN_TRANSIENT 或
//                    AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE 时，
                    //会触发此回调事件，例如播放短视频，拨打电话等。
                    //通常需要暂停音乐播放
                    pauseMusicByFocus();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //短暂性丢失焦点并作降音处理
                    break;
            }
        }


    };

    private void initView() {
        //初始化控件
        nextiv = findViewById(R.id.local_music_bottom_iv_next);
        preiv = findViewById(R.id.local_music_bottom_iv_pre);
        playiv = findViewById(R.id.local_music_bottom_iv_play);
        singertv = findViewById(R.id.local_music_bottom_tv_singer);
        songtv = findViewById(R.id.local_music_bottom_tv_song);
        musicRv = findViewById(R.id.local_music_rv);
        albumIv = findViewById(R.id.albumIv);
        seekBar = findViewById(R.id.local_music_bottom_seekbar);
        mRecyclerView = findViewById(R.id.local_music_rv);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());
        nextiv.setOnClickListener(this);
        preiv.setOnClickListener(this);
        playiv.setOnClickListener(this);
        animator = ObjectAnimator.ofFloat(albumIv, "rotation", 0, 360.0F);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.local_music_bottom_iv_pre:
                if (currentPlayPosition == -1) {
                    //并没有播放音乐
                    Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    pre();
                }
                break;
            case R.id.local_music_bottom_iv_next:
                next();
                break;
            case R.id.local_music_bottom_iv_play:
                if (currentPlayPosition == -1) {
                    //并没有播放音乐
                    Toast.makeText(this, "请选择要播放的音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    currentPositionOfSong = mediaPlayer.getCurrentPosition();
                    //此时处于播放状态，点击后应该暂停
                    pauseMusic();
                    timer.purge();
                } else {
                    //此时没有播放音乐，点击开始播放音乐
                    PlayMusic();
                }
                break;
            case R.id.albumIv:


        }

    }

}

