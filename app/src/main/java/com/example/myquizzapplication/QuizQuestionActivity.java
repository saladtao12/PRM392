package com.example.myquizzapplication;

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
import com.example.myquizzapplication.Repository.SubmissionRepository;
import com.example.myquizzapplication.models.CauHoi;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình hiển thị câu hỏi trắc nghiệm và lưu kết quả.
 */
public class QuizQuestionActivity extends AppCompatActivity {

    private TextView tvCauHoi, tvSoThuTu;
    private RadioButton rbA, rbB, rbC, rbD;
    private RadioGroup rgDapAn;
    private Button btnNext;

    private List<CauHoi> cauHoiList;
    private String[] answersUser; // lưu đáp án người chọn
    private int currentIndex = 0;

    private int monHocId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);

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
        userId    = getIntent().getIntExtra(QuizOptionActivity.EXTRA_USER_ID, 1);
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
        toolbar.setNavigationOnClickListener(v -> finish());

        // Lấy câu hỏi
        QuestionRepository repo = new QuestionRepository(this);
        cauHoiList = repo.getQuestionsByMonHocId(monHocId);
        Log.d("QUIZ", "Số câu hỏi lấy được = " + cauHoiList.size());

        if (cauHoiList.isEmpty()) {
            Toast.makeText(this, "Không có câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        answersUser = new String[cauHoiList.size()];
        showQuestion(currentIndex);

        // Xử lý nút Next
        btnNext.setOnClickListener(v -> {
            // Lưu đáp án vừa chọn
            answersUser[currentIndex] = getSelectedAnswer();

            currentIndex++;
            if (currentIndex < cauHoiList.size()) {
                showQuestion(currentIndex);
            } else {
                // Tính điểm & lưu DB
                saveResultAndFinish();
            }
        });
    }

    /** lấy ký tự A/B/C/D tương ứng radio đã chọn; trả "" nếu chưa chọn */
    private String getSelectedAnswer() {
        int checkedId = rgDapAn.getCheckedRadioButtonId();
        if (checkedId == R.id.rbA) return "A";
        if (checkedId == R.id.rbB) return "B";
        if (checkedId == R.id.rbC) return "C";
        if (checkedId == R.id.rbD) return "D";
        return ""; // bỏ qua nếu chưa chọn
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
        rgDapAn.clearCheck();
        tvSoThuTu.setText("Current Question: " + (index + 1) + "/" + cauHoiList.size());
    }

    /** Tính điểm, lưu DB và thông báo */
    private void saveResultAndFinish() {
        int correct = 0;
        for (int i = 0; i < cauHoiList.size(); i++) {
            if (cauHoiList.get(i).getDapAnDung().equalsIgnoreCase(answersUser[i])) {
                correct++;
            }
        }
        double score = correct; // mỗi câu 1 điểm – tuỳ chỉnh

        // Lưu vào DB
        SubmissionRepository subRepo = new SubmissionRepository(this);
        subRepo.saveSubmission(userId, monHocId, score, cauHoiList, answersUser);

        Toast.makeText(this, "Bạn đúng " + correct + "/" + cauHoiList.size() + " – Điểm: " + score, Toast.LENGTH_LONG).show();
        finish();
    }
}
