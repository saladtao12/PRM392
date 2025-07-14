package com.example.myquizzapplication;

import android.database.Cursor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history); // File XML bạn gửi
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn trước
            }
        });

        container = findViewById(R.id.history_container); // LinearLayout trong ScrollView
        dbContext = new DbContext(this);

        loadHistoryData();
    }

    private void loadHistoryData() {
        // Truy vấn dữ liệu bài nộp và join với tên môn học
        String query = "SELECT bn.diem, bn.ngay_nop, mh.ten_mon " +
                "FROM bai_nop bn " +
                "JOIN mon_hoc mh ON bn.mon_hoc_id = mh.id " +
                "ORDER BY bn.ngay_nop DESC";

        Cursor cursor = dbContext.rawQuery(query, null);

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
        // Inflate layout từ card template (dùng LayoutInflater)
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout card = (LinearLayout) inflater.inflate(R.layout.item_history_card, null);

        // Set dữ liệu
        TextView txtMon = card.findViewById(R.id.txtSubject);
        TextView txtDiem = card.findViewById(R.id.txtScore);
        TextView txtNgay = card.findViewById(R.id.txtDate);

        txtMon.setText(mon);
        txtDiem.setText(diem);
        txtNgay.setText(ngayNop);

        // Thêm card vào container
        container.addView(card);
    }

}
