package com.example.myquizzapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myquizzapplication.Repository.CourseRepository;
import com.example.myquizzapplication.Repository.MonHocAdapter1;
import com.example.myquizzapplication.models.MonHoc;

import java.util.List;

public class QuizOptionActivity extends AppCompatActivity {
    public static final String EXTRA_MON_HOC_ID = "mon_hoc_id";
    public static final String EXTRA_TEN_MON    = "ten_mon";
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<MonHoc> monHocList = repository.getAllCourses();
        Log.d("CHECK_MONHOC", "Số môn học lấy được: " + monHocList.size());

        MonHocAdapter1 adapter = new MonHocAdapter1(this, monHocList, monHoc -> {
            // Khi click vào môn học => sang màn hình câu hỏi
            Intent intent = new Intent(QuizOptionActivity.this, QuizQuestionActivity.class);
            intent.putExtra(EXTRA_MON_HOC_ID, monHoc.getId());   // truyền đúng key hằng số
            intent.putExtra(EXTRA_TEN_MON,    monHoc.getTenMon());
            startActivity(intent);
        });

        rvMonHoc.setLayoutManager(new LinearLayoutManager(this));
        rvMonHoc.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Nút back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
