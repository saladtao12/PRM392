package com.example.myquizzapplication.Repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myquizzapplication.DbContext;
import com.example.myquizzapplication.models.CauHoi;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {
    private final DbContext dbContext;

    public QuestionRepository(Context context) {
        this.dbContext = new DbContext(context);
    }

    public List<CauHoi> getQuestionsByMonHocId(int monHocId) {
        List<CauHoi> list = new ArrayList<>();
        SQLiteDatabase db = dbContext.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM cau_hoi_theo_mon WHERE mon_hoc_id = ?", new String[]{String.valueOf(monHocId)});
        if (cursor.moveToFirst()) {
            do {
                CauHoi q = new CauHoi(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("mon_hoc_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("noi_dung")),
                        cursor.getString(cursor.getColumnIndexOrThrow("lua_chon_a")),
                        cursor.getString(cursor.getColumnIndexOrThrow("lua_chon_b")),
                        cursor.getString(cursor.getColumnIndexOrThrow("lua_chon_c")),
                        cursor.getString(cursor.getColumnIndexOrThrow("lua_chon_d")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dap_an_dung"))
                );
                list.add(q);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }
}
