package com.example.myquizzapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myquizzapplication.Repository.UserRepository;
import com.example.myquizzapplication.models.NguoiDung;

public class HomeActivity1 extends AppCompatActivity {
    private TextView userName;
    private LinearLayout btnStartQuiz;
    private LinearLayout btnHistory;
    private LinearLayout btnLogout;
    private LinearLayout btnRule;
    private LinearLayout btnEditPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Gắn layout XML

        // Ánh xạ ID từ XML
        userName = findViewById(R.id.userName);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        btnHistory = findViewById(R.id.btnHistory); // 🔧 THÊM DÒNG NÀY
        btnLogout = findViewById(R.id.btnLogout);
        btnRule = findViewById(R.id.rule);
        btnEditPassWord = findViewById(R.id.editPassWord);

        // Lấy tên người dùng từ SessionManager
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            Intent intent = new Intent(HomeActivity1.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        var email = session.getUserEmail();
        UserRepository userRepo = new UserRepository(this);
        NguoiDung user = userRepo.getNguoiDung(email);

        // Hiển thị tên người dùng
        if (userName != null) {
            userName.setText("Hello, " + user.getTen());
        } else {
            Log.e("HomeActivity", "Không tìm thấy userName trong layout!");
        }

        // Xử lý nút Start Quiz
        if (btnStartQuiz != null) {
            btnStartQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeActivity", "Start Quiz clicked");
                    Intent intent = new Intent(HomeActivity1.this, QuizOptionActivity.class);
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
                    Intent intent = new Intent(HomeActivity1.this, HistoryActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("HomeActivity", "Không tìm thấy btnHistory trong layout!");
        }


        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeActivity", "Logout clicked");

                    SessionManager sessionManager = new SessionManager(HomeActivity1.this);
                    sessionManager.logout();

                    // Chuyển về màn hình đăng nhập (MainActivity)
                    Intent intent = new Intent(HomeActivity1.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xoá stack
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Log.e("HomeActivity", "Không tìm thấy btnLogout trong layout!");
        }

        if (btnRule != null) {
            btnRule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeActivity", "History clicked");
                    Intent intent = new Intent(HomeActivity1.this, ActivityRules.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("HomeActivity", "Không tìm thấy btnHistory trong layout!");
        }
        if (btnEditPassWord != null) {
            btnEditPassWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeActivity", "History clicked");
                    Intent intent = new Intent(HomeActivity1.this, ActivityEditPassword.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("HomeActivity", "Không tìm thấy btnHistory trong layout!");
        }
    }
}
