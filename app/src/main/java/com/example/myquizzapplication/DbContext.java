package com.example.myquizzapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbContext extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz_app.db";
    private static final int DATABASE_VERSION = 1;

    public DbContext(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE nguoi_dung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "mat_khau TEXT NOT NULL);");

        db.execSQL("CREATE TABLE mon_hoc (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten_mon TEXT NOT NULL);");

        db.execSQL("CREATE TABLE cau_hoi_theo_mon (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mon_hoc_id INTEGER NOT NULL," +
                "noi_dung TEXT NOT NULL," +
                "lua_chon_a TEXT NOT NULL," +
                "lua_chon_b TEXT NOT NULL," +
                "lua_chon_c TEXT NOT NULL," +
                "lua_chon_d TEXT NOT NULL," +
                "dap_an_dung TEXT NOT NULL," +
                "FOREIGN KEY(mon_hoc_id) REFERENCES mon_hoc(id));");

        db.execSQL("CREATE TABLE bai_nop (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nguoi_dung_id INTEGER NOT NULL," +
                "mon_hoc_id INTEGER NOT NULL," +
                "diem REAL NOT NULL," +
                "ngay_nop TEXT NOT NULL," +
                "FOREIGN KEY(nguoi_dung_id) REFERENCES nguoi_dung(id)," +
                "FOREIGN KEY(mon_hoc_id) REFERENCES mon_hoc(id));");

        db.execSQL("CREATE TABLE chi_tiet_bai_nop (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bai_nop_id INTEGER NOT NULL," +
                "cau_hoi_id INTEGER NOT NULL," +
                "dap_an_chon TEXT NOT NULL," +
                "FOREIGN KEY(bai_nop_id) REFERENCES bai_nop(id)," +
                "FOREIGN KEY(cau_hoi_id) REFERENCES cau_hoi_theo_mon(id));");
        db.execSQL("INSERT INTO nguoi_dung (ten, email, mat_khau) VALUES " +
                "('Nguyễn Văn A', 'a@gmail.com', 'a')," +
                "('Trần Thị B', 'b@gmail.com', '123456')," +
                "('Lê Văn C', 'c@gmail.com', '123456');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS chi_tiet_bai_nop");
        db.execSQL("DROP TABLE IF EXISTS bai_nop");
        db.execSQL("DROP TABLE IF EXISTS cau_hoi_theo_mon");
        db.execSQL("DROP TABLE IF EXISTS mon_hoc");
        db.execSQL("DROP TABLE IF EXISTS nguoi_dung");
        onCreate(db);
    }
}
