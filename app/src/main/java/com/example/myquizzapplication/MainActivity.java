package com.example.myquizzapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignIn";

    private EditText emailInput, passwordInput;
    private Button loginButton, googleSignInButton;
    private DbContext dbContext;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        // Khởi tạo view
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        TextView registerText = findViewById(R.id.registerLoginText);

        // Khởi tạo DB
        dbContext = new DbContext(this);

        // Cấu hình Google Sign-In với error handling
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Log.d(TAG, "Google Sign-In configured successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error configuring Google Sign-In: " + e.getMessage());
            Toast.makeText(this, "Lỗi cấu hình Google Sign-In", Toast.LENGTH_SHORT).show();
            // Ẩn nút Google Sign-In nếu không thể cấu hình
            googleSignInButton.setVisibility(View.GONE);
        }

        // Sự kiện nhấn "Đăng ký"
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

        // Sự kiện nhấn "Đăng nhập" thông thường
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                if (kiemTraDangNhap(email, password)) {
                    Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    // ✅ Lưu session
                    SessionManager session = new SessionManager(MainActivity.this);
                    session.login(email); // 🔴 BẠN ĐANG BỎ QUA DÒNG NÀY
                    chuyenDenManHinhChinh(email);
                } else {
                    Toast.makeText(MainActivity.this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sự kiện nhấn "Đăng nhập Google"
        googleSignInButton.setOnClickListener(v -> {
            if (mGoogleSignInClient != null) {
                signInWithGoogle();
            } else {
                Toast.makeText(this, "Google Sign-In chưa được cấu hình", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
            Log.d(TAG, "Starting Google Sign-In intent");
        } catch (Exception e) {
            Log.e(TAG, "Error starting Google Sign-In: " + e.getMessage());
            Toast.makeText(this, "Không thể khởi động Google Sign-In", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Google sign in successful: " + account.getEmail());
                handleGoogleSignInResult(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed with code: " + e.getStatusCode());
                String errorMessage = getGoogleSignInErrorMessage(e.getStatusCode());
                Toast.makeText(this, "Đăng nhập Google thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getGoogleSignInErrorMessage(int statusCode) {
        switch (statusCode) {
            case 12501:
                return "Người dùng đã hủy đăng nhập";
            case 12502:
                return "Lỗi mạng";
            case 12500:
                return "Lỗi cấu hình";
            case 10: // DEVELOPER_ERROR
                return "Lỗi cấu hình developer (kiểm tra SHA-1, package name)";
            case 7: // NETWORK_ERROR
                return "Lỗi kết nối mạng";
            case 8: // INTERNAL_ERROR
                return "Lỗi nội bộ Google Services";
            case 4: // SIGN_IN_REQUIRED
                return "Cần đăng nhập lại";
            case 5: // INVALID_ACCOUNT
                return "Tài khoản không hợp lệ";
            default:
                return "Lỗi không xác định (Code: " + statusCode + ")";
        }
    }

    private void handleGoogleSignInResult(GoogleSignInAccount account) {
        if (account != null) {
            String email = account.getEmail();
            String name = account.getDisplayName();
            String googleId = account.getId();

            Log.d(TAG, "Google account info - Email: " + email + ", Name: " + name);

            try {
                // Kiểm tra xem user đã tồn tại trong DB chưa
                if (!kiemTraTaiKhoanGoogle(email)) {
                    // Tạo tài khoản mới cho user Google
                    taoTaiKhoanGoogle(email, name, googleId);
                    Log.d(TAG, "Created new Google user in database");
                }

                Toast.makeText(this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                chuyenDenManHinhChinh(email);
            } catch (Exception e) {
                Log.e(TAG, "Error handling Google user: " + e.getMessage());
                Toast.makeText(this, "Lỗi xử lý tài khoản Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean kiemTraTaiKhoanGoogle(String email) {
        SQLiteDatabase db = dbContext.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE email = ?", new String[]{email});
        boolean ketQua = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ketQua;
    }

    private void taoTaiKhoanGoogle(String email, String name, String googleId) {
        SQLiteDatabase db = dbContext.getWritableDatabase();
        try {
            // Thêm người dùng mới vào DB
            db.execSQL("INSERT INTO nguoi_dung (email, ten, google_id, loai_dang_nhap) VALUES (?, ?, ?, ?)",
                    new String[]{email, name, googleId, "GOOGLE"});
            Log.d(TAG, "Successfully inserted Google user into database");
        } catch (Exception e) {
            Log.e(TAG, "Error inserting Google user: " + e.getMessage());
            throw e;
        } finally {
            db.close();
        }
    }

    private boolean kiemTraDangNhap(String email, String password) {
        SQLiteDatabase db = dbContext.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM nguoi_dung WHERE email = ? AND mat_khau = ?", new String[]{email, password});
        boolean ketQua = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ketQua;
    }

    private void chuyenDenManHinhChinh(String email) {
        Intent intent = new Intent(MainActivity.this, HomeActivity1.class); // Thay Register.class bằng HomeActivity.class
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra xem user đã đăng nhập Google chưa
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d(TAG, "User already signed in with Google: " + account.getEmail());
            // User đã đăng nhập, chuyển đến màn hình chính
            chuyenDenManHinhChinh(account.getEmail());
        }
    }
}