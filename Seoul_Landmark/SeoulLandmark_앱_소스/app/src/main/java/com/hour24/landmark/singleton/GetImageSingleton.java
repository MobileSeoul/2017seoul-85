package com.hour24.landmark.singleton;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.hour24.landmark.BuildConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetImageSingleton {

    //Const
    public static final int REQ_PIC_INTENT_GALLERY = 9000;
    public static final int REQ_PIC_INTENT_CAMERA = 9001;

    //Variable
    private static Activity activity;
    private static GetImageSingleton instance;

    private static String imgPath = "";

    // Constructor
    private GetImageSingleton(Activity activity) {
        this.activity = activity;
    }

    // Getter
    public static synchronized GetImageSingleton getInstance(Activity activity) {
        if (instance == null) {
            instance = new GetImageSingleton(activity);
        }
        return instance;
    }

    public Intent getCameraIntent(Uri uri) {

        // Get Image From Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        // 카메라 컴퍼넌트 리턴
        return isPossibleCameraComponent(intent);
    }

    public Intent getGalleryIntent() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");

        // 카메라 컴퍼넌트 리턴
        return intent;
    }

    public Uri initImageUrlForCamera() {
        Uri imgUri = null;
        try {
            // Store image in dcim
            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "seoul_" + new Date().getTime() + ".png");

            // Android N File Permission Issue
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imgUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                imgUri = Uri.fromFile(file);
            }

            this.imgPath = file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
        return imgUri;
    }

    public String getImageUrlForCamera() {
        return this.imgPath;
    }

    public Intent isPossibleCameraComponent(Intent intent) {

        List<Intent> camIntentsList = new ArrayList<Intent>();
        List<ResolveInfo> listCam = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo res : listCam) {
            Intent finalIntent = new Intent(intent);
            finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            camIntentsList.add(finalIntent);
        }

        if (camIntentsList.size() == 0) {
            // 사용불가능
            return null;
        } else {
            // 사용가능
            return camIntentsList.get(0);
        }
    }
}
