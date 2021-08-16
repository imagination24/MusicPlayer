package com.laizhenghuo.media;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
      return new MusicControl();
    }
    public class MusicControl extends Binder{
        public void play(int i){//String path
            Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/"+"music"+i);
            try{
                mediaPlayer.reset();//重置音乐播放器
                //加载多媒体文件
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();//播放音乐
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        public void pausePlay(){
            mediaPlayer.pause();//暂停播放音乐
        }
        public void continuePlay(){
            mediaPlayer.start();//继续播放音乐
        }
        public void seekTo(int progress){
            mediaPlayer.seekTo(progress);//设置音乐的播放位置
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer==null) return;
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();//停止播放音乐
        mediaPlayer.release();//释放占用的资源
        mediaPlayer=null;//将player置为空
    }
}