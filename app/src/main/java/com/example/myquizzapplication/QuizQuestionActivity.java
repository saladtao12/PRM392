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
import com.example.myquizzapplication.models.CauHoi;
import java.util.List;

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
        int monHocId = getIntent().getIntExtra(QuizOptionActivity.EXTRA_MON_HOC_ID, -1);
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
        toolbar.setNavigationOnClickListener(v -> finish());

        // Lấy câu hỏi
        QuestionRepository repo = new QuestionRepository(this);
        cauHoiList = repo.getQuestionsByMonHocId(monHocId);
        Log.d("QUIZ", "Số câu hỏi lấy được = " + cauHoiList.size());

        if (!cauHoiList.isEmpty()) {
            showQuestion(currentIndex);
        } else {
            Toast.makeText(this, "Không có câu hỏi!", Toast.LENGTH_SHORT).show();
        }

        // Xử lý nút Next
        btnNext.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < cauHoiList.size()) {
                showQuestion(currentIndex);
            } else {
                Toast.makeText(this, "Đã hết câu hỏi!", Toast.LENGTH_SHORT).show();
                finish();
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
        rgDapAn.clearCheck();
        tvSoThuTu.setText("Current Question: " + (index + 1) + "/" + cauHoiList.size());
    }
}
