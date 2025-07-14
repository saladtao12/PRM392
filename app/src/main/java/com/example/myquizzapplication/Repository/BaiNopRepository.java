package com.example.myquizzapplication.Repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myquizzapplication.DbContext;
import com.example.myquizzapplication.models.BaiNop;

public class BaiNopRepository {
    private final DbContext dbContext;

    public BaiNopRepository(Context context) {
        dbContext = new DbContext(context);
    }

    public BaiNop getBaiNopById(int baiNopId) {
        Cursor cursor = dbContext.rawQuery("SELECT * FROM bai_nop WHERE id = ?", new String[]{String.valueOf(baiNopId)});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            int nguoiDungId = cursor.getInt(1);
            int monHocId = cursor.getInt(2);
            double diem = cursor.getDouble(3);
            String ngayNop = cursor.getString(4);
            cursor.close();
            return new BaiNop(id, nguoiDungId, monHocId, diem, ngayNop);
        }
        cursor.close();
        return null;
    }
}
