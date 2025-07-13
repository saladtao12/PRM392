package com.example.myquizzapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbContext extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz_app_v2.db";
    private static final int DATABASE_VERSION = 2;

    public DbContext(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public Cursor rawQuery(String query, String[] args) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, args);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE nguoi_dung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "mat_khau TEXT NOT NULL);");

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

        // 1. Người dùng
        db.execSQL("INSERT INTO nguoi_dung (ten, email, mat_khau) VALUES " +
                "('Nguyễn Văn A', 'a@gmail.com', 'a')," +
                "('Trần Thị B', 'b@gmail.com', '123456')," +
                "('Lê Văn C', 'c@gmail.com', '123456');");
        // 2. Môn học
        db.execSQL("INSERT INTO mon_hoc (ten_mon) VALUES " +
                "('Toán')," +
                "('Lịch sử');");

        // 3. Câu hỏi cho môn Toán (id = 1)
        db.execSQL("INSERT INTO cau_hoi_theo_mon (mon_hoc_id, noi_dung, lua_chon_a, lua_chon_b, lua_chon_c, lua_chon_d, dap_an_dung) VALUES " +
                "(1, '2 + 2 = ?', '3', '4', '5', '6', 'B')," +
                "(1, '5 * 3 = ?', '15', '20', '10', '8', 'A');");

        // 4. Câu hỏi cho môn Lịch sử (id = 2)
        db.execSQL("INSERT INTO cau_hoi_theo_mon (mon_hoc_id, noi_dung, lua_chon_a, lua_chon_b, lua_chon_c, lua_chon_d, dap_an_dung) VALUES " +
                "(2, 'Ngày quốc khánh Việt Nam?', '2/9', '1/5', '30/4', '1/1', 'A')," +
                "(2, 'Chủ tịch Hồ Chí Minh sinh năm nào?', '1890', '1900', '1885', '1895', 'A');");

        // 5. Bài nộp giả lập cho user 1 làm môn Toán
        db.execSQL("INSERT INTO bai_nop (nguoi_dung_id, mon_hoc_id, diem, ngay_nop) VALUES " +
                "(1, 1, 10.0, '2025-07-08');");

        // 6. Chi tiết bài nộp (bài_nop_id = 1)
        db.execSQL("INSERT INTO chi_tiet_bai_nop (bai_nop_id, cau_hoi_id, dap_an_chon) VALUES " +
                "(1, 1, 'B')," + // đúng
                "(1, 2, 'A');");  // đúng

        db.execSQL("INSERT INTO bai_nop (bai_nop_id, cau_hoi_id, dap_an_chon) VALUES " +
                "(1, 1, 'B')," + // đúng
                "(1, 2, 'A');");  // đúng
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
