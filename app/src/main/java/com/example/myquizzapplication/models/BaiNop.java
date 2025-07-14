package com.example.myquizzapplication.models;

public class BaiNop {
    private int id;
    private int nguoiDungId;
    private int monHocId;
    private double diem;
    private String ngayNop;

    public BaiNop(int id, int nguoiDungId, int monHocId, double diem, String ngayNop) {
        this.id = id;
        this.nguoiDungId = nguoiDungId;
        this.monHocId = monHocId;
        this.diem = diem;
        this.ngayNop = ngayNop;
    }

    public int getId() { return id; }
    public int getNguoiDungId() { return nguoiDungId; }
    public int getMonHocId() { return monHocId; }
    public double getDiem() { return diem; }
    public String getNgayNop() { return ngayNop; }
}
