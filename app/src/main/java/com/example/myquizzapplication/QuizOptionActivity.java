package com.example.myquizzapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myquizzapplication.Adapter.MonHocAdapter;
import com.example.myquizzapplication.Repository.CourseRepository;
import com.example.myquizzapplication.models.MonHoc;
import java.util.List;

/**
 * Màn hình chọn môn học → bắt đầu quiz.
 */
public class QuizOptionActivity extends AppCompatActivity {

    /** Key dùng chung để truyền / nhận id và tên môn học */
    public static final String EXTRA_MON_HOC_ID = "mon_hoc_id";
    public static final String EXTRA_TEN_MON    = "ten_mon";
    public static final String EXTRA_USER_ID    = "user_id"; // tạm hard‑code user 1

    private RecyclerView rvMonHoc;
    private CourseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_option);

        rvMonHoc = findViewById(R.id.rvMonHoc);
        repository = new CourseRepository(this);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarQuizOption);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Lấy danh sách môn học
        List<MonHoc> monHocList = repository.getAllCourses();
        Log.d("CHECK_MONHOC", "Số môn học lấy được: " + monHocList.size());

        // Adapter (dùng lambda để bắt sự kiện click)
        MonHocAdapter adapter = new MonHocAdapter(this, monHocList, monHoc -> {
            Log.d("CHECK_MONHOC", "Click id = " + monHoc.getId());
            Intent intent = new Intent(this, QuizQuestionActivity.class);
            intent.putExtra(EXTRA_MON_HOC_ID, monHoc.getId());
            intent.putExtra(EXTRA_TEN_MON,    monHoc.getTenMon());
            intent.putExtra(EXTRA_USER_ID,    1); // TODO: truyền user thực
            startActivity(intent);
        });

        rvMonHoc.setLayoutManager(new LinearLayoutManager(this));
        rvMonHoc.setHasFixedSize(true);
        rvMonHoc.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}