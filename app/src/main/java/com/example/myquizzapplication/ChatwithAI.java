package com.example.myquizzapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatwithAI extends AppCompatActivity {
    // Constants
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int MAX_CONVERSATION_HISTORY = 10; // Giới hạn lịch sử để tránh API limit
    private static final int MAX_IMAGE_SIZE = 800; // Giảm kích thước ảnh
    private static final int JPEG_QUALITY = 80; // Giảm quality để tối ưu

    // API Configuration
    private static final String API_KEY = "AIzaSyBnMQ6HEu57v-FtQBggCsZvmQoVh9Gv0R8";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    // UI Components
    private ImageView imageView;
    private TextView txtAIResponse;
    private EditText edtUserMessage;
    private Button btnSendText, btnTakePicture;
    private ScrollView scrollView;

    // Data
    private List<JSONObject> conversationHistory = new ArrayList<>();
    private Bitmap imageBitmap;
    private Uri photoURI;
    private File photoFile;

    // HTTP Client - Singleton với timeout
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatwithai);

        initializeHttpClient();
        initializeViews();
        checkCameraPermission();
        setupEventListeners();
    }

    private void initializeHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.imageView1);
        txtAIResponse = findViewById(R.id.txtAIResponse);
        edtUserMessage = findViewById(R.id.edtUserMessage);
        btnSendText = findViewById(R.id.btnSendTextToGemini);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        scrollView = findViewById(R.id.scrollView);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void setupEventListeners() {
        btnTakePicture.setOnClickListener(v -> dispatchTakePictureIntent());

        btnSendText.setOnClickListener(v -> {
            String userMessage = edtUserMessage.getText().toString().trim();
            if (userMessage.isEmpty()) {
                showToast("Vui lòng nhập tin nhắn.");
                return;
            }

            // Disable button để tránh spam
            btnSendText.setEnabled(false);
            sendToGemini(userMessage, imageBitmap);
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            getPackageName() + ".provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                showToast("Lỗi tạo file ảnh");
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            processImageResult();
        }
    }

    private void processImageResult() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            if (bitmap == null) {
                showToast("Không thể đọc ảnh");
                return;
            }

            // Xoay ảnh theo EXIF
            bitmap = rotateImageIfRequired(bitmap);

            // Resize ảnh để tối ưu
            imageBitmap = resizeImage(bitmap, MAX_IMAGE_SIZE);
            imageView.setImageBitmap(imageBitmap);

            // Cleanup
            if (bitmap != imageBitmap) {
                bitmap.recycle();
            }

        } catch (IOException e) {
            showToast("Không thể xử lý ảnh");
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotationDegrees = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationDegrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationDegrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationDegrees = 270;
                break;
        }

        if (rotationDegrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

    private Bitmap resizeImage(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxSize && height <= maxSize) {
            return bitmap;
        }

        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void sendToGemini(String userText, Bitmap imageBitmap) {
        try {
            String base64Image = encodeImageToBase64(imageBitmap);

            // Tạo user message
            JSONObject userMessage = createUserMessage(userText, base64Image);

            // Quản lý lịch sử conversation
            manageConversationHistory(userMessage);

            // Tạo request
            JSONObject content = createRequestContent();

            // Hiển thị UI
            updateChatDisplay();

            // Gửi request
            sendRequest(content);

        } catch (Exception e) {
            handleError("Lỗi khi tạo request: " + e.getMessage());
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, baos);
        byte[] imageBytes = baos.toByteArray();

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private JSONObject createUserMessage(String text, String base64Image) throws JSONException {
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");

        JSONArray parts = new JSONArray();

        // Text part
        JSONObject textPart = new JSONObject();
        textPart.put("text", text);
        parts.put(textPart);

        // Image part if available
        if (base64Image != null) {
            JSONObject imagePart = new JSONObject();
            JSONObject inlineData = new JSONObject();
            inlineData.put("mime_type", "image/jpeg");
            inlineData.put("data", base64Image);
            imagePart.put("inline_data", inlineData);
            parts.put(imagePart);
        }

        userMessage.put("parts", parts);
        return userMessage;
    }

    private JSONObject createAssistantMessage(String text) throws JSONException {
        JSONObject assistantMessage = new JSONObject();
        assistantMessage.put("role", "model");

        JSONArray parts = new JSONArray();
        JSONObject textPart = new JSONObject();
        textPart.put("text", text);
        parts.put(textPart);

        assistantMessage.put("parts", parts);
        return assistantMessage;
    }

    private void manageConversationHistory(JSONObject userMessage) {
        conversationHistory.add(userMessage);

        // Giới hạn lịch sử để tránh API limit
        while (conversationHistory.size() > MAX_CONVERSATION_HISTORY * 2) {
            conversationHistory.remove(0);
        }
    }

    private JSONObject createRequestContent() throws JSONException {
        JSONObject content = new JSONObject();
        JSONArray contents = new JSONArray();

        for (JSONObject message : conversationHistory) {
            contents.put(message);
        }

        content.put("contents", contents);
        return content;
    }

    private void updateChatDisplay() {
        StringBuilder chatHistory = new StringBuilder();

        for (JSONObject message : conversationHistory) {
            try {
                String role = message.getString("role");
                JSONArray parts = message.getJSONArray("parts");

                chatHistory.append(role.equals("user") ? "Bạn: " : "AI: ");

                for (int i = 0; i < parts.length(); i++) {
                    JSONObject part = parts.getJSONObject(i);
                    if (part.has("text")) {
                        chatHistory.append(part.getString("text"));
                    }
                    if (part.has("inline_data")) {
                        chatHistory.append(" [Đã gửi ảnh]");
                    }
                }

                chatHistory.append("\n\n");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        txtAIResponse.setText(chatHistory.toString());
        scrollToBottom();
    }

    private void sendRequest(JSONObject content) {
        RequestBody requestBody = RequestBody.create(content.toString(), JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> handleError("Lỗi kết nối: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    handleSuccessResponse(responseBody);
                } else {
                    runOnUiThread(() -> handleError("Lỗi API: " + response.code()));
                }
            }
        });
    }

    private void handleSuccessResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONArray candidates = json.getJSONArray("candidates");
            String output = candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            runOnUiThread(() -> {
                try {
                    // Thêm phản hồi AI vào lịch sử
                    JSONObject assistantMessage = createAssistantMessage(output);
                    conversationHistory.add(assistantMessage);

                    // Cập nhật UI
                    updateChatDisplay();
                    resetInputs();

                } catch (JSONException e) {
                    handleError("Lỗi xử lý phản hồi: " + e.getMessage());
                }
            });

        } catch (JSONException e) {
            runOnUiThread(() -> handleError("Lỗi JSON: " + e.getMessage()));
        }
    }

    private void resetInputs() {
        edtUserMessage.setText("");
        btnSendText.setEnabled(true);

        // Cleanup image
        if (imageBitmap != null) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
        imageView.setImageBitmap(null);
    }

    private void handleError(String errorMessage) {
        runOnUiThread(() -> {
            txtAIResponse.append(errorMessage + "\n\n");
            btnSendText.setEnabled(true);
            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cleanup resources
        if (imageBitmap != null) {
            imageBitmap.recycle();
        }

        // Clear conversation history
        conversationHistory.clear();
    }
}