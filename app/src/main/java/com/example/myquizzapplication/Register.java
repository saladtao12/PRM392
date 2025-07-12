package com.example.myquizzapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    EditText etEmail, etPassword, etConfirmPassword;
    Button btnCreateAccount;
    ImageButton btnBack;
    DbContext dbContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // XML giao diện bạn đã gửi

        // Gán view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnBack = findViewById(R.id.btnBack);

        dbContext = new DbContext(this);

        // Nút quay lại
        btnBack.setOnClickListener(view -> {
            finish(); // Đóng RegisterActivity, quay lại MainActivity
        });

        // Nút tạo tài khoản
        btnCreateAccount.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (kiemTraEmailDaTonTai(email)) {
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dangKyTaiKhoan(email, password)) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean kiemTraEmailDaTonTai(String email) {
        SQLiteDatabase db = dbContext.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM nguoi_dung WHERE email = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    private boolean dangKyTaiKhoan(String email, String password) {
        SQLiteDatabase db = dbContext.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ten", "Người dùng mới"); // Bạn có thể cho phép nhập tên nếu muốn
        values.put("email", email);
        values.put("mat_khau", password);

        long result = db.insert("nguoi_dung", null, values);
        return result != -1;
    }
}
