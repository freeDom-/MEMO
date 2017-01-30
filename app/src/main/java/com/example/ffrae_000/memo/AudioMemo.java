package com.example.ffrae_000.memo;

import java.io.File;

class AudioMemo extends Memo {
    private File data;

    AudioMemo(int id, String name) {
        super(id, name);
    }

    public File getData() {
        return data;
    }

    public void setFileDir(String path) {
        data = new File(path + File.separator + super.getName() + super.getId() + ".3gpp");
    }
}
