package com.example.myquizzapplication.models;

public class NguoiDung {
    public int id;
    public String ten;
    public String email;
    public String matKhau;

    public NguoiDung(int id, String ten, String email, String matKhau) {
        this.id = id;
        this.ten = ten;
        this.email = email;
        this.matKhau = matKhau;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }
}
