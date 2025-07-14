package com.example.myquizzapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout container;
    private DbContext dbContext;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Nút quay lại
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Khởi tạo biến
        container = findViewById(R.id.history_container);
        dbContext = new DbContext(this);
        session = new SessionManager(this);

        loadHistoryData();
    }

    private void loadHistoryData() {
        // Lấy email từ Session
        String email = session.getUserEmail();
        if (email == null) return;

        // Truy vấn userId từ email
        int userId = -1;
        SQLiteDatabase db = dbContext.getReadableDatabase();
        Cursor cursorUser = db.rawQuery("SELECT id FROM nguoi_dung WHERE email = ?", new String[]{email});

        if (cursorUser.moveToFirst()) {
            userId = cursorUser.getInt(0);
        }

        cursorUser.close();
        db.close();

        if (userId == -1) return; // Không tìm thấy user

        // Truy vấn lịch sử bài nộp của user
        String query = "SELECT bn.diem, bn.ngay_nop, mh.ten_mon " +
                "FROM bai_nop bn " +
                "JOIN mon_hoc mh ON bn.mon_hoc_id = mh.id " +
                "WHERE bn.nguoi_dung_id = ? " +
                "ORDER BY bn.ngay_nop DESC";

        Cursor cursor = dbContext.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String mon = cursor.getString(2);
                String diem = String.valueOf(cursor.getDouble(0));
                String ngayNop = cursor.getString(1);

                addCard(mon, diem, ngayNop);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    private void addCard(String mon, String diem, String ngayNop) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout card = (LinearLayout) inflater.inflate(R.layout.item_history_card, null);

        TextView txtMon = card.findViewById(R.id.txtSubject);
        TextView txtDiem = card.findViewById(R.id.txtScore);
        TextView txtNgay = card.findViewById(R.id.txtDate);

        txtMon.setText(mon);
        txtDiem.setText(diem);
        txtNgay.setText(ngayNop);

        container.addView(card);
    }
}
