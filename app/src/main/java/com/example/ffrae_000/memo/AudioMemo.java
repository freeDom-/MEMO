package com.example.ffrae_000.memo;


import android.os.Environment;

class AudioMemo extends Memo {
    private String dir = Environment.getExternalStorageDirectory() + "/MEMO/";
    private String path;

    AudioMemo(int id, String name) {
        super(id, name);
        path = dir + name + id + ".3gpp";
    }

    public String getPath() {
        return path;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
