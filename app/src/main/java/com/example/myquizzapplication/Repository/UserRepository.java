package com.example.myquizzapplication.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myquizzapplication.DbContext;
import com.example.myquizzapplication.models.NguoiDung;

public class UserRepository {
    private final DbContext dbContext;

    public UserRepository(Context context) {
        dbContext = new DbContext(context);
    }

    public boolean updatePassword(String email, String oldPassword, String newPassword) {
        SQLiteDatabase db = dbContext.getWritableDatabase();

        // Kiểm tra tồn tại người dùng
        Cursor cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE email = ? AND mat_khau = ?", new String[]{email, oldPassword});
        if (cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put("mat_khau", newPassword);
            int result = db.update("nguoi_dung", values, "email = ?", new String[]{email});
            cursor.close();
            return result > 0;
        }
        cursor.close();
        return false;
    }

    public NguoiDung getNguoiDung(String email) {
        SQLiteDatabase db = dbContext.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String ten = cursor.getString(cursor.getColumnIndexOrThrow("ten"));
            String matKhau = cursor.getString(cursor.getColumnIndexOrThrow("mat_khau"));
            cursor.close();
            return new NguoiDung(id, ten, email, matKhau);
        }
        cursor.close();
        return null;
    }
}
