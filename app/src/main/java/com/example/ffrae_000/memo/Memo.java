package com.example.ffrae_000.memo;

import java.util.Date;

class Memo implements java.io.Serializable, Comparable<Memo> {

    private final int id;
    private String name;
    private Date date;

    Memo(int id, String name) {
        this.id = id;
        this.name = name;
        this.date = new Date();
    }

    public int compareTo(Memo m) {
        // Sort in reversed order, newest date first
        return date.compareTo(m.getDate()) * (-1);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
