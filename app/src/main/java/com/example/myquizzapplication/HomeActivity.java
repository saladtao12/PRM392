package com.example.myquizzapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout btnStartQuiz;
    private LinearLayout btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Gắn layout XML

        // Ánh xạ ID từ XML
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        btnHistory = findViewById(R.id.btnHistory); // 🔧 THÊM DÒNG NÀY

        // Xử lý nút Start Quiz
        if (btnStartQuiz != null) {
            btnStartQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeActivity", "Start Quiz clicked");
                    Intent intent = new Intent(HomeActivity.this, QuizOptionActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("HomeActivity", "Không tìm thấy btnStartQuiz trong layout!");
        }

        // Xử lý nút History
        if (btnHistory != null) {
            btnHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeActivity", "History clicked");
                    Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("HomeActivity", "Không tìm thấy btnHistory trong layout!");
        }
    }
}
