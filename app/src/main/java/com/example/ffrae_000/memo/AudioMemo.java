package com.example.ffrae_000.memo;


import android.media.MediaRecorder;
import android.media.MediaPlayer;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class AudioMemo extends Memo {
    private MediaRecorder mRecorder = null;
    private String dir = "/SD-Karte/Media/audio/MEMO-audio/";
    private String path;
    AudioMemo(int id, String name){
        super(id, name);
        path = dir+name+id+ ".3GPP";
    }

    public String getPath() {
        return path;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
