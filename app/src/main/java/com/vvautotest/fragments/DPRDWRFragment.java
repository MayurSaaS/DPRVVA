package com.vvautotest.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vvautotest.R;
import com.vvautotest.activities.UploadPDFActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DPRDWRFragment extends Fragment {

    @BindView(R.id.pdfView)
    CardView pdfView;
    @BindView(R.id.uploadPDFBtn)
    CardView uploadPDFBtn;
    public DPRDWRFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_d_p_r_d_w_r, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.uploadPDFBtn)
    public void uploadPDF()
    {
        Intent intent = new Intent(getActivity(), UploadPDFActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.pdfView)
    public void openPDF()
    {
        Uri path = null;
        try {
            path = Uri.parse("http://shortcodeblogs.com/sh_365.pdf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try
        {
            getActivity().startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText( getActivity(), "NO Pdf Viewer", Toast.LENGTH_SHORT).show();
        }
    }

    private void CopyAssetsbrochure() {
        AssetManager assetManager = getActivity().getAssets();
        String[] files = null;
        try
        {
            files = assetManager.list("");
        }
        catch (IOException e)
        {
            Log.e("tag", e.getMessage());
        }
        for(int i=0; i<files.length; i++)
        {
            String fStr = files[i];
            if(fStr.equalsIgnoreCase("abc.pdf"))
            {
                InputStream in = null;
                OutputStream out = null;
                try
                {
                    in = assetManager.open(files[i]);
                    out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + files[i]);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                    break;
                }
                catch(Exception e)
                {
                    Log.e("tag", e.getMessage());
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}