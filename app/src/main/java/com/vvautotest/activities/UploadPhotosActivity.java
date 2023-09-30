package com.vvautotest.activities;

import static com.vvautotest.utils.ServerConfig.Photos_URL;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.vvautotest.R;
import com.vvautotest.adapter.CategoryAdapter;
import com.vvautotest.db.CategoryRepo;
import com.vvautotest.model.Category;
import com.vvautotest.model.Image;
import com.vvautotest.model.ImageDetail;
import com.vvautotest.model.OfflinePhotoModel;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.model.UserItem;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadPhotosActivity extends BaseActivity implements BaseActivity.BaseClassListener {
    static final int REQUEST_TAKE_PHOTO = 1;
    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;

    @BindView(R.id.categorySpinner)
    Spinner categorySpinner;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categoryDataArrayList;
    int categorySelectedId = -1;

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.descriptionTV)
    EditText descriptionTV;

    public LatLng currentLatLong;

    private static final int PICK_IMAGE_REQUEST_PERMISSION = 34;
    private static final int SETTING_REQUEST_PERMISSION = 35;

    @BindView(R.id.saveBtn)
    CardView saveBtn;
    private Uri fileUri;
    String photoPath = "";
    String photoPath2 = "";

    SessionManager sessionManager;
    User currentUser;
    Site selectedSite;

    ProgressDialog p;

    String encodedImage = "";
    String finalfileName = "";
    String fileExtention = "";

    String action = "";

    String currentDate = "";
    String imageUrl = "";
    String[] parts;

    @BindView(R.id.editBtn)
    ImageView editBtn;
    @BindView(R.id.buttonTxt)
    TextView buttonTxt;
    @BindView(R.id.viewFullImage)
    TextView viewFullImage;

    int id = 0;
    private String mCurrentPhotoPath;
    double lat = 0;
    double longi = 0;

    CategoryRepo categoryRepo;
    String selectedUserId = "";
    String selectedSiteId = "";
    String createdByDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photos);
        ButterKnife.bind(this);
        categoryRepo = new CategoryRepo(this);
        setBaseListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            action = intent.getStringExtra("action");
            currentDate = intent.getStringExtra("currentDate");
            imageUrl = intent.getStringExtra("imageUrl");

            if ("edit".equalsIgnoreCase(action)) {
                selectedUserId = intent.getStringExtra("selectedUserId");
                selectedSiteId = intent.getStringExtra("selectedSiteId");
            }
        }

        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getUserDetails();
        selectedSite = sessionManager.getSelectedSite();
        setupToolbar();
        init();

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> listOfRecords = categoryRepo.getAllCategory();
            if (listOfRecords != null && listOfRecords.size() > 0) {
                categoryDataArrayList = new ArrayList<>();
                categoryDataArrayList.addAll(listOfRecords);
                loadSpinner();
            } else {
                getCategories();
            }
        });

        if ("edit".equalsIgnoreCase(action)) {
            editBtn.setVisibility(View.VISIBLE);
            buttonTxt.setText("Update");
            try {
                parts = imageUrl.split("\\.");
                String u = imageUrl.replaceAll(" ", "%20");
                Glide.with(image).load(Photos_URL + selectedSiteId + "/"
                        + selectedUserId + "/" + currentDate + "/" + u).into(image);
                getImageDetails();
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewFullImage.setVisibility(View.VISIBLE);
        } else {
            viewFullImage.setVisibility(View.GONE);
            buttonTxt.setText("Save");
            editBtn.setVisibility(View.GONE);
        }
    }

    private void init() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (categorySelectedId == -1) {
                    AppUtils.showToast(UploadPhotosActivity.this, "Please select a category");
                    return;
                }
                if ("".equals(descriptionTV.getText().toString().trim())) {
                    AppUtils.showToast(UploadPhotosActivity.this, "Please enter description");
                    return;
                }
                if (!"edit".equalsIgnoreCase(action)) {
                    if ("".equals(encodedImage)) {
                        AppUtils.showToast(UploadPhotosActivity.this, "image not loaded");
                        return;
                    }
                }
                uploadImage(encodedImage, finalfileName, fileExtention);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRuntimePermissions();
            }
        });
        viewFullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl != null || "".equals(imageUrl)) {
                    ArrayList<Image> images = new ArrayList<>();
                    Image imma = new Image();
                    String u = imageUrl.replaceAll(" ", "%20");
                    imma.setUrl(Photos_URL + selectedSiteId + "/"
                            + selectedUserId + "/" + currentDate + "/" + u);
                    images.add(imma);
                    Intent intent = new Intent(UploadPhotosActivity.this, FullImageViewActivity.class);
                    intent.putExtra("images", images);
                    startActivity(intent);
                } else {
                    AppUtils.showToast(UploadPhotosActivity.this, "Image not Found");
                }
            }
        });
    }


    private void loadSpinner() {
        Category c = new Category();
        c.setId(-1);
        c.setName("Select Category");
        c.setSortOrder(-1);
        categoryDataArrayList.add(0, c);

        categoryAdapter = new CategoryAdapter(UploadPhotosActivity.this, categoryDataArrayList);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id) {
                        Category clickedItem = (Category) parent.getItemAtPosition(position);
                        String name = clickedItem.name;
                        categorySelectedId = clickedItem.id;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

    }

    private void setupToolbar() {
        navigationIcon.setOnClickListener(v -> onBackPressed());
    }

    private void getCategories() {
        AndroidNetworking.post(ServerConfig.Categories_URL)
                .setTag("Categories")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        L.printInfo(response.toString());
                                        ObjectMapper om = new ObjectMapper();
                                        try {
                                            categoryDataArrayList = new ArrayList<>();
                                            ArrayList<Category> tempList = om.readValue(response.toString(), new TypeReference<List<Category>>() {
                                            });
                                            categoryDataArrayList.addAll(tempList);
                                            loadSpinner();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        L.printError(anError.toString());
                                    }
                                }
                );

    }


    private void checkRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(UploadPhotosActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showOption();
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

    private void showOption() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(UploadPhotosActivity.this);
        dialog.setMessage("Browse");
        dialog.setPositiveButton("Camera",
                (dialog1, which) -> {


           /*  photoEasy = PhotoEasy.builder()
                            .setActivity(this)
                            .setMimeType(PhotoEasy.MimeType.imageJpeg)
                            .build();

             photoEasy.startActivityForResult(this);*/
                    openCamera();
                    dialog1.dismiss();
                });

        dialog.setNeutralButton("Gallery",
                (dialog12, which) -> {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startGalleryIntent.launch(i);
                    dialog12.dismiss();
                });

        AlertDialog d = dialog.create();
        d.show();
    }


    ActivityResultLauncher<Intent> startGalleryIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        try {
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();

                            photoPath = picturePath;

                            new CompressImage().execute(picturePath);
                            /*Uri uri = Uri.fromFile(new File(picturePath));
                            L.printError("Image Uri Gallery : " + uri.getPath());
*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            });


    ActivityResultLauncher<Intent> startCameraIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try {
                        //    galleryAddPic();
                        /*
                        if(result.getResultCode() == -1)
                        {
                            L.printError("Photo Path : " + photoPath);
                            L.printError("Photo Path 2: " + photoPath2);
                            //    Uri uri = Uri.fromFile(new File(photoPath2));
                            //    Glide.with(image).load(new File(photoPath2)).into(image);
                            new CompressImage().execute(photoPath);
                        }*/
                        photoPath = AppUtils.getCameraImagePath(UploadPhotosActivity.this);
                        L.printError("Photo Path........ : " + AppUtils.getCameraImagePath(UploadPhotosActivity.this));
                        L.printError("Photo Path........ : " + photoPath2);
                        File f = new File(photoPath);
                        Uri uri = Uri.fromFile(f);
                        Glide.with(image).load(uri).into(image);
                        new CompressImage().execute(photoPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //   Uri uri = Uri.fromFile(new File(photoPath));
                    //   L.printError("Image Uri Camera : " + uri.getPath());
                }
            });

    private void galleryAddPic() {
        /*Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(fileUri);
        sendBroadcast(mediaScanIntent);*/
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);

    }

    public Bitmap drawTextToBitmap(Context gContext,
                                   Bitmap bitmap,
                                   String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.white));
