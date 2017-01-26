package com.example.ffrae_000.memo;

import java.io.File;

class AudioMemo extends Memo {
    private File data;
    private String dir = null;
    private String n;
    private int i;

    AudioMemo(int id, String name) {
        super(id, name);
        n = name;
        i = id;
    }

    public File getData() {
        return data;
    }

    public void setFileDir(String path) {
        dir = path;
        data = new File(dir + File.separator + n + i + ".3gpp");
    }
}
