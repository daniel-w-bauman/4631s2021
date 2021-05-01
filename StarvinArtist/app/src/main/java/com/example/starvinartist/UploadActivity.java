package com.example.starvinartist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    EditText nameText;
    EditText priceText;
    EditText contactText;
    EditText tagsText;
    Button uploadButton;
    ImageView selectedImage;

    static final String ROOT_URL = "http://10.0.0.227:3000/upload";
    static final int REQUEST_PERMISSIONS = 100;
    static final int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        nameText = (EditText) findViewById(R.id.name_text);
        priceText = (EditText) findViewById(R.id.price_text);
        contactText = (EditText) findViewById(R.id.contact_text);
        tagsText = (EditText) findViewById(R.id.tags_text);
        uploadButton = (Button) findViewById(R.id.upload_image_button);
        selectedImage = (ImageView) findViewById(R.id.selected_image);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(UploadActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            if (filePath != null) {
                try {
                    Log.d("filePath", String.valueOf(filePath));
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        uploadBitmap(bitmap);
                    }
                    selectedImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        UploadActivity.this,"no image selected",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadBitmap(final Bitmap bitmap) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String name = nameText.getText().toString();
        String price = priceText.getText().toString();
        String contact = contactText.getText().toString();
        String tags = tagsText.getText().toString();
        Intent i = getIntent();
        String token = i.getStringExtra("token");
        if(token == null){
            return;
        }

        String boundary = "apiclient-" + System.currentTimeMillis();
        String mimeType = "multipart/form-data;boundary=" + boundary;

        MultipartRequest request = new MultipartRequest(ROOT_URL, null,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d("Volley upload", "success");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley upload", "failure\n\t" + error.toString());
                    }
                });

        request.addPart(new MultipartRequest.FormPart("name", name));
        request.addPart(new MultipartRequest.FormPart("token", token));
        request.addPart(new MultipartRequest.FormPart("contact", contact));
        request.addPart(new MultipartRequest.FormPart("price", price));
        request.addPart(new MultipartRequest.FormPart("tags", tags));

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            request.addPart(new MultipartRequest.FilePart("photo", mimeType, "photo"+System.currentTimeMillis()+".jpeg", bytes));
            requestQueue.add(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
