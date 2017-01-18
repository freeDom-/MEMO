package com.example.ffrae_000.memo;


import android.media.MediaRecorder;

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
