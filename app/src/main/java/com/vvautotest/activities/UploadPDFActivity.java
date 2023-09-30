package com.vvautotest.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.vvautotest.R;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.PathUtil;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;

import org.apache.commons.io.file.PathUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadPDFActivity extends AppCompatActivity {

    public final static int PICKER_REQUEST_CODE = 12344;

    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;

    @BindView(R.id.loadPDFBtn)
    CardView loadPDFBtn;

    ActivityResultLauncher<Intent> resultLauncher;

    private static final int PICK_IMAGE_REQUEST_PERMISSION = 34;
    private static final int SETTING_REQUEST_PERMISSION = 35;

    String photoPath = "";
    String encodedImage = "";
    String finalfileName ="";
    String fileExtention  = "";
    @BindView(R.id.fileNameTV)
    TextView fileNameTV;
    @BindView(R.id.fileDesET)
    EditText fileDesET;
    @BindView(R.id.uploadPDF)
    CardView uploadPDF;

    String id = "";
    String action = "";

    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdfactivity);
        ButterKnife.bind(this);
        setupToolbar();
        sessionManager = new SessionManager(UploadPDFActivity.this);
        currentUser = sessionManager.getUserDetails();
        selectedSite = sessionManager.getSelectedSite();
        init();

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @SuppressLint("Range")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Initialize result data
                        Intent data = result.getData();
                        // check condition
                        if (data != null) {
                            Uri sUri = data.getData();
                            String uriString = sUri.toString();
                            File myFile = new File(uriString);
                            String path = myFile.getAbsolutePath();
                            String displayName = null;
                            int size = 0;

                            if (uriString.startsWith("content://")) {
                                Cursor cursor = null;
                                try {
                                    cursor = getContentResolver().query(sUri, null, null, null, null);
                                    if (cursor != null && cursor.moveToFirst()) {
                                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                    //    size = cursor.getInt(cursor.getColumnIndex(OpenableColumns.SIZE));
                                    }
                                } finally {
                                    cursor.close();
                                }
                            } else if (uriString.startsWith("file://")) {
                                displayName = myFile.getName();
                            }
                            try{
                                byte[] videoBytes;
                                if (sUri.getScheme().equals("content")){
                                    InputStream iStream =   getContentResolver().openInputStream(sUri);
                                    videoBytes = getBytes(iStream);
                                    new CompressImage().execute(videoBytes);
                                }
                                fileNameTV.setText(displayName);
                            //    photoPath = path;
                                String fileName = displayName;
                                String [] parts = fileName.split("\\.");
                                finalfileName = parts[0];
                                fileExtention  = parts[1];
                                L.printError("display Name " + displayName);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                         }
                        }
                });

    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    private class CompressImage extends AsyncTask<byte[], Void , String> {


        @Override
        protected String doInBackground(byte[]... path) {
            String value = "";
            try {
                byte[] ba = path[0];
                int len = ba.length / 1000;
                if (len < 10000)
                {
                value = Base64.encodeToString(ba, Base64.NO_WRAP);
                }else
                {
                    value = "greater";
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPostExecute(String imagePath) {
            super.onPostExecute(imagePath);


            if("greater".equalsIgnoreCase(imagePath)) {
                AppUtils.showToast(UploadPDFActivity.this, "File can't be greater then 10 MB");
                return;
            }

            if(!"".equals(imagePath))
            {
                encodedImage = imagePath;
                File f = new File(photoPath);
                Uri uri = Uri.fromFile(f);
            }else
            {
                AppUtils.showToast(UploadPDFActivity.this, "Unable to pick file, Please try again");
            }

        }
    }

    private void init(){
        loadPDFBtn.setOnClickListener(v -> {
            checkRuntimePermissions();
        });
        uploadPDF.setOnClickListener(v -> {
            if("".equals(fileDesET.getText().toString().trim()))
            {
                AppUtils.showToast(UploadPDFActivity.this, "Please enter description");
                return;
            }
            if("".equals(encodedImage))
            {
                AppUtils.showToast(UploadPDFActivity.this, "Please enter description");
                return;
            }
            uploadImage(encodedImage, finalfileName);
        });
    }

    public void getPDFFile(){
        try
        {
            Intent intent
                    = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            resultLauncher.launch(intent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void checkRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(UploadPDFActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            getPDFFile();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                String requiresPermission = "", cam = "", storage = "";

                cam = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.
                        PERMISSION_GRANTED ? "Camera" : "";
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    storage = "Storage";
                }
                if (!cam.isEmpty() && !storage.isEmpty()) {
                    requiresPermission = cam + ", " + storage + " permissions from settings.";
                } else if (!cam.isEmpty() && storage.isEmpty()) {
                    requiresPermission = cam + " permission from settings.";
                } else if (cam.isEmpty() && !storage.isEmpty()) {
                    requiresPermission = storage + " permission from settings.";
                }
                openUtilityDialog(this, "We need these permissions for fetching and saving image. Please grant " + requiresPermission);
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA},
                        PICK_IMAGE_REQUEST_PERMISSION);
            }
        }
    }

    public void openUtilityDialog(final Context ctx,
                                  final String messageID) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ctx);
        dialog.setMessage(messageID);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Settings", (dialog12, which) -> {
            dialog12.dismiss();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
            intent.setData(uri);
            this.startActivityForResult(intent, SETTING_REQUEST_PERMISSION);
        });
        dialog.setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_PERMISSION) {
            if (ContextCompat.checkSelfPermission(UploadPDFActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(UploadPDFActivity.this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                getPDFFile();
            } else {
                Toast.makeText(UploadPDFActivity.this, "Unable to get required Permission.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void uploadImage(String image, String name){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String requestDate = sdf.format(new Date());

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            if("edit".equalsIgnoreCase(action)) {
                jsonObject.put("id", id);
            }else
            {
                jsonObject.put("id", 0);
            }
            jsonObject.put("name", name);
            jsonObject.put("pdfDesc", fileDesET.getText().toString().trim());
            jsonObject.put("createdByUserID", currentUser.userID);
            jsonObject.put("createdByDateTime", requestDate);
            jsonObject.put("siteID", selectedSite.id);
            jsonObject.put("Base64PDFData", image);
            L.printError(jsonObject.toString());
        }catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        AndroidNetworking.post(ServerConfig.PDF_Upload_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("PDF Upload")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         AppUtils.showToast(UploadPDFActivity.this, "File Successfully Uploaded ");
                                         finish();
                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         progressDialog.dismiss();
                                         anError.printStackTrace();
                                         L.printError(anError.getErrorBody());
                                         L.printError("" + anError.getErrorCode());

                                     }
                                 }
                );

    }



    private void setupToolbar(){
        navigationIcon.setOnClickListener(v -> onBackPressed());
    }
}