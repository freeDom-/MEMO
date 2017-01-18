package com.example.ffrae_000.memo;

import java.util.Date;

public class Memo implements java.io.Serializable, Comparable<Memo> {
    
    private int id;
    private String name;
    private Date date;

    Memo(int id, String name) {
        this.id = id;
        this.name = name;
        this.date = new Date();
    }

    public int compareTo(Memo m) {
        return date.compareTo(m.getDate());
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
