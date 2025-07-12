package com.example.myquizzapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.media.ExifInterface;
import android.graphics.Matrix;

public class ChatwithAI extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imageView;
    Bitmap imageBitmap;
    TextView txtAIResponse;
    private Uri photoURI;       // <-- Thêm dòng này
    private File photoFile;     // <-- Nếu bạn dùng File
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        }
        setContentView(R.layout.chatwithai);  // Đảm bảo file layout có id đúng


        imageView = findViewById(R.id.imageView1); // Đảm bảo trong layout có @+id/imageView1
        txtAIResponse = findViewById(R.id.txtAIResponse);

        findViewById(R.id.btnTakePicture).setOnClickListener(v -> dispatchTakePictureIntent());

        findViewById(R.id.btnSendToGemini).setOnClickListener(v -> {
            if (imageBitmap != null) {
                sendImageToGemini(imageBitmap);
            } else {
                txtAIResponse.setText("Chưa có ảnh để gửi.");
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Lỗi tạo file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Đã cấp quyền camera", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Từ chối quyền camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);

                // Đọc EXIF từ file
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

                Matrix matrix = new Matrix();
                matrix.postRotate(rotationDegrees);

                Bitmap rotatedBitmap = Bitmap.createBitmap(
                        originalBitmap, 0, 0,
                        originalBitmap.getWidth(),
                        originalBitmap.getHeight(),
                        matrix, true
                );

                imageBitmap = rotatedBitmap;
                imageView.setImageBitmap(imageBitmap);

            } catch (IOException e) {
                Toast.makeText(this, "Không thể lấy ảnh", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoURI != null) {
            outState.putString("photo_uri", photoURI.toString());
        }
    }

    private void sendImageToGemini(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        String jsonBody = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"parts\": [\n" +
                "        {\n" +
                "          \"text\": \"Mô tả hình ảnh này\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"inline_data\": {\n" +
                "            \"mime_type\": \"image/jpeg\",\n" +
                "            \"data\": \"" + base64Image + "\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyDoKKKrwp1t1TJ8r2wg_c7REuWb9Ar_czc")

// Thay bằng API Key thật
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> txtAIResponse.setText("Lỗi: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject root = new JSONObject(json);
                        JSONArray candidates = root.getJSONArray("candidates");
                        String outputText = candidates.getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() -> txtAIResponse.setText(outputText));
                    } catch (JSONException e) {
                        runOnUiThread(() -> txtAIResponse.setText("Lỗi phân tích JSON"));
                    }
                } else {
                    runOnUiThread(() -> txtAIResponse.setText("API lỗi: " + response.code()));
                }
            }
        });
    }
}
