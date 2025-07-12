package com.example.myquizzapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton, googleSignInButton;
    TextView registerText;
    DbContext dbContext;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Khởi tạo views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        registerText = findViewById(R.id.registerLoginText);

        dbContext = new DbContext(this);
        session = new SessionManager(this);

        // Nếu đã đăng nhập => chuyển sang MainActivity
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Xử lý nút Đăng nhập
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkLogin(email, password)) {
                // Lưu session
                session.login(email);

                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("email", email); // nếu cần dùng email ở MainActivity
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý click "Đăng ký"
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, Register.class);
            startActivity(intent);
        });

        // Google Sign In (chưa dùng)
        googleSignInButton.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Chức năng Google chưa hỗ trợ", Toast.LENGTH_SHORT).show());
    }

    private boolean checkLogin(String email, String password) {
        SQLiteDatabase db = dbContext.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE email = ? AND mat_khau = ?", new String[]{email, password});
        boolean isLoggedIn = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isLoggedIn;
    }
}
