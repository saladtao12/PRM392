package com.example.myquizzapplication.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.myquizzapplication.DbContext;
import com.example.myquizzapplication.models.CauHoi;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Lưu bài nộp và chi tiết bài nộp vào DB.
 */
public class SubmissionRepository {

    private final DbContext dbContext;

    public SubmissionRepository(Context ctx) {
        this.dbContext = new DbContext(ctx);
    }

    /**
     * @param userId      người dùng làm bài
     * @param monHocId    môn học
     * @param diem        điểm đạt
     * @param cauHoiList  danh sách câu hỏi
     * @param answers     đáp án người chọn (kích thước = cauHoiList.size())
     */
    public void saveSubmission(int userId, int monHocId, double diem,
                               List<CauHoi> cauHoiList, String[] answers) {
        SQLiteDatabase db = dbContext.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Insert bài_nop
            ContentValues bn = new ContentValues();
            bn.put("nguoi_dung_id", userId);
            bn.put("mon_hoc_id", monHocId);
            bn.put("diem", diem);
            bn.put("ngay_nop", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            long baiNopId = db.insert("bai_nop", null, bn);

            // 2. Insert chi tiết cho từng câu hỏi
            for (int i = 0; i < cauHoiList.size(); i++) {
                ContentValues ct = new ContentValues();
                ct.put("bai_nop_id", baiNopId);
                ct.put("cau_hoi_id", cauHoiList.get(i).getId());
                ct.put("dap_an_chon", answers[i] == null ? "" : answers[i]);
                db.insert("chi_tiet_bai_nop", null, ct);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
