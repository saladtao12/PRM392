package com.example.myquizzapplication.models;

public class CauHoi {
    private int id;
    private int monHocId;
    private String noiDung;
    private String luaChonA, luaChonB, luaChonC, luaChonD;
    private String dapAnDung;

    public CauHoi(int id, int monHocId, String noiDung,
                  String luaChonA, String luaChonB, String luaChonC, String luaChonD, String dapAnDung) {
        this.id = id;
        this.monHocId = monHocId;
        this.noiDung = noiDung;
        this.luaChonA = luaChonA;
        this.luaChonB = luaChonB;
        this.luaChonC = luaChonC;
        this.luaChonD = luaChonD;
        this.dapAnDung = dapAnDung;
    }

    public int getId() { return id; }
    public int getMonHocId() { return monHocId; }
    public String getNoiDung() { return noiDung; }
    public String getLuaChonA() { return luaChonA; }
    public String getLuaChonB() { return luaChonB; }
    public String getLuaChonC() { return luaChonC; }
    public String getLuaChonD() { return luaChonD; }
    public String getDapAnDung() { return dapAnDung; }
}
