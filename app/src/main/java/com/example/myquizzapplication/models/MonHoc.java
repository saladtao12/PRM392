package com.example.myquizzapplication.models;

public class MonHoc {
    private int id;
    private String tenMon;

    public MonHoc(int id, String tenMon) {
        this.id = id;
        this.tenMon = tenMon;
    }

    public int getId() {
        return id;
    }

    public String getTenMon() {
        return tenMon;
    }
}
