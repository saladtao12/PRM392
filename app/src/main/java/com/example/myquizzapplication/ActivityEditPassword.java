package com.example.myquizzapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myquizzapplication.Repository.UserRepository;
import com.example.myquizzapplication.models.NguoiDung;

public class ActivityEditPassword extends AppCompatActivity {

    private EditText oldPassword, newPassword, confirmPassword;
    private TextView messageText;
    private ImageButton eyeOld, eyeNew, eyeConfirm;
    private Button savePasswordButton;
    private ImageButton backButton;
    private UserRepository userRepo;
    private NguoiDung user;
    private SessionManager session;

    private void BindingViews() {
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        messageText = findViewById(R.id.messageText);
        savePasswordButton = findViewById(R.id.savePasswordButton);
        eyeOld = findViewById(R.id.eyeOld);
        eyeNew = findViewById(R.id.eyeNew);
        eyeConfirm = findViewById(R.id.eyeConfirm);
        backButton = findViewById(R.id.backButton);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            // Nếu chưa đăng nhập, chuyển về LoginActivity
            Intent intent = new Intent(ActivityEditPassword.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        var email = session.getUserEmail();
        userRepo = new UserRepository(this);
        user = userRepo.getNguoiDung(email);
    }
    private void BindingActions() {
        savePasswordButton.setOnClickListener(this::savePassword);
        eyeOld.setOnClickListener(this::toggleOldPasswordVisibility);
        eyeNew.setOnClickListener(this::toggleNewPasswordVisibility);
        eyeConfirm.setOnClickListener(this::toggleConfirmPasswordVisibility);
        backButton.setOnClickListener(this::backHome);
    }

    private void backHome(View view) {

        Intent intent = new Intent(ActivityEditPassword.this, HomeActivity1.class);
        startActivity(intent);
        finish();
    }

    private boolean isOldPasswordVisible = false;
    private void toggleOldPasswordVisibility(View view) {
        if (isOldPasswordVisible) {
            oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeOld.setImageResource(R.drawable.visibility_off);
        } else {
            oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeOld.setImageResource(R.drawable.visibility_off);
        }
        oldPassword.setSelection(oldPassword.getText().length());
        isOldPasswordVisible = !isOldPasswordVisible;
    }
    private boolean isNewPasswordVisible = false;
    private void toggleNewPasswordVisibility(View view) {
        if (isNewPasswordVisible) {
            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeNew.setImageResource(R.drawable.visibility);
        } else {
            newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeNew.setImageResource(R.drawable.visibility_off);
        }
        newPassword.setSelection(newPassword.getText().length());
        isNewPasswordVisible = !isNewPasswordVisible;
    }
    private boolean isConfirmPasswordVisible = false;
    private void toggleConfirmPasswordVisibility(View view) {
        if (isConfirmPasswordVisible) {
            confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeConfirm.setImageResource(R.drawable.visibility);
        } else {
            confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeConfirm.setImageResource(R.drawable.visibility_off);
        }
        confirmPassword.setSelection(confirmPassword.getText().length());
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }
    private void savePassword(View view) {
        String oldPwd = oldPassword.getText().toString().trim();
        String newPwd = newPassword.getText().toString().trim();
        String confirmPwd = confirmPassword.getText().toString().trim();
        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showMessage("Please fill all fields");
            return;
        }
        if(!oldPwd.equals(user.matKhau)){
            showMessage("Old password is wrong");
            return;
        }
        if (newPwd.equals(oldPwd)) {
            showMessage("New password must be different from old password");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            showMessage("New passwords do not match");
            return;
        }
        userRepo.updatePassword(user.email, oldPwd, newPwd);
        showMessage("Password updated successfully", "#4CAF50");
        clearPassword();
//        Log.d("sang", "Updating: " + user.email + " with new pass: " + user.matKhau+ " with new pass: " + newPwd);
        user.matKhau = newPwd;
    }
    private void showMessage(String message) {
        showMessage(message, "#FF4444");
    }
    private void showMessage(String message, String colorHex) {
        messageText.setText(message);
        messageText.setTextColor(Color.parseColor(colorHex));
        messageText.setVisibility(View.VISIBLE);
    }
    private void clearPassword() {
        oldPassword.setText("");
        newPassword.setText("");
        confirmPassword.setText("");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BindingViews();
        BindingActions();
    }
}