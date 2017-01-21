package com.example.ffrae_000.memo;


class TextMemo extends Memo {

    private String data = "";

    TextMemo(int id, String name) {
        super(id, name);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
