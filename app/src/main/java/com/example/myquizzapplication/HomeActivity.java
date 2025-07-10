package com.example.myquizzapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout btnStartQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // XML layout đúng tên

        // Ánh xạ ID
        btnStartQuiz = findViewById(R.id.btnStartQuiz);

        if (btnStartQuiz != null) {
            // Bắt sự kiện nhấn vào nút Start Quiz
            btnStartQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeActivity", "Start Quiz clicked");

                    // Mở QuizOptionActivity
                    Intent intent = new Intent(HomeActivity.this, QuizOptionActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("HomeActivity", "Không tìm thấy btnStartQuiz trong layout!");
        }
    }
}
