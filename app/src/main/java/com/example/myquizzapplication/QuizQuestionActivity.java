package com.example.myquizzapplication;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myquizzapplication.Repository.QuestionRepository;
import com.example.myquizzapplication.models.CauHoi;

import java.util.List;

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

        tvCauHoi = findViewById(R.id.tvCauHoi);
        tvSoThuTu = findViewById(R.id.tvSoThuTu);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
        rgDapAn = findViewById(R.id.rgDapAn);
        btnNext = findViewById(R.id.btnNext);

        int monHocId = getIntent().getIntExtra("monHocId", -1);
        String tenMon = getIntent().getStringExtra("tenMon");

        Toolbar toolbar = findViewById(R.id.toolbarQuestion);
        toolbar.setTitle(tenMon + " Quiz");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        QuestionRepository repo = new QuestionRepository(this);
        cauHoiList = repo.getQuestionsByMonHocId(monHocId);

        if (cauHoiList.size() > 0) {
            showQuestion(currentIndex);
        } else {
            Toast.makeText(this, "Không có câu hỏi!", Toast.LENGTH_SHORT).show();
        }

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

    private void showQuestion(int index) {
        CauHoi q = cauHoiList.get(index);
        tvCauHoi.setText(q.getNoiDung());
        rbA.setText("A. " + q.getLuaChonA());
        rbB.setText("B. " + q.getLuaChonB());
        rbC.setText("C. " + q.getLuaChonC());
        rbD.setText("D. " + q.getLuaChonD());
        rgDapAn.clearCheck();
        tvSoThuTu.setText("Current Question: " + (index + 1));
    }
}