//        paint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/DS-DIGI.TTF"));
        paint.setTextSize((int) (30 * scale));
        paint.setShadowLayer(1f, 0f, 1f, getResources().getColor(R.color.dark_gray));

        int noOfLines = 0;
        for (String line : gText.split("\n")) {
            noOfLines++;
        }
      /*   int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;*/
        int y = 0;
        boolean isassigned = false;
        int lineCount = 0;
        for (String line : gText.split("\n")) {
            Rect bounds = new Rect();
            paint.getTextBounds(gText, 0, line.length(), bounds);
            int horizontalSpacing = 10;
            if (lineCount == 0) {
                horizontalSpacing = 60;
                lineCount++;
            } else if (lineCount == 1) {
                horizontalSpacing = 30;
                lineCount++;
            } else if (lineCount == 2) {
                horizontalSpacing = 50;
                lineCount++;
            } else if (lineCount == 3) {
                horizontalSpacing = 0;
                lineCount++;
            } else if (lineCount == 4) {
                horizontalSpacing = 150;
                lineCount++;
            }else if (lineCount == 5) {
                horizontalSpacing = 140;
                lineCount++;
            }else if (lineCount == 6) {
                horizontalSpacing = 0;
                lineCount++;
            }

            int verticalSpacing = 50;
            int x = bitmap.getWidth() - bounds.right - horizontalSpacing;//(bitmap.getWidth() - bounds.width()) / 2;
            if (!isassigned) {
                y = (bitmap.getHeight() - bounds.height() * noOfLines) - verticalSpacing;//(bitmap.getHeight() + bounds.height()) / 2;
                isassigned = true;
            }
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }
        //    canvas.drawText(gText, x, y, paint);
        return bitmap;
    }

    @Override
    public void onLocationChange(LatLng latLng) {
        currentLatLong = latLng;
        L.printError("Location....................." + latLng.longitude);
    }


    private class CompressImage extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... path) {
            String value = "";
            try {
                String mPath = path[0];
                Bitmap bmm = BitmapFactory.decodeFile(mPath);
                String text = createTextToWriteOnImage();
                L.printError(text);
                Bitmap bm = drawTextToBitmap(UploadPhotosActivity.this
                        , bmm, text);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte[] ba = bao.toByteArray();
                value = Base64.encodeToString(ba, Base64.NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPostExecute(String imagePath) {
            super.onPostExecute(imagePath);

            if (!"".equals(imagePath)) {
                encodedImage = imagePath;
                L.printError(encodedImage);
                L.printError("Image Path................" + imagePath);
                File f = new File(photoPath);
                Uri uri = Uri.fromFile(f);
                byte[] imageByteArray = Base64.decode(encodedImage, Base64.DEFAULT);
                Glide.with(image).load(imageByteArray).into(image);
                String fileName = f.getName();
                String[] parts = fileName.split("\\.");

                finalfileName = parts[0];
                fileExtention = parts[1];
            } else {
                AppUtils.showToast(UploadPhotosActivity.this, "Unable to create image, Please try again");
            }


            /*try {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            encodedImage = getStringFile(f);
                            L.printError(encodedImage);
                            L.printError("photoPath : " + f.getAbsolutePath());
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
        }
    }

    private class CompressBitmap extends AsyncTask<Bitmap, Void, String> {


        @Override
        protected String doInBackground(Bitmap... path) {
            String value = "";
            try {
                Bitmap bm = path[0];
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte[] ba = bao.toByteArray();
                value = Base64.encodeToString(ba, Base64.NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPostExecute(String imagePath) {
            super.onPostExecute(imagePath);

            if (!"".equals(imagePath)) {
                encodedImage = imagePath;
                //    L.printError(encodedImage);
                //    L.printError("Image Path................" + imagePath);
                finalfileName = new Date().getTime() + "";
                fileExtention = "jpg";
            } else {
                AppUtils.showToast(UploadPhotosActivity.this, "Unable to create image, Please try again");
            }


            /*try {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            encodedImage = getStringFile(f);
                            L.printError(encodedImage);
                            L.printError("photoPath : " + f.getAbsolutePath());
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
        }
    }

    // Converting File to Base64.encode String type using Method
    public String getStringFile(File f) {
        InputStream inputStream = null;
        String encodedFile = "", lastVal;
        try {
            inputStream = new FileInputStream(f.getAbsolutePath());

            byte[] buffer = new byte[10240]; //specify the size to allow
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }


            output64.close();


            encodedFile = output.toString();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastVal = encodedFile;
        return lastVal;
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

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                AppUtils.setCameraImagePath(UploadPhotosActivity.this, photoFile.getAbsolutePath());
                photoPath2 = photoPath;
                //    L.printError("Photo Path.... : " + photoPath);
                //     L.printError("Photo Path 2.... : " + photoPath2);
                fileUri = FileProvider.getUriForFile(UploadPhotosActivity.this,
                        getResources().getString(R.string.file_provider_package), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                startCameraIntent.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        L.printError("Request Code : " + requestCode);
        L.printError("Result Code : " + resultCode);
        /*if(photoEasy != null)
        {
            L.printError("photoEasy : " + photoEasy.toString());
            photoEasy.onActivityResult(requestCode, resultCode, new OnPictureReady() {
                @Override
                public void onFinish(Bitmap thumbnail) {
                    try {
                        L.printError("thumbnail : " + thumbnail.toString());
                        image.setImageBitmap(thumbnail);
                        new CompressBitmap().execute(thumbnail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }*/

        if (requestCode == SETTING_REQUEST_PERMISSION) {
            if (ContextCompat.checkSelfPermission(UploadPhotosActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(UploadPhotosActivity.this, Manifest.permission.
                            READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(UploadPhotosActivity.this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showOption();
            } else {
                Toast.makeText(UploadPhotosActivity.this, "Unable to get required Permission.", Toast.LENGTH_LONG).show();
            }
        }

       /* if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(bitmap);
        }*/
        /*if (requestCode == 87) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap =  getBitmapFromUri(uri);
                //    im.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String mimeType = this.getContentResolver().getType(uri);
            String filename = null;
            if (mimeType == null) {
            *//*    String path = CommonMethod.getPath(getActivity(), uri);

                File file = new File(path);
                filename = file.getName();*//*
                //                    }
            } else {
                Cursor returnCursor = this.getContentResolver().query(uri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                String size = Long.toString(returnCursor.getLong(sizeIndex));
            }
            File fileSave = this.getExternalFilesDir(null);
            String sourcePath = this.getExternalFilesDir(null).toString();
            File targetFile = null;
            try {
                targetFile = new File(sourcePath + "/" + filename);
                //   boolean success = CommonMethod.copyFileStream(new File(sourcePath + "/" + filename), uri, getActivity());
                //    boolean success = saveFile(uri, new File(sourcePath + "/" + filename));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (targetFile != null) {
                Uri uri1 = Uri.fromFile(targetFile);
                imagefilepath = uri1.getPath();
                new UploadImageDoc().execute(selectedData.getDocumentId());
            }
        }*/
    }


   /* private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);

    }*/


    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (currentLatLong != null) {
            savedInstanceState.putDouble("lat", currentLatLong.latitude);
            savedInstanceState.putDouble("longi", currentLatLong.longitude);
        }
        savedInstanceState.putLong("spinnerVal", categorySpinner.getSelectedItemPosition());
        super.onSaveInstanceState(savedInstanceState);
        //Put your spinner values to restore later...
    }


    private void uploadImage(String image, String name, String extention) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String requestDate = sdf.format(new Date());

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            if ("edit".equalsIgnoreCase(action)) {
                jsonObject.put("id", id);
                jsonObject.put("siteID", selectedSiteId);
                jsonObject.put("createdByDateTime", createdByDateTime);
            } else {
                jsonObject.put("id", 0);
                jsonObject.put("siteID", selectedSite.id);
                jsonObject.put("createdByDateTime", requestDate);
            }
            jsonObject.put("name", name);
            jsonObject.put("imageExt", extention);
            jsonObject.put("imageDesc", descriptionTV.getText().toString().trim());
            jsonObject.put("createdByUserID", currentUser.userID);
            jsonObject.put("categoryID", categorySelectedId);
            jsonObject.put("base64ImageData", image);
            L.printError(jsonObject.toString());

            //Adding Details for offline mode
            OfflinePhotoModel offlinePhotoModel = new OfflinePhotoModel();
            offlinePhotoModel.setDescription(descriptionTV.getText().toString().trim());
            offlinePhotoModel.setSite_id(selectedSite.id);
            offlinePhotoModel.setUser_id(currentUser.userID);
            offlinePhotoModel.setCategory_id(categorySelectedId + "");
            offlinePhotoModel.setIs_uploaded(false);
            offlinePhotoModel.setCreated_date(requestDate);
            offlinePhotoModel.setImage_extension(extention);
            offlinePhotoModel.setImage_name(name);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        AndroidNetworking.post(ServerConfig.Image_Upload_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("ImageUpload")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         AppUtils.showToast(UploadPhotosActivity.this, "Image Successfully Uploaded ");
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

    private void getImageDetails() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userID", selectedUserId);
            jsonObject.put("siteID", selectedSiteId);
            jsonObject.put("dateFolder", currentDate);
            jsonObject.put("name", parts[0]);
            jsonObject.put("imageExt", parts[1]);

            L.printError("Request.... " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading, Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AndroidNetworking.post(ServerConfig.Image_Details_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("image details")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         ObjectMapper om = new ObjectMapper();
                                         try {
                                             ImageDetail imageDetail = om.readValue(response.toString(), ImageDetail.class);
                                             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                                             sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                             String requestDate = sdf.format(imageDetail.createdByDateTime);
                                             createdByDateTime = requestDate;
                                             descriptionTV.setText(imageDetail.imageDesc);
                                             if ("edit".equalsIgnoreCase(action)) {
                                                 id = imageDetail.id;
                                                 int index = 0;
                                                 if (categoryDataArrayList != null && categoryDataArrayList.size() > 0) {
                                                     for (int i = 0; i < categoryDataArrayList.size(); i++) {
                                                         Category data = categoryDataArrayList.get(i);
                                                         if (imageDetail.categoryID == data.id) {
                                                             index = i;
                                                         }
                                                     }
                                                     categorySpinner.setSelection(index);
                                                 }

                                             }
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }

                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         progressDialog.dismiss();
                                         L.printError(anError.toString());
                                         AppUtils.showToast(UploadPhotosActivity.this, "Something went wrong, please try after sometime");
                                     }
                                 }
                );

    }

    private String convertTODegree(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();


        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        if (latitude < 0) {
            builder.append(" S");
        } else {
            builder.append(" N");
        }
        builder.append(" ");

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        if (longitude < 0) {
            builder.append(" W");
        } else {
            builder.append(" E");
        }
        return builder.toString();
    }

    private String createTextToWriteOnImage() {
        String str = "";
        DateFormat df = new SimpleDateFormat("MMM d, yyyy hh:mm:ss aa");
        String date = df.format(new Date());
        str += date + "\n";

        if (currentLatLong != null) {
            String lal = convertTODegree(currentLatLong.latitude, currentLatLong.longitude);
            str += lal.trim() + "\n";
            /*----------to get City-Name from coordinates ------------- */
            String country = "";
            String locality = "";
            String postalCode = "";
            String state = "";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(currentLatLong.latitude, currentLatLong.longitude, 1);
                if (addresses.size() > 0) {
                   /* L.printError(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getAddressLine(0);*/
                    locality = addresses.get(0).getSubLocality();
                    postalCode = addresses.get(0).getPostalCode();
                    country = addresses.get(0).getCountryName();
                    state = addresses.get(0).getAdminArea();
                    //    L.printError(addresses.get(0).getPremises());
                    //    L.printError(addresses.get(0).getCountryName());
                    //    L.printError(addresses.get(0).getAdminArea());
                    //    L.printError(addresses.get(0).getSubLocality());
                    //    L.printError(addresses.get(0).getPostalCode());
                }

                str += locality + " " + postalCode.trim() + "\n";
                str += state + ", " + country.trim() + "\n";

                str += selectedSite.name.trim() + "\n";
                if (selectedSite.roadNo != null) {
                    str += "PKG " + selectedSite.roadNo.trim() + "\n";
                }
                if (selectedSite.roadName != null) {
                    str += selectedSite.roadName.trim() + "\n";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // get values from saved state
        double lat = savedInstanceState.getDouble("lat");
        double longi = savedInstanceState.getDouble("longi");

        currentLatLong = new LatLng(lat, longi);
        super.onRestoreInstanceState(savedInstanceState);
    }

}