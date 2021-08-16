package com.laizhenghuo.media;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    public static Handler handler;
    String song;
    String singer;
    String album;
    private TextView SongTv,SingerTv;
    private ImageView RebackIv,PlayInPlayIv,PlayPreIv,PlayNextIv;
    List<LocalMusicBean>MDatas;
    int CurrentPlayPosition;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    private boolean isSeekBarChanging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent=getIntent();
        song=intent.getStringExtra("song");
        singer=intent.getStringExtra("singer");
        CurrentPlayPosition=intent.getIntExtra("position",-1);
        SongTv=findViewById(R.id.tv_play_song);
        SingerTv=findViewById(R.id.tv_play_singer);
        SongTv.setText(song);
        SingerTv.setText(singer);
//        initView();
    }

//    private void initView() {
//        SongTv=findViewById(R.id.tv_play_song);
//        SingerTv=findViewById(R.id.tv_play_singer);
//        RebackIv=findViewById(R.id.iv_reback);
//        PlayInPlayIv=findViewById(R.id.iv_playinplay);
//        PlayPreIv=findViewById(R.id.iv_music_play_pre);
//        PlayNextIv=findViewById(R.id.iv_music_next);
//        seekBar=findViewById(R.id.local_music_bottom_seekbar);
//        SongTv.setText(song);
//        SingerTv.setText(singer);
//        RebackIv.setOnClickListener(this);
//        PlayPreIv.setOnClickListener(this);
//        PlayInPlayIv.setOnClickListener(this);
//        PlayNextIv.setOnClickListener(this);
//    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_reback:
//                Intent intent=new Intent(MainActivity2.this,MainActivity.class);
//                startActivity(intent);
//            case R.id.iv_music_play_pre:
//        }

    }

//    public class seekbarchange implements SeekBar.OnSeekBarChangeListener{
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            isSeekBarChanging=true;
//
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            isSeekBarChanging=false;
//            mediaPlayer.seekTo(seekBar.getProgress());
//
//        }
//    }

}