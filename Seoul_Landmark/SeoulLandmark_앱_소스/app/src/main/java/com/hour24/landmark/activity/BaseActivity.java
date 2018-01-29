package com.hour24.landmark.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hour24.landmark.singleton.GetImageSingleton;
import com.hour24.landmark.R;
import com.hour24.landmark.constant.PermissionConst;
import com.hour24.landmark.util.Utils;


public class BaseActivity extends AppCompatActivity {

    private Context context;
    private Uri uri;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
        getSupportActionBar().hide();

        progressDialog = new Dialog(context, R.style.DialogTransparent);
        progressDialog.addContentView(new ProgressBar(context),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionConst.PERMISSION_CAMERA_UPLOAD:
                // 카메라 (카메라, 저장공간)
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Utils.isPermissionDeniedNeverAskAgain(context, requestCode, true, getString(R.string.permission_denied_camera));
                            return;
                        }
                    }
                    getImageCamera();
                }
                break;

            case PermissionConst.PERMISSION_DOCUMENT_GALLERY_UPLOAD:
                // 갤러리 (카메라, 저장공간)
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Utils.isPermissionDeniedNeverAskAgain(context, requestCode, true, getString(R.string.permission_denied_camera));
                            return;
                        }
                    }
                    getImageGallery();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GetImageSingleton.REQ_PIC_INTENT_GALLERY: {
                if (resultCode == RESULT_OK) {
                    getRequestImage(requestCode, data.getData());
                }
            }
            break;

            case GetImageSingleton.REQ_PIC_INTENT_CAMERA: {
                if (resultCode == RESULT_OK) {
                    getRequestImage(requestCode, uri);
                }
            }
            break;
        }
    }

    public void getImage(int requestCode) {
        if (requestCode == GetImageSingleton.REQ_PIC_INTENT_CAMERA) {
            if (Utils.isCheckPermission((Activity) context,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionConst.PERMISSION_CAMERA_UPLOAD)) {
                getImageCamera();
            }
        }

        if (requestCode == GetImageSingleton.REQ_PIC_INTENT_GALLERY) {
            if (Utils.isCheckPermission(BaseActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionConst.PERMISSION_DOCUMENT_GALLERY_UPLOAD)) {
                getImageGallery();
            }
        }
    }

    // 카메라로 이미지 Get
    private void getImageCamera() {

        // get URI
        uri = GetImageSingleton.getInstance((Activity) context).initImageUrlForCamera();
        Intent intent = GetImageSingleton.getInstance((Activity) context).getCameraIntent(uri);

        if (intent == null) {
            Toast.makeText(context, getString(R.string.toast_not_supported_camera), Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(intent, GetImageSingleton.REQ_PIC_INTENT_CAMERA);
        }
    }

    private void getImageGallery() {
        // Get Image From Gallery
        Intent intent = GetImageSingleton.getInstance((Activity) context).getGalleryIntent();
        startActivityForResult(intent, GetImageSingleton.REQ_PIC_INTENT_GALLERY);
    }

    // 이미지를 가져옴
    public void getRequestImage(final int requestCode, final Uri uri) {

    }
}