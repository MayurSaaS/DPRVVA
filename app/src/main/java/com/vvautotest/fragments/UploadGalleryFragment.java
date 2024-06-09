package com.vvautotest.fragments;

import static com.vvautotest.utils.ServerConfig.My_Document_URL;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.vvautotest.R;
import com.vvautotest.activities.FullImageViewActivity;
import com.vvautotest.activities.HomeActivity;
import com.vvautotest.activities.VVVideoPlayer;
import com.vvautotest.adapter.DocumentGalleryAdapter;
import com.vvautotest.adapter.SiteSpinnerAdapter;
import com.vvautotest.adapter.UserSpinnerAdapter;
import com.vvautotest.model.Image;
import com.vvautotest.model.ImageData;
import com.vvautotest.model.PhotoPathImage;
import com.vvautotest.model.Site;
import com.vvautotest.model.User;
import com.vvautotest.model.UserItem;
import com.vvautotest.service.UploadVideoService;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UploadGalleryFragment extends Fragment implements HomeActivity.HomePageActionsListenr {

    JSONObject json;

    private static final int REQUEST_POST_NOTIFICATIONS_PERMISSION = 1;
    private static final int PICK_IMAGE_REQUEST_PERMISSION = 34;
    private static final int SETTING_REQUEST_PERMISSION = 35;
    private int PICK_VIDEO_REQUEST = 2;
    private Uri fileUri;
    String photoPath = "";
    ArrayList<PhotoPathImage> photoPathArray = new ArrayList<>();
    JSONArray requestJSONImages = new JSONArray();
    Queue<String> requestFilesQueue;
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
    
    boolean isFistImageUpload = false;
    ProgressDialog imageUploadDailog;


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

        if (currentUser.userType.equalsIgnoreCase(AppUtils.UserType.TYPE_MANAGEMENT)) {
            filterLL.setVisibility(View.VISIBLE);
            siteSpinner.setText(selectedSite.name);
            userSpinner.setText(currentUser.fName + " " + currentUser.mName + " " + currentUser.lName);
            loadSite();
            //    loadUser();
        } else {
            filterLL.setVisibility(View.GONE);
        }
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

                try {
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
                            imma.setUrl(My_Document_URL + selectedUserId + "/" +
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
                    }else if(data.file.contains(".mp4"))
                    {
                        String pathname = "";
                        if(dirPath.size() > 0)
                        {
                            for (int i = 0; i < dirPath.size(); i++) {
                                pathname = pathname + dirPath.get(i) + "/";
                            }
                        }
                        String videoUrl = My_Document_URL + selectedUserId + "/"
                                +  pathname  + data.file;

                        /*Intent intent = new Intent(getActivity(), VVVideoPlayer.class);
                        intent.putExtra("url", videoUrl);
                        startActivity(intent);*/
                        /*MKPlayer mkplayer = new MKPlayer(getActivity());
                        mkplayer.play(videoUrl);*/
                        openMp4Files(data.file);
                    }else if(data.file.contains(".mkv"))
                    {
                        String pathname = "";
                        if(dirPath.size() > 0)
                        {
                            for (int i = 0; i < dirPath.size(); i++) {
                                pathname = pathname + dirPath.get(i) + "/";
                            }
                        }
                        String videoUrl = My_Document_URL + selectedUserId + "/"
                                +  pathname  + data.file;
                        openMKVFiles(videoUrl);
                       /* Intent intent = new Intent(getActivity(), VVVideoPlayer.class);
                        intent.putExtra("url", videoUrl);
                        startActivity(intent);*/
                    }else if(data.file.contains(".mov"))
                    {
                        String pathname = "";
                        if(dirPath.size() > 0)
                        {
                            for (int i = 0; i < dirPath.size(); i++) {
                                pathname = pathname + dirPath.get(i) + "/";
                            }
                        }
                        String videoUrl = My_Document_URL + selectedUserId + "/"
                                +  pathname  + data.file;
                        openMOVFiles(videoUrl);
                        /*Intent intent = new Intent(getActivity(), VVVideoPlayer.class);
                        intent.putExtra("url", videoUrl);
                        startActivity(intent);*/
                    }else
                    {
                        dirPath.add(data.file);
                        back.setVisibility(View.VISIBLE);
                        getGalleryFolders();
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
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

    public  void openMp4Files(String url){
        try {
                String pathname = "";
                if(dirPath.size() > 0)
                {
                    for (int i = 0; i < dirPath.size(); i++) {
                        pathname = pathname + dirPath.get(i) + "/";
                    }
                }
            String videoUrl = My_Document_URL + selectedUserId + "/"
                    +  pathname  + url;
            Intent playVideo = new Intent(Intent.ACTION_VIEW);
            playVideo.setDataAndType(Uri.parse(videoUrl), "video/mp4");
            startActivity(playVideo);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public  void openMOVFiles(String url){
        try {
            String pathname = "";
            if(dirPath.size() > 0)
            {
                for (int i = 0; i < dirPath.size(); i++) {
                    pathname = pathname + dirPath.get(i) + "/";
                }
            }
            String videoUrl = My_Document_URL + selectedUserId + "/"
                    +  pathname  + url;
            Intent playVideo = new Intent(Intent.ACTION_VIEW);
            playVideo.setDataAndType(Uri.parse(videoUrl), "video/mov");
            startActivity(playVideo);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
   public  void openMKVFiles(String url){
        try {
            String pathname = "";
            if(dirPath.size() > 0)
            {
                for (int i = 0; i < dirPath.size(); i++) {
                    pathname = pathname + dirPath.get(i) + "/";
                }
            }
            String videoUrl = My_Document_URL + selectedUserId + "/"
                    +  pathname  + url;
            Intent playVideo = new Intent(Intent.ACTION_VIEW);
            playVideo.setDataAndType(Uri.parse(videoUrl), "video/*");
            startActivity(playVideo);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
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
            path = Uri.parse(My_Document_URL + selectedUserId + "/"
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
                                                 photoAdapter.updateUserId(selectedUserId);
                                                 photoAdapter.updateList(imageDataArrayList, pathname);
                                             } else {
                                                 String pathname = "";
                                                 if(dirPath.size() > 0)
                                                 {
                                                     for (int i = 0; i < dirPath.size(); i++) {
                                                         pathname = pathname + dirPath.get(i) + "/";
                                                     }
                                                 }
                                                 photoAdapter.updateUserId(selectedUserId);
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
                                             photoAdapter.updateUserId(selectedUserId);
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
                                         photoAdapter.updateUserId(selectedUserId);
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
                                     photoAdapter.updateUserId(selectedUserId);
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
                || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            showOption();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.POST_NOTIFICATIONS)
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
                                Manifest.permission.POST_NOTIFICATIONS,
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
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startGalleryIntent.launch(Intent.createChooser(intent, "Select Picture"));

                    /* Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");
                    startGalleryIntent.launch(i);*/
                    dialog12.dismiss();
                });

        dialog.setNegativeButton("Video", new DialogInterface.OnClickListener() {
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

    boolean isCancel = false;


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

                        //Multiple Selection
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            if(result.getData().getClipData() != null) {
                                int count = result.getData().getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                                photoPathArray = new ArrayList<>();
                                for(int i = 0; i < count; i++) {
                                    Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                                    Cursor cursor = getActivity().getContentResolver().query(imageUri,
                                            filePathColumn, null, null, null);
                                    cursor.moveToFirst();
                                    try {
                                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                        String picturePath = cursor.getString(columnIndex);
                                        cursor.close();
                                        PhotoPathImage photoPathImage = new PhotoPathImage();
                                        photoPathImage.setPath(picturePath);
                                        photoPathArray.add(photoPathImage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    L.printError("Gallery Pic" + imageUri);
                                }
                                new CompressMultipleImage().execute();

                            }
                        }/* else if(result.getData().getData() != null) {
                            String imagePath = result.getData().getData().getPath();
                            L.printError("Gallery Path" + imagePath);
                            //do something with the image (save it to some directory or whatever you need to do with it here)
                        }*/


                    //Single Selection
                    /* if (result != null && result.getData() != null) {
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }*/
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

    private class CompressMultipleImage extends AsyncTask<String, Void, ArrayList<PhotoPathImage>> {
        ProgressDialog progressDialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<PhotoPathImage> doInBackground(String... path) {
            ArrayList<PhotoPathImage> value = new ArrayList<>();
            try {
                for (int i = 0; i < photoPathArray.size(); i++) {
                    PhotoPathImage data = photoPathArray.get(i);
                    String mPath = data.getPath();
                    Bitmap bmm = BitmapFactory.decodeFile(mPath);
               /* String text = createTextToWriteOnImage();
                L.printError(text);
                Bitmap bm = drawTextToBitmap(getActivity()
                        , bmm, text);*/
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    bmm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                    byte[] ba = bao.toByteArray();
                    String base64 = Base64.encodeToString(ba, Base64.NO_WRAP);
                    data.setFileBase64Image(base64);
                    value.add(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }

        @Override
        protected void onPostExecute(ArrayList<PhotoPathImage> imagePath) {
            super.onPostExecute(imagePath);
            progressDialog.dismiss();
            if (imagePath != null && imagePath.size() > 0)
            {
                String pathname = "";
                if(dirPath.size() > 0)
                {
                    for (int i = 0; i < dirPath.size(); i++) {
                        pathname = pathname + dirPath.get(i) + "/";
                    }
                }
                requestJSONImages = new JSONArray();
                requestFilesQueue = new LinkedList();
                for (int i = 0; i < imagePath.size(); i++) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        File f = new File(imagePath.get(i).getPath());
                        String fileName = f.getName();
                        String[] parts = fileName.split("\\.");

                        finalfileName = parts[0];
                        fileExtention = parts[1];
                        jsonObject.put("fileExt",fileExtention);
                        jsonObject.put("base64Data",imagePath.get(i).getFileBase64Image());
                        jsonObject.put("fileName",finalfileName);
                        jsonObject.put("createdByUserID",currentUser.userID);
                        jsonObject.put("folderPath", pathname);
                        requestJSONImages.put(jsonObject);
                        requestFilesQueue.add(jsonObject.toString());
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                isFistImageUpload = true;
                uploadMultipleImage();
            }
        }
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

    private void uploadMultipleImage() {
        if(!requestFilesQueue.isEmpty())
        {
            String value;
            try {

                if(isFistImageUpload)
            {
                imageUploadDailog = new ProgressDialog(context);
             //   imageUploadDailog.setMax(100);
                imageUploadDailog.setTitle("Uploading files " + ((requestJSONImages.length() - requestFilesQueue.size()) + 1)+"/"+ requestJSONImages.length());
                imageUploadDailog.setMessage("Uploading...");
                imageUploadDailog.setCancelable(false);
          //      imageUploadDailog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                imageUploadDailog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isCancel = true;
                        AndroidNetworking.forceCancelAll();
                        //    Toast.makeText(context, "Upload Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
            //    imageUploadDailog.setProgress(0);
                imageUploadDailog.show();
                isFistImageUpload = false;
                value  = requestFilesQueue.poll();

            }else
            {

            }

                value  = requestFilesQueue.poll();
                JSONObject jsonObject = new JSONObject(value);
                imageUploadDailog.setTitle("Uploading files " + ((requestJSONImages.length() - requestFilesQueue.size()) + 1)+"/"+ requestJSONImages.length());
                imageUploadDailog.setMessage("Uploading..." + jsonObject.getString("fileName") + "." +
                        jsonObject.getString("fileExt"));

        /*        uploadImage(jsonObject.getString("base64Data"), jsonObject.getString("fileName"),
                        jsonObject.getString("fileExt"));
        */
                AndroidNetworking.post(ServerConfig.Image_Upload_Document_URL)
                        .addJSONObjectBody(jsonObject) // posting json
                        .setTag(jsonObject.getString("fileName"))
                        .setPriority(Priority.HIGH)
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {
                                L.printError("...................." + bytesUploaded + "/" + totalBytes);
                            //    int percent = (int)(100.0*(double)bytesUploaded/totalBytes + 0.5);
                            //imageUploadDailog.setProgress(percent);
              /*                  imageUploadDailog.setMessage("Uploading " + AppUtils.bytesIntoHumanReadable(bytesUploaded)
                                        + "/" + AppUtils.bytesIntoHumanReadable(totalBytes) + " " + finalfileName+ "." + fileExtention);
*/
                            }
                        })
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                L.printInfo(response.toString());
                                if(requestFilesQueue.isEmpty())
                                {
                                    isFistImageUpload = false;
                                    imageUploadDailog.dismiss();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                    builder1.setTitle("Success");
                                    builder1.setMessage("File Uploaded Successfully");
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton(
                                            "Close",
                                            (dialog, id) -> {
                                                getGalleryFolders();
                                                dialog.cancel();
                                            });
                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }else
                                {
                                    uploadMultipleImage();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                if(requestFilesQueue.isEmpty())
                                {
                                    isFistImageUpload = false;
                                    imageUploadDailog.dismiss();
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                    builder1.setTitle("Error");
                                    builder1.setMessage(anError.getErrorBody());
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton(
                                            "Close",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }else
                                {
                                    uploadMultipleImage();
                                }

                            }
                        });

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }else {

        }

/*
        final ProgressDialog mDialog;
        mDialog = new ProgressDialog(context);
        mDialog.setMax(100);
        mDialog.setCancelable(false);
        mDialog.setMessage("Uploading files");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isCancel = true;
                AndroidNetworking.forceCancelAll();
                //    Toast.makeText(context, "Upload Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        mDialog.setProgress(0);


        mDialog.show();
        L.printError("Request Json : " + requestJSONImages.toString());

        AndroidNetworking.post(ServerConfig.Image_Upload_Multiple_Document_URL)
                .addJSONArrayBody(requestJSONImages) // posting json
                .setTag("ImageUpload")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        L.printError("...................." + bytesUploaded + "/" + totalBytes);
                        int percent = (int)(100.0*(double)bytesUploaded/totalBytes + 0.5);
                    //    mDialog.setProgress(percent);
                                            mDialog.setMessage("Uploading " + AppUtils.bytesIntoHumanReadable(bytesUploaded)
                                                    + "/" + AppUtils.bytesIntoHumanReadable(totalBytes) + " " + finalfileName+ "." + fileExtention);
                                       //    L.printError("Progress : " + bytesUploaded);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                     //   mDialog.dismiss();
                        mDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("Success");
                        builder1.setMessage("File Uploaded Successfully");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Close",
                                (dialog, id) -> {
                                    getGalleryFolders();
                                    dialog.cancel();
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                        //    AppUtils.showToast(UploadPhotosActivity.this, "Image Successfully Uploaded ");

                    }

                    @Override
                    public void onError(ANError anError) {
                        if(isCancel)
                        {
                            isCancel = false;
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle("Error");
                            builder1.setMessage("Uploading has been canceled");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    "Close",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                        }else
                        {
                            mDialog.dismiss();
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setTitle("Error");
                            builder1.setMessage(anError.getErrorBody());
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    "Close",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                    }
                });
*/

    }


    //method to show file chooser
    private void showVideoFileChooser() {
        // Intent intent = new Intent();
        // intent.setType("*/*");
        //   intent.setAction(Intent.ACTION_GET_CONTENT);
        //  startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_DOC_REQUEST);

        String[] mimeTypes = {"video/mp4"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "video/mp4");
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

                            json  = new JSONObject();
                            json.put("FileData" , photoPath);
                            json.put("FileName" , finalfileName);
                            json.put("FileExt" , fileExtention);
                            json.put("CreatedByUserID" , currentUser.userID);
                            json.put("FolderPath" , pathname);

                        //    checkNotificationsPermission();
                            /*mVideoView.setMediaController(new MediaController(getActivity()));
                            mVideoView.setVideoPath(photoPath);
                            mVideoView.requestFocus();
                            mVideoView.start();*/
                       //     new FileUploadTask().execute(photoPath.trim(), pathname,  finalfileName.trim(), fileExtention.trim(),  currentUser.userID.trim());
                       //     new UploadVideoFile().execute(pathname,  finalfileName.trim(), fileExtention.trim(),  currentUser.userID.trim(), photoPath.trim());
                    //        new CompressImage().execute(picturePath);

                            final ProgressDialog mDialog;

                            mDialog = new ProgressDialog(context);
                            mDialog.setMax(100);
                            mDialog.setCancelable(false);
                            mDialog.setMessage("Uploading " + finalfileName);
                            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isCancel = true;
                                    AndroidNetworking.forceCancelAll();
                                //    Toast.makeText(context, "Upload Cancel", Toast.LENGTH_SHORT).show();
                                }
                            });
                            mDialog.setProgress(0);

                            mDialog.show();

                            AndroidNetworking.upload(ServerConfig.PDF_Upload_Video_URL)
                                    .addMultipartFile("FileData",new File(photoPath))
                                    .addMultipartParameter("FolderPath",pathname)
                                    .addMultipartParameter("FileName",finalfileName)
                                    .addMultipartParameter("FileExt", fileExtention)
                                    .addMultipartParameter("CreatedByUserID", currentUser.userID)
                                    .setTag("upload video")
                                    .setPriority(Priority.HIGH)
                                    .build()
                                    .setUploadProgressListener(new UploadProgressListener() {
                                        @Override
                                        public void onProgress(long bytesUploaded, long totalBytes) {
                                            int percent = (int)(100.0*(double)bytesUploaded/totalBytes + 0.5);
                                            mDialog.setProgress(percent);
                                            mDialog.setMessage("Uploading " + AppUtils.bytesIntoHumanReadable(bytesUploaded)
                                                    + "/" + AppUtils.bytesIntoHumanReadable(totalBytes) + " " + finalfileName+ "." + fileExtention);
                                        //  L.printError("Progress : " + bytesUploaded);
                                        }
                                    })
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            mDialog.dismiss();
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                            builder1.setTitle("Success");
                                            builder1.setMessage("File Uploaded Successfully");
                                            builder1.setCancelable(true);
                                            builder1.setPositiveButton(
                                                    "Close",
                                                    (dialog, id) -> {
                                                        getGalleryFolders();
                                                        dialog.cancel();
                                                    });
                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();
                                        //    Toast.makeText(context, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            L.printError("Response : " + response);
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            if(isCancel)
                                            {
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                builder1.setTitle("Error");
                                                builder1.setMessage("Uploading has been canceled");
                                                builder1.setCancelable(true);
                                                builder1.setPositiveButton(
                                                        "Close",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });

                                                AlertDialog alert11 = builder1.create();
                                                alert11.show();

                                            }else
                                            {
                                                mDialog.dismiss();
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                                builder1.setTitle("Error");
                                                builder1.setMessage(anError.getErrorBody());
                                                builder1.setCancelable(true);
                                                builder1.setPositiveButton(
                                                        "Close",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });

                                                AlertDialog alert11 = builder1.create();
                                                alert11.show();
                                            }
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            });





    private static String getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    private static String getFileSizeKiloBytes(File file) {
        return (double) file.length() / 1024 + "  kb";
    }

    private static String getFileSizeBytes(File file) {
        return file.length() + " bytes";
    }



    private void checkNotificationsPermission() {
        if (isNotificationsPermissionGranted()) {
           startVideoUploadingService();
        } else {
            requestNotificationsPermission();
        }
    }

    private boolean isNotificationsPermissionGranted() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotificationsPermission() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            startVideoUploadingService();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

   public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (manager != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    for (String service : processInfo.pkgList) {
                        if (service.equals(serviceClass.getName())) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startVideoUploadingService();
                }
            });


    public void startVideoUploadingService(){
        if (!isServiceRunning(getActivity(),UploadVideoService.class)){
            Intent i = new Intent(getActivity(), UploadVideoService.class);
            i.putExtra("data", json.toString());
            i.setAction("start");
            getActivity().startService(i);
        }else
        {
            Toast.makeText(context, "File Uploading Already running, Please wait for complete.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSite() {
        sitesArrayList = new ArrayList<>();
        if (currentUser.sites != null) {
            for (Site si : currentUser.sites) {
                if (si.id != 0) {
                    sitesArrayList.add(si);
                }
            }
        }

        if (sitesArrayList != null && sitesArrayList.size() > 0) {
            try {
                Site dataModel = sitesArrayList.get(0);
                //    sessionManager.saveSelectedSite(dataModel);
                siteSpinner.setText(dataModel.name);
                selectedSite = dataModel;
                //    loadUser();
                getUsers(dataModel.id);
                //    getGalleryFolders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        siteSpinner.setOnClickListener(v -> {
            if (sitesArrayList.size() > 1) {
                showSiteSelectionDialog();
            }
        });
        //    spinner1.setText("Site One");
    }

    private void getUsers(int siteid){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("siteID", siteid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AndroidNetworking.post(ServerConfig.Load_Users_By_Site_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("Users")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        L.printInfo(response.toString());
                                        ObjectMapper om = new ObjectMapper();
                                        try {
                                            usersArrayList = new ArrayList<>();
                                            ArrayList<UserItem>   sitesArrayList = om.readValue(response.toString(), new TypeReference<List<UserItem>>(){});
                                            usersArrayList.addAll(sitesArrayList);
                                            loadUser();
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

    private void loadUser() {
        if (usersArrayList != null && usersArrayList.size() > 0) {
            int index = 0;
            for (int i = 0; i < usersArrayList.size(); i++) {
                if (usersArrayList.get(i).id == Integer.parseInt(currentUser.userID)) {
                    index = i;
                }
            }

            UserItem dataModel = usersArrayList.get(index);
            userSpinner.setText(dataModel.name);
            selectedUserId = String.valueOf(dataModel.id);
            selectedUserName = dataModel.name;
            getGalleryFolders();

        }

        userSpinner.setOnClickListener(v -> {
            if (usersArrayList.size() > 1) {
                showUserSelectionDialog();
            }
        });
        //    spinner1.setText("Site One");
    }

    public void showUserSelectionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.user_list_dailog, null);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setView(customLayout);

        ListView listView = customLayout.findViewById(R.id.list);
        UserSpinnerAdapter adapter = new UserSpinnerAdapter(context, usersArrayList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserItem dataModel = usersArrayList.get(position);
                userSpinner.setText(dataModel.name);
                selectedUserId = String.valueOf(dataModel.id);
                selectedUserName = dataModel.name;
                if(dirPath != null)
                {
                    dirPath.clear();
                    back.setVisibility(View.GONE);
                }
                getGalleryFolders();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showSiteSelectionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.site_selection_dailog, null);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setView(customLayout);

        ListView listView = customLayout.findViewById(R.id.list);
        SiteSpinnerAdapter adapter = new SiteSpinnerAdapter(context, sitesArrayList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Site dataModel = sitesArrayList.get(position);
                    //    sessionManager.saveSelectedSite(dataModel);
                    siteSpinner.setText(dataModel.name);
                    selectedSite = dataModel;
                    getUsers(dataModel.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}