package com.example.myquizzapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myquizzapplication.Repository.UserRepository;
import com.example.myquizzapplication.models.BaiNop;
import com.example.myquizzapplication.models.NguoiDung;

public class BaiNopActivity extends AppCompatActivity {

    private static final String TAG = "BaiNopActivity";

    private TextView subjectValueTextView, pointsValueTextView, dateValueTextView;
    private Button startAgainButton;
    private DbContext dbContext;
    private UserRepository userRepo;
    private NguoiDung user;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            // Nếu chưa đăng nhập, chuyển về LoginActivity
            Intent intent = new Intent(BaiNopActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        var email = session.getUserEmail();
        userRepo = new UserRepository(this);
        user = userRepo.getNguoiDung(email);
        Log.d(TAG, "onCreate started");

        try {
            setContentView(R.layout.activity_result);
            Log.d(TAG, "Layout set successfully");

            // 1. Khởi tạo DB
            dbContext = new DbContext(this);
            Log.d(TAG, "DbContext initialized");

            // 2. Ánh xạ View với null check
            initializeViews();

            // 3. Truy vấn bài nộp mới nhất
            BaiNop baiNop = fetchLatestBaiNop();
            Log.d(TAG, "Latest BaiNop fetched: " + (baiNop != null ? "Success" : "Null"));

            if (baiNop != null) {
                String tenMon = getTenMonById(baiNop.getMonHocId());
                Log.d(TAG, "Subject name: " + tenMon);

                subjectValueTextView.setText(tenMon != null ? tenMon : "Không rõ môn");
                pointsValueTextView.setText(String.format("%.1f", baiNop.getDiem()));
                dateValueTextView.setText(formatDate(baiNop.getNgayNop()));
            } else {
                showEmptyState();
            }

            // 4. Xử lý nút
            startAgainButton.setOnClickListener(v -> {
                Log.d(TAG, "Start again button clicked");
                finish(); // Quay lại màn trước
            });

            Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Không finish() ngay lập tức, cho phép user thấy lỗi
        }
    }

    /**
     * Khởi tạo views với null check
     */
    private void initializeViews() {
        try {
            subjectValueTextView = findViewById(R.id.subjectValueTextView);
            pointsValueTextView = findViewById(R.id.pointsValueTextView);
            dateValueTextView = findViewById(R.id.dateValueTextView);
            startAgainButton = findViewById(R.id.start_again_button);

            // Kiểm tra null
            if (subjectValueTextView == null || pointsValueTextView == null ||
                    dateValueTextView == null || startAgainButton == null) {
                throw new RuntimeException("Một hoặc nhiều view không tìm thấy trong layout");
            }

            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Hiển thị trạng thái rỗng
     */
    private void showEmptyState() {
        subjectValueTextView.setText("Không rõ môn");
        pointsValueTextView.setText("0.0");
        dateValueTextView.setText("Chưa có bài nộp");
        Log.d(TAG, "Empty state displayed");
    }

    /**
     * Format ngày tháng để hiển thị
     */
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Không rõ ngày";
        }

        try {
            // Nếu dateString có format "yyyy-MM-dd HH:mm:ss", chỉ lấy phần ngày
            if (dateString.length() > 10) {
                return dateString.substring(0, 10);
            }
            return dateString;
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + e.getMessage());
            return dateString;
        }
    }

    /**
     * Truy vấn bài nộp mới nhất
     */
    private BaiNop fetchLatestBaiNop() {
        BaiNop baiNop = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbContext.getReadableDatabase();
            Log.d(TAG, "Database opened for reading");

            cursor = db.rawQuery("SELECT * FROM bai_nop ORDER BY ngay_nop DESC LIMIT 1", null);
            Log.d(TAG, "Query executed, cursor count: " + (cursor != null ? cursor.getCount() : 0));

            if (cursor != null && cursor.moveToFirst()) {
                baiNop = new BaiNop(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("nguoi_dung_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("mon_hoc_id")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("diem")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ngay_nop"))
                );
                Log.d(TAG, "BaiNop created successfully with ID: " + baiNop.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching latest BaiNop: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return baiNop;
    }

    /**
     * Truy vấn tên môn học theo ID
     */
    private String getTenMonById(int monHocId) {
        String tenMon = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbContext.getReadableDatabase();
            cursor = db.rawQuery("SELECT ten_mon FROM mon_hoc WHERE id = ?", new String[]{String.valueOf(monHocId)});

            if (cursor != null && cursor.moveToFirst()) {
                tenMon = cursor.getString(cursor.getColumnIndexOrThrow("ten_mon"));
                Log.d(TAG, "Subject name found: " + tenMon);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting subject name: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return tenMon;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (dbContext != null) {
                dbContext.close();
            }
            Log.d(TAG, "onDestroy completed");
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage(), e);
        }
    }
}