package com.vvautotest.fragments;

import static com.vvautotest.utils.ServerConfig.My_Document_URL;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.vvautotest.R;
import com.vvautotest.activities.FullImageViewActivity;
import com.vvautotest.activities.HomeActivity;
import com.vvautotest.adapter.DocumentGalleryAdapter;
import com.vvautotest.model.Image;
import com.vvautotest.model.ImageData;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.model.UserItem;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.DateUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.MultipartUtility;
import com.vvautotest.utils.ServerConfig;
import com.vvautotest.utils.SessionManager;
import com.vvautotest.utils.XMLParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UploadGalleryFragment extends Fragment implements HomeActivity.HomePageActionsListenr {


    private static final int PICK_IMAGE_REQUEST_PERMISSION = 34;
    private static final int SETTING_REQUEST_PERMISSION = 35;
    private int PICK_VIDEO_REQUEST = 2;
    private Uri fileUri;
    String photoPath = "";
    String photoPath2 = "";
    String encodedImage = "";
    String finalfileName = "";
    String fileExtention = "";
    @BindView(R.id.uploadPhotos)
    CardView uploadPhotos;
    @BindView(R.id.wrongSiteMessageLL)
    LinearLayout wrongSiteMessageLL;
    @BindView(R.id.siteSpinner)
    TextView siteSpinner;
    @BindView(R.id.userSpinner)
    TextView userSpinner;
    @BindView(R.id.noRecordMessageTV)
    TextView noRecordMessageTV;

    SessionManager sessionManager;
    User currentUser;
    String selectedUserId;
    String selectedUserName;
    Site selectedSite;

    String currentDate = "";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.filterLL)
    LinearLayout filterLL;
    LinearLayoutManager linearLayoutManager;
    ArrayList<ImageData> imageDataArrayList;
    DocumentGalleryAdapter photoAdapter;

    String menuId;
    Context context;

    boolean currentPointPolygonStatus;
    LatLng currentLatLong;

    ArrayList<Site> sitesArrayList;
    ArrayList<UserItem> usersArrayList;

    ArrayList<String> dirPath = new ArrayList<>();
    @BindView(R.id.pathTextView)
    TextView pathTextView;

    @BindView(R.id.back)
    ImageView back;
    private String mCurrentPhotoPath;
    private Uri VideofilePath = null;
    @BindView(R.id.videoview)
    VideoView mVideoView;

    long totalSize = 0;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity) context).setHomePageActionsListenr(this);
    }


    public UploadGalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_upload_gallery, container, false);
        ButterKnife.bind(this, rootView);
        sessionManager = new SessionManager(context);
        if (getArguments() != null) {
            menuId = getArguments().getString("menuId");
            currentPointPolygonStatus = getArguments().getBoolean("currentPointPolygonStatus", false);
        }
        currentUser = sessionManager.getUserDetails();
        selectedUserId = currentUser.userID;
        selectedUserName = currentUser.fName + " " + currentUser.mName + " " + currentUser.lName;
        selectedSite = sessionManager.getSelectedSite();

        /*if (currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT)) {
            filterLL.setVisibility(View.VISIBLE);
            siteSpinner.setText(selectedSite.name);
            userSpinner.setText(currentUser.fName + " " + currentUser.mName + " " + currentUser.lName);
            loadSite();
            //    loadUser();
        } else {
            filterLL.setVisibility(View.GONE);
        }*/
        currentDate = DateUtils.getCurrentDate("dd-MM-yyyy");
        L.printError("Current Formatted Date : " + currentDate);
        init(rootView);
    //    updateData();
        getGalleryFolders();

        return rootView;
    }

    public void updateData() {
        if (currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT)) {
            wrongSiteMessageLL.setVisibility(View.GONE);
            if (currentPointPolygonStatus) {
                uploadPhotos.setVisibility(View.VISIBLE);
            } else {
                uploadPhotos.setVisibility(View.GONE);
            }
        } else {
            if (currentPointPolygonStatus) {
                uploadPhotos.setVisibility(View.VISIBLE);
                wrongSiteMessageLL.setVisibility(View.GONE);
            } else {
                uploadPhotos.setVisibility(View.GONE);
                wrongSiteMessageLL.setVisibility(View.VISIBLE);
            }
        }
    }

    private void init(View rootView) {
       /* boolean value = AppUtils.getActionValueWithKey(context, AppUtils.Action.Action_Add);
        if (value) {
            uploadPhotos.setVisibility(View.VISIBLE);
        } else {
            uploadPhotos.setVisibility(View.GONE);
        }*/

        imageDataArrayList = new ArrayList<>();
        photoAdapter = new DocumentGalleryAdapter(context, imageDataArrayList, currentDate);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.setAdapter(photoAdapter);
        photoAdapter.setOnItemClickListener(new DocumentGalleryAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                ImageData data = imageDataArrayList.get(position);

                if(data.file.contains(".jpg") || data.file.contains(".png") ||
                        data.file.contains(".jpeg"))
                {
                    if (data.file != null || "".equals(data.file)) {
                        String path = "";
                        if(dirPath.size() > 0)
                        {
                            for (int i = 0; i < dirPath.size(); i++) {
                                path = path + dirPath.get(i) + "/";
                            }
                        }
                        ArrayList<Image> images = new ArrayList<>();
                        Image imma = new Image();
                        imma.setUrl(My_Document_URL + currentUser.userID + "/" +
                                path + data.file);
                        images.add(imma);
                        Intent intent = new Intent(getActivity(), FullImageViewActivity.class);
                        intent.putExtra("images", images);
                        startActivity(intent);
                    } else {
                        AppUtils.showToast(getActivity(), "Image not Found");
                    }
                }else if(data.file.contains(".pdf"))
                {
                    openPDF(data.file);
                }else
                {
                    dirPath.add(data.file);
                    back.setVisibility(View.VISIBLE);
                    getGalleryFolders();

                }
               /* Intent intent = new Intent(context, ImageListActivity.class);
                intent.putExtra("name", data.file);
                intent.putExtra("selectedUserId", selectedUserId);
                intent.putExtra("selectedSiteId", String.valueOf(selectedSite.id));
                intent.putExtra("selectedUserName", selectedUserName);
                intent.putExtra("selectedSiteName", selectedSite.name);*/
               // context.startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dirPath.size() > 0)
                {
                    if(dirPath.size() == 1)
                    {
                        dirPath.remove(dirPath.size()-1);
                        back.setVisibility(View.GONE);
                    }else
                    {
                        dirPath.remove(dirPath.size()-1);
                    }
                }else
                {
                    back.setVisibility(View.GONE);
                }
                getGalleryFolders();
            }
        });
    }

    public void openPDF(String url)
    {
        Uri path = null;
        try {

            String pathname = "";
            if(dirPath.size() > 0)
            {
                for (int i = 0; i < dirPath.size(); i++) {
                    pathname = pathname + dirPath.get(i) + "/";
                }
            }

            path = Uri.parse(My_Document_URL + currentUser.userID + "/"
                    +  pathname  + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try
        {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText( context, "NO Pdf Viewer", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.uploadPhotos)
    public void uploadPhotos() {

        checkRuntimePermissions();
    }

    @OnClick(R.id.createFolder)
    public void addFolder() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.folder_name_dialog, null);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setView(customLayout);
        EditText text = (EditText) customLayout.findViewById(R.id.folderNameET);
        Button dialogButton = (Button) customLayout.findViewById(R.id.addBtn);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = text.getText().toString().trim();
                if(!"".equalsIgnoreCase(name))
                {
                    String path = "";
                    if(dirPath.size() > 0)
                    {
                        for (int i = 0; i < dirPath.size(); i++) {
                            path = path + dirPath.get(i).trim() + "/";
                        }
                    }
                    createFolder(path + name);
                    dialog.dismiss();
                }else
                {
                    Toast.makeText(context, "Name can't be blank", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    public void getGalleryFolders() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String path = "";
        if(dirPath.size() > 0)
        {
            for (int i = 0; i < dirPath.size(); i++) {
                path = path + dirPath.get(i) + "/";
            }
        }
        pathTextView.setText(path);
        L.printError(ServerConfig.My_Document_URL + selectedUserId + "/" + path);
        AndroidNetworking.get(ServerConfig.My_Document_URL + selectedUserId + "/" + path)
                .setTag("Document")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                                 @Override
                                 public void onResponse(String response) {
                                     progressDialog.dismiss();
                                     L.printInfo("Documents : "+response.toString());
                                     try {
                                         JSONArray parseJson = XMLParser.parseHtmlToJSON2(response);
                                         ObjectMapper om = new ObjectMapper();
                                         try {
                                             imageDataArrayList = new ArrayList<>();
                                             imageDataArrayList = om.readValue(parseJson.toString(), new TypeReference<List<ImageData>>() {
                                             });
                                             if (imageDataArrayList != null && imageDataArrayList.size() > 0) {
                                                 noRecordMessageTV.setVisibility(View.GONE);
                                                 /*Collections.sort(imageDataArrayList, new Comparator<ImageData>() {
                                                     DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                                     @Override
                                                     public int compare(ImageData o1, ImageData o2) {
                                                         try {
                                                             return f.parse(o1.file).compareTo(f.parse(o2.file));

                                                         } catch (ParseException e) {
                                                             throw new IllegalArgumentException(e);
                                                         }
                                                     }
                                                 });*/
                                                 Collections.reverse(imageDataArrayList);
                                                 String pathname = "";
                                                 if(dirPath.size() > 0)
                                                 {
                                                     for (int i = 0; i < dirPath.size(); i++) {
                                                         pathname = pathname + dirPath.get(i) + "/";
                                                     }
                                                 }
                                                 photoAdapter.updateList(imageDataArrayList, pathname);
                                             } else {
                                                 String pathname = "";
                                                 if(dirPath.size() > 0)
                                                 {
                                                     for (int i = 0; i < dirPath.size(); i++) {
                                                         pathname = pathname + dirPath.get(i) + "/";
                                                     }
                                                 }
                                                 photoAdapter.updateList(imageDataArrayList, pathname);
                                                 noRecordMessageTV.setVisibility(View.VISIBLE);
                                             }
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                             String pathname = "";
                                             if(dirPath.size() > 0)
                                             {
                                                 for (int i = 0; i < dirPath.size(); i++) {
                                                     pathname = pathname + dirPath.get(i) + "/";
                                                 }
                                             }
                                             photoAdapter.updateList(imageDataArrayList,pathname);
                                             noRecordMessageTV.setVisibility(View.VISIBLE);
                                         }
                                     } catch (JSONException e) {
                                         e.printStackTrace();
                                         String pathname = "";
                                         if(dirPath.size() > 0)
                                         {
                                             for (int i = 0; i < dirPath.size(); i++) {
                                                 pathname = pathname + dirPath.get(i) + "/";
                                             }
                                         }
                                         photoAdapter.updateList(imageDataArrayList, pathname);
                                         noRecordMessageTV.setVisibility(View.VISIBLE);
                                     }
                                 }

                                 @Override
                                 public void onError(ANError anError) {
                                     L.printError("Documents Error : "+anError.getErrorBody());
                                     imageDataArrayList = new ArrayList<>();
                                     String pathname = "";
                                     if(dirPath.size() > 0)
                                     {
                                         for (int i = 0; i < dirPath.size(); i++) {
                                             pathname = pathname + dirPath.get(i) + "/";
                                         }
                                     }
                                     photoAdapter.updateList(imageDataArrayList, pathname);
                                     noRecordMessageTV.setVisibility(View.VISIBLE);
                                     progressDialog.dismiss();
                                 }
                             }
                );
    }


    private void checkRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            showOption();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_MEDIA_VIDEO)) {
                String requiresPermission = "", cam = "", storage = "";

                cam = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.
                        PERMISSION_GRANTED ? "Camera" : "";
                if (ContextCompat.checkSelfPermission(getActivity(),
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
                openUtilityDialog(getActivity(), "We need these permissions for fetching and saving image. Please grant " + requiresPermission);
            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_MEDIA_VIDEO},
                        PICK_IMAGE_REQUEST_PERMISSION);
            }
        }
    }

    private void showOption() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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

        dialog.setNegativeButton("Upload Video", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showVideoFileChooser();
                dialog.dismiss();
            }
        });

        AlertDialog d = dialog.create();
        d.show();
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                AppUtils.setCameraImagePath(getActivity(), photoFile.getAbsolutePath());
                photoPath2 = photoPath;
                //    L.printError("Photo Path.... : " + photoPath);
                //     L.printError("Photo Path 2.... : " + photoPath2);
                fileUri = FileProvider.getUriForFile(getActivity(),
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
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
    public void onSiteChange(Site site) {
        /*try {
            if (currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT)) {

            } else {
                selectedSite = sessionManager.getSelectedSite();
                getGalleryFolders();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onSiteRemainSame(Site site) {

    }

    @Override
    public void onLocationChange(LatLng latLng) {
        currentLatLong = latLng;
    }

    @Override
    public void onPointInPolygon(boolean isPointIn) {
        currentPointPolygonStatus = isPointIn;
        if (currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT)) {

        } else {
        //    updateData();
        }
    }

    private void createFolder(String siteid){
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("folderPath", siteid);
            jsonObject.put("createdByUserID", currentUser.userID);
            L.printError("Create folder Request : " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(ServerConfig.CeateFolder)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("Folder")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        L.printError("Create Folder : " + response.toString());
                        getGalleryFolders();
                    }

                    @Override
                    public void onError(ANError anError) {
                        L.printError(anError.toString());
                    }
                });
    }

    ActivityResultLauncher<Intent> startGalleryIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
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
                        photoPath = AppUtils.getCameraImagePath(getActivity());
                        L.printError("Photo Path........ : " + AppUtils.getCameraImagePath(getActivity()));
                        L.printError("Photo Path........ : " + photoPath2);
                        File f = new File(photoPath);
                        Uri uri = Uri.fromFile(f);
                    //    Glide.with(image).load(uri).into(image);
                        new CompressImage().execute(photoPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //   Uri uri = Uri.fromFile(new File(photoPath));
                    //   L.printError("Image Uri Camera : " + uri.getPath());
                }
            });


    private class CompressImage extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... path) {
            String value = "";
            try {
                String mPath = path[0];
                Bitmap bmm = BitmapFactory.decodeFile(mPath);
               /* String text = createTextToWriteOnImage();
                L.printError(text);
                Bitmap bm = drawTextToBitmap(getActivity()
                        , bmm, text);*/
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bmm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
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
            //    Glide.with(image).load(imageByteArray).into(image);
                String fileName = f.getName();
                String[] parts = fileName.split("\\.");

                finalfileName = parts[0];
                fileExtention = parts[1];

                uploadImage(encodedImage, finalfileName, fileExtention);
            } else {
                AppUtils.showToast(getActivity(), "Unable to create image, Please try again");
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
            Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
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


    private void uploadImage(String image, String name, String extention) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String requestDate = sdf.format(new Date());

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {

            String pathname = "";
            if(dirPath.size() > 0)
            {
                for (int i = 0; i < dirPath.size(); i++) {
                    pathname = pathname + dirPath.get(i) + "/";
                }
            }

            jsonObject.put("folderPath", pathname);
            jsonObject.put("fileName", name);
            jsonObject.put("fileExt", extention);

            jsonObject.put("createdByUserID", currentUser.userID);
            jsonObject.put("base64Data", image);
            L.printError(jsonObject.toString());

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        AndroidNetworking.post(ServerConfig.Image_Upload_Document_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("ImageUpload")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         getGalleryFolders();
                                     //    AppUtils.showToast(UploadPhotosActivity.this, "Image Successfully Uploaded ");

                                     }

                                     @Override
                                     public void onError(ANError anError) {
                                         progressDialog.dismiss();
                                         anError.printStackTrace();
                                         getGalleryFolders();
                                     }
                                 }
                );

    }


    //method to show file chooser
    private void showVideoFileChooser() {
        // Intent intent = new Intent();
        // intent.setType("*/*");
        //   intent.setAction(Intent.ACTION_GET_CONTENT);
        //  startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_DOC_REQUEST);

        String[] mimeTypes = {"video/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startVideoPickerIntent.launch(Intent.createChooser(intent, "Pick Video to Upload"));


    }



    ActivityResultLauncher<Intent> startVideoPickerIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getData() != null) {
                        VideofilePath = result.getData().getData();
                        String filename = "VID_" + VideofilePath.toString().substring(VideofilePath.toString().lastIndexOf("/") + 1);
                        L.printError("Video File"+filename);

                      /*  String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().getContentResolver().query(VideofilePath, projection, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            return cursor.getString(column_index);
                        }
                        */


                        String[] filePathColumn = {MediaStore.Video.Media.DATA};
                        Cursor cursor = getActivity().getContentResolver().query(VideofilePath,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        try {
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            cursor.close();

                            photoPath = picturePath;
                            File f = new File(photoPath);
                            String fileName = f.getName();
                            String[] parts = fileName.split("\\.");

                            finalfileName = parts[0];
                            fileExtention = parts[1];

                            String pathname = "";
                            if(dirPath.size() > 0)
                            {
                                for (int i = 0; i < dirPath.size(); i++) {
                                    pathname = pathname + dirPath.get(i) + "/";
                                }
                            }
                            /*mVideoView.setMediaController(new MediaController(getActivity()));
                            mVideoView.setVideoPath(photoPath);
                            mVideoView.requestFocus();
                            mVideoView.start();*/
                       //     new FileUploadTask().execute(photoPath.trim(), pathname,  finalfileName.trim(), fileExtention.trim(),  currentUser.userID.trim());
                            new UploadVideoFile().execute(pathname,  finalfileName.trim(), fileExtention.trim(),  currentUser.userID.trim(), photoPath.trim());
                    //        new CompressImage().execute(picturePath);
                      } catch (Exception e) {
                            e.printStackTrace();
                      }

                    }
                }

            });


    class UploadVideoFile extends AsyncTask<String, String, String> {
        ProgressDialog pd = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        }


        @Override
        protected String doInBackground(String... params) {
            String resp = "";
            try {
                L.printError(params[4]);
                File file = new File(params[4]);
                if (file.exists() || file.isFile())
                {
                    System.out.println(getFileSizeBytes(file));
                    System.out.println(getFileSizeKiloBytes(file));
                    System.out.println(getFileSizeMegaBytes(file));
                }

         /*       byte[] fileData = new byte[(int)im.length()];
                FileInputStream in = new FileInputStream(im);
                in.read(fileData);
                in.close();*/
                L.printError(ServerConfig.PDF_Upload_Video_URL);
                String charset = "UTF-8";
             /*   MultipartUtility multipart = new MultipartUtility("http://192.168.224.206:8080/api/v1.0/blogger/uploadOtherPostImage", charset);
                //  multipart.addFormField("user_id", userid);
                multipart.addFormFieldJSON("file_type_id", "4");
                multipart.addFormFieldJSON("post_id","2");
                multipart.addFilePart("file", file);

*/
                MultipartUtility multipart = new MultipartUtility(ServerConfig.PDF_Upload_Video_URL, charset);
                //  multipart.addFormField("user_id", userid);
                multipart.addFormFieldJSON("FolderPath", params[0]);
                multipart.addFormFieldJSON("FileName", params[1]);
                multipart.addFormFieldJSON("FileExt", params[2]);
                multipart.addFormFieldJSON("CreatedByUserID", params[3]);
                multipart.addFilePartForVideo("FileData", file);

                L.printError(multipart.printRequest());

                /*final String urlStr = ServerConfig.PDF_Upload_Video_URL;
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                MultipartHelper multipart = new MultipartHelper(connection);
                multipart.addStringPart("FolderPath", params[0]);
                multipart.addStringPart("FileName", params[1]);
                multipart.addStringPart("FileExt", params[2]);
                multipart.addStringPart("CreatedByUserID", params[3]);
                multipart.addFilePart(file, URLConnection.guessContentTypeFromName(params[1]),  "FileData");

                multipart.makeRequest();*/


                List<String> response = multipart.finish();
            //    List<String> response = new ArrayList<>();

                for (String line : response) {
                    resp = resp + line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            pd.dismiss();
            L.printInfo("Upload Video Response : " + response);
            getGalleryFolders();

        }

    }


    private static String getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    private static String getFileSizeKiloBytes(File file) {
        return (double) file.length() / 1024 + "  kb";
    }

    private static String getFileSizeBytes(File file) {
        return file.length() + " bytes";
    }

    public class FileUploadTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String sourceFileUri = params[0];
            String folderName = params[1]; // Extra parameter value
            String fName = params[2]; // Extra parameter value
            String fExt = params[3]; // Extra parameter value
            String user = params[4]; // Extra parameter value

            String upLoadServerUri = ServerConfig.PDF_Upload_Video_URL; // Replace with your server URL

            String fileName = sourceFileUri;

            L.printError(sourceFileUri + " : " +  folderName + " : " + fName + " : " + fExt+ " : " + user + " : " +  URLConnection.guessContentTypeFromName(fileName));

            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File not exist :" + fileName);
                return "Source File not exist :" + fileName;
            } else {
                try {
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    // Add extra parameter to the request
                    conn.setRequestProperty("FolderPath", folderName);
                    conn.setRequestProperty("FileName", fName);
                    conn.setRequestProperty("FileExt", fExt);
                    conn.setRequestProperty("CreatedByUserID", user);

                    conn.setRequestProperty("FileData", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"FileData\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    // Close the streams
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    if (serverResponseCode == 200) {
                        return "File Upload Completed.";
                    } else {
                        return "File Upload Failed. HTTP Response Code: " + serverResponseCode;
                    }

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    return "MalformedURLException";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Got Exception : see logcat ";
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            L.printInfo("Upload Video Response : " + result);
            getGalleryFolders();
        }
    }

}