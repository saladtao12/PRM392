package com.example.myquizzapplication.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myquizzapplication.DbContext;
import com.example.myquizzapplication.models.MonHoc;

import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    private final DbContext dbContext;

    public CourseRepository(Context context) {
        dbContext = new DbContext(context);
    }

    // Lấy tất cả môn học
    public List<MonHoc> getAllCourses() {
        List<MonHoc> list = new ArrayList<>();
        SQLiteDatabase db = dbContext.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM mon_hoc", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String tenMon = cursor.getString(cursor.getColumnIndexOrThrow("ten_mon"));
                list.add(new MonHoc(id, tenMon));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    // Thêm môn học
    public boolean insertCourse(String tenMon) {
        SQLiteDatabase db = dbContext.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ten_mon", tenMon);
        long result = db.insert("mon_hoc", null, values);
        return result != -1;
    }

    // Cập nhật tên môn học
    public boolean updateCourse(int id, String tenMonMoi) {
        SQLiteDatabase db = dbContext.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ten_mon", tenMonMoi);
        int result = db.update("mon_hoc", values, "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Xoá môn học theo ID
    public boolean deleteCourse(int id) {
        SQLiteDatabase db = dbContext.getWritableDatabase();
        int result = db.delete("mon_hoc", "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Tìm môn học theo ID
    public MonHoc getCourseById(int id) {
        SQLiteDatabase db = dbContext.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM mon_hoc WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String tenMon = cursor.getString(cursor.getColumnIndexOrThrow("ten_mon"));
            cursor.close();
            return new MonHoc(id, tenMon);
        }
        cursor.close();
        return null;
    }
}