package com.laizhenghuo.media;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utils {
    public static ArrayList<com.laizhenghuo.media.LrcMusic> redLrc(InputStream in) {
        ArrayList<com.laizhenghuo.media.LrcMusic> alist = new ArrayList<com.laizhenghuo.media.LrcMusic>();
        //File f = new File(path.replace(".mp3", ".lrc"));
        try {
            //FileInputStream fs = new FileInputStream(f);
            InputStreamReader input = new InputStreamReader(in, "utf-8");
            BufferedReader br = new BufferedReader(input);
            String s = "";

            while ((s = br.readLine()) != null) {
                if (!TextUtils.isEmpty(s)) {
                    String lyLrc = s.replace("[", "");
                    String[] data_ly = lyLrc.split("]");
                    if (data_ly.length > 1) {
                        String time = data_ly[0];
                        String lrc = data_ly[1];
                        com.laizhenghuo.media.LrcMusic lrcMusic = new com.laizhenghuo.media.LrcMusic(lrcData(time), lrc);
                        alist.add(lrcMusic);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alist;
    }

    public static int lrcData(String time) {
        time = time.replace(":", "#");
        time = time.replace(".", "#");

        String[] mTime = time.split("#");

        //[03:31.42]
        int mtime = Integer.parseInt(mTime[0]);
        int stime = Integer.parseInt(mTime[1]);
        int mitime = Integer.parseInt(mTime[2]);

        int ctime = (mtime * 60 + stime) * 1000 + mitime * 10;

        return ctime;
    }
}
