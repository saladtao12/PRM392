package com.example.myquizzapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbContext extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz_app.db";
    private static final int DATABASE_VERSION = 2; // Tăng version từ 1 lên 2

    public DbContext(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public Cursor rawQuery(String query, String[] args) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query, args);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng nguoi_dung với các cột mới
        db.execSQL("CREATE TABLE nguoi_dung (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ten TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "mat_khau TEXT," + // Không NOT NULL vì Google user không có password
                "google_id TEXT," +
                "loai_dang_nhap TEXT DEFAULT 'NORMAL');"); // 'NORMAL' hoặc 'GOOGLE'

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

        // 1. Người dùng (cập nhật với loai_dang_nhap)
        db.execSQL("INSERT INTO nguoi_dung (ten, email, mat_khau, loai_dang_nhap) VALUES " +
                "('Nguyễn Văn A', 'a@gmail.com', 'a', 'NORMAL')," +
                "('Trần Thị B', 'b@gmail.com', '123456', 'NORMAL')," +
                "('Lê Văn C', 'c@gmail.com', '123456', 'NORMAL');");

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

  
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Cập nhật từ version 1 lên 2
            try {
                // Thêm cột google_id
                db.execSQL("ALTER TABLE nguoi_dung ADD COLUMN google_id TEXT;");

                // Thêm cột loai_dang_nhap
                db.execSQL("ALTER TABLE nguoi_dung ADD COLUMN loai_dang_nhap TEXT DEFAULT 'NORMAL';");

                // Cập nhật cột mat_khau để có thể null
                // Tạo bảng tạm thời
                db.execSQL("CREATE TABLE nguoi_dung_temp (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "ten TEXT NOT NULL," +
                        "email TEXT UNIQUE NOT NULL," +
                        "mat_khau TEXT," + // Không NOT NULL
                        "google_id TEXT," +
                        "loai_dang_nhap TEXT DEFAULT 'NORMAL');");

                // Copy dữ liệu từ bảng cũ sang bảng mới
                db.execSQL("INSERT INTO nguoi_dung_temp (id, ten, email, mat_khau, loai_dang_nhap) " +
                        "SELECT id, ten, email, mat_khau, 'NORMAL' FROM nguoi_dung;");

                // Xóa bảng cũ
                db.execSQL("DROP TABLE nguoi_dung;");

                // Đổi tên bảng mới
                db.execSQL("ALTER TABLE nguoi_dung_temp RENAME TO nguoi_dung;");

            } catch (Exception e) {
                // Nếu có lỗi, xóa tất cả và tạo lại
                db.execSQL("DROP TABLE IF EXISTS chi_tiet_bai_nop");
                db.execSQL("DROP TABLE IF EXISTS bai_nop");
                db.execSQL("DROP TABLE IF EXISTS cau_hoi_theo_mon");
                db.execSQL("DROP TABLE IF EXISTS mon_hoc");
                db.execSQL("DROP TABLE IF EXISTS nguoi_dung");
                onCreate(db);
            }
        }
    }

    // Phương thức helper để thêm user Google
    public boolean themNguoiDungGoogle(String ten, String email, String googleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO nguoi_dung (ten, email, google_id, loai_dang_nhap) VALUES (?, ?, ?, 'GOOGLE')",
                    new String[]{ten, email, googleId});
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
        }
    }

    // Phương thức kiểm tra user Google
    public boolean kiemTraNguoiDungGoogle(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE email = ?", new String[]{email});
        boolean ketQua = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ketQua;
    }
}