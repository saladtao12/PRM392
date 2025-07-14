package com.example.myquizzapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myquizzapplication.Repository.QuestionRepository;
import com.example.myquizzapplication.models.CauHoi;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Màn hình hiển thị câu hỏi trắc nghiệm.
 */
public class QuizQuestionActivity extends AppCompatActivity {

    private TextView tvCauHoi, tvSoThuTu;
    private RadioButton rbA, rbB, rbC, rbD;
    private RadioGroup rgDapAn;
    private Button btnNext;

    private List<CauHoi> cauHoiList;
    private int currentIndex = 0;
    private int monHocId;
    private int nguoiDungId = 1; // Giả sử user hiện tại có id = 1, bạn có thể thay đổi theo logic app

    // Lưu trữ đáp án người dùng chọn
    private List<String> userAnswers;

    private DbContext dbContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);

        // Khởi tạo database và list lưu đáp án
        dbContext = new DbContext(this);
        userAnswers = new ArrayList<>();

        // Ánh xạ view
        tvCauHoi = findViewById(R.id.tvCauHoi);
        tvSoThuTu = findViewById(R.id.tvSoThuTu);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
        rgDapAn = findViewById(R.id.rgDapAn);
        btnNext = findViewById(R.id.btnNext);

        // Nhận dữ liệu từ intent
        monHocId = getIntent().getIntExtra(QuizOptionActivity.EXTRA_MON_HOC_ID, -1);
        String tenMon = getIntent().getStringExtra(QuizOptionActivity.EXTRA_TEN_MON);
        Log.d("QUIZ", "monHocId nhận được = " + monHocId);

        if (monHocId == -1) {
            Toast.makeText(this, "Không tìm thấy môn học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarQuestion);
        toolbar.setTitle((tenMon == null ? "Quiz" : tenMon + " Quiz"));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Lấy câu hỏi
        QuestionRepository repo = new QuestionRepository(this);
        cauHoiList = repo.getQuestionsByMonHocId(monHocId);
        Log.d("QUIZ", "Số câu hỏi lấy được = " + cauHoiList.size());

        if (!cauHoiList.isEmpty()) {
            // Khởi tạo list đáp án với kích thước bằng số câu hỏi
            for (int i = 0; i < cauHoiList.size(); i++) {
                userAnswers.add(""); // Khởi tạo rỗng
            }
            showQuestion(currentIndex);
        } else {
            Toast.makeText(this, "Không có câu hỏi!", Toast.LENGTH_SHORT).show();
        }

        // Xử lý nút Next
        btnNext.setOnClickListener(v -> {
            // Lưu đáp án hiện tại
            saveCurrentAnswer();

            currentIndex++;
            if (currentIndex < cauHoiList.size()) {
                showQuestion(currentIndex);
            } else {
                // Kết thúc quiz, lưu kết quả
                saveQuizResult();

                // Chuyển sang BaiNopActivity để hiển thị kết quả
                navigateToBaiNopActivity();
            }
        });
    }

    /**
     * Hiển thị câu hỏi theo index.
     */
    private void showQuestion(int index) {
        CauHoi q = cauHoiList.get(index);
        tvCauHoi.setText(q.getNoiDung());
        rbA.setText("A. " + q.getLuaChonA());
        rbB.setText("B. " + q.getLuaChonB());
        rbC.setText("C. " + q.getLuaChonC());
        rbD.setText("D. " + q.getLuaChonD());

        // Khôi phục đáp án đã chọn nếu có
        rgDapAn.clearCheck();
        String savedAnswer = userAnswers.get(index);
        if (!savedAnswer.isEmpty()) {
            switch (savedAnswer) {
                case "A":
                    rbA.setChecked(true);
                    break;
                case "B":
                    rbB.setChecked(true);
                    break;
                case "C":
                    rbC.setChecked(true);
                    break;
                case "D":
                    rbD.setChecked(true);
                    break;
            }
        }

        tvSoThuTu.setText("Current Question: " + (index + 1) + "/" + cauHoiList.size());

        // Đổi text nút Next thành "Finish" cho câu cuối
        if (index == cauHoiList.size() - 1) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
    }

    /**
     * Lưu đáp án hiện tại vào list
     */
    private void saveCurrentAnswer() {
        String selectedAnswer = "";
        int selectedId = rgDapAn.getCheckedRadioButtonId();

        if (selectedId == rbA.getId()) {
            selectedAnswer = "A";
        } else if (selectedId == rbB.getId()) {
            selectedAnswer = "B";
        } else if (selectedId == rbC.getId()) {
            selectedAnswer = "C";
        } else if (selectedId == rbD.getId()) {
            selectedAnswer = "D";
        }

        userAnswers.set(currentIndex, selectedAnswer);
    }

    /**
     * Lưu kết quả quiz vào database
     */
    private void saveQuizResult() {
        SQLiteDatabase db = dbContext.getWritableDatabase();

        try {
            // Tính điểm
            int correctAnswers = 0;
            for (int i = 0; i < cauHoiList.size(); i++) {
                CauHoi question = cauHoiList.get(i);
                String userAnswer = userAnswers.get(i);
                if (userAnswer.equals(question.getDapAnDung())) {
                    correctAnswers++;
                }
            }

            double score = (double) correctAnswers / cauHoiList.size() * 10; // Thang điểm 10

            // Lưu bài nộp
            ContentValues baiNopValues = new ContentValues();
            baiNopValues.put("nguoi_dung_id", nguoiDungId);
            baiNopValues.put("mon_hoc_id", monHocId);
            baiNopValues.put("diem", score);
            baiNopValues.put("ngay_nop", getCurrentDate());

            long baiNopId = db.insert("bai_nop", null, baiNopValues);

            if (baiNopId != -1) {
                // Lưu chi tiết bài nộp
                for (int i = 0; i < cauHoiList.size(); i++) {
                    CauHoi question = cauHoiList.get(i);
                    String userAnswer = userAnswers.get(i);

                    ContentValues chiTietValues = new ContentValues();
                    chiTietValues.put("bai_nop_id", baiNopId);
                    chiTietValues.put("cau_hoi_id", question.getId());
                    chiTietValues.put("dap_an_chon", userAnswer);

                    db.insert("chi_tiet_bai_nop", null, chiTietValues);
                }

                Log.d("QUIZ", "Lưu kết quả thành công! Điểm: " + score);
                Toast.makeText(this, "Hoàn thành quiz! Điểm: " + String.format("%.1f", score) + "/10", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("QUIZ", "Lỗi khi lưu kết quả: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi lưu kết quả!", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    /**
     * Chuyển sang BaiNopActivity để hiển thị kết quả
     */
    private void navigateToBaiNopActivity() {
        Intent intent = new Intent(this, BaiNopActivity.class);
        startActivity(intent);

        // Set result để báo cho activity trước biết quiz đã hoàn thành
        setResult(RESULT_OK);

        // Kết thúc activity hiện tại
        finish();
    }

    /**
     * Lấy ngày hiện tại theo format
     */
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbContext != null) {
            dbContext.close();
        }
    }
}