package com.example.ffrae_000.memo;

import android.os.Environment;

import java.io.File;

class AudioMemo extends Memo {
    private File data;

    AudioMemo(int id, String name) {
        super(id, name);
        if (Utilities.externalStoragecheck()) {
            data = new File(Environment.getExternalStorageDirectory() + "/MEMO/" + name + id + ".3gpp");
        } else {
            data = new File(Environment.getDataDirectory().getPath() + "/MEMO/" + name + id + ".3gpp");
        }
    }

    public File getData() {
        return data;
    }

    public void setData(File path) {
        data = path;
    }
}
