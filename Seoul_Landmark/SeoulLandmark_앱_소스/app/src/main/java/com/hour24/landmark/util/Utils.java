package com.hour24.landmark.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.hour24.landmark.R;
import com.hour24.landmark.constant.PermissionConst;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static float getDPfromPX(Context context, int dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);

        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            int idx = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static boolean isCheckPermission(Activity activity, String[] permissionStr, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionStr.length != 0) {
                ArrayList<String> deniedPermissionList = new ArrayList<>();
                int grantedPermissionCnt = 0;
                for (int i = 0; i < permissionStr.length; i++) {
                    if (ContextCompat.checkSelfPermission(activity, permissionStr[i]) == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissionCnt++;
                    } else {
                        deniedPermissionList.add(permissionStr[i]);
                    }
                }

                if (grantedPermissionCnt == permissionStr.length) {
                    return true;
                } else {
                    if (deniedPermissionList.size() != 0) {
                        String[] permissionsList = new String[deniedPermissionList.size()];
                        permissionsList = deniedPermissionList.toArray(permissionsList);
                        ActivityCompat.requestPermissions(activity, permissionsList, requestCode);
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public static void isPermissionDeniedNeverAskAgain(Context context, int requestCode, boolean isMoveSetting, String msg) {

        Activity activity = (Activity) context;

        if (requestCode == PermissionConst.PERMISSION_CAMERA_UPLOAD) {
            // 카메라
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 다시묻지않기 체크
                if (msg == null) {
                    Toast.makeText(context, context.getString(R.string.permission_denied_camera), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

                if (isMoveSetting) {
                    context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + context.getPackageName())));
                }
                return;
            }
        } else if (requestCode == PermissionConst.PERMISSION_DOCUMENT_GALLERY_UPLOAD) {
            // 갤러리 (저장권한)
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 다시묻지않기 체크
                if (msg == null) {
                    Toast.makeText(context, context.getString(R.string.permission_denied_camera), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

                if (isMoveSetting) {
                    context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + context.getPackageName())));
                }
                return;
            }
        }
    }

    public static Map<String, Object> getScreenSize(Context context) {

        Map<String, Object> result = null;

        try {

            result = new HashMap<>();

            WindowManager windowManager = ((Activity) context).getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            // since SDK_INT = 1;
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            double x = Math.pow(width / displayMetrics.xdpi, 2);
            double y = Math.pow(height / displayMetrics.ydpi, 2);
            double screenInch = Math.sqrt(x + y);

            float density = displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / density;
            float dpHeight = displayMetrics.widthPixels / density;

            result.put("screenInch", screenInch);
            result.put("width", width);
            result.put("height", height);
            result.put("dpWidth", (int) dpWidth);
            result.put("dpHeight", (int) dpHeight);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Bitmap bitmapDecodeResize(File file, int size) {

        try {

            Bitmap bitmap;

            // bitmap을 불러올때 파일이 큰 경우 Out of Memory가 나올 수 있다.
            // 불러오기 전에 미리 사이즈를 측정해서 사이즈 변환을 한다.
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getPath(), options1);

            int bitmapWidth = options1.outWidth;
            int inSampleSize = 1;

            // 가로기준
            inSampleSize = Math.round((float) bitmapWidth / (float) size);

            // 사이즈 처리하여 bitmap 반환
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeFile(file.getPath(), options2);
            bitmap = bitmapRotate(file, bitmap);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * bitmap을 회전
     *
     * @param bitmap
     */
    public static Bitmap bitmapRotate(File file, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(file.getPath());
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int exifDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotate(bitmap, exifDegree);
            return bitmap;
        } catch (Exception e) {
            return bitmap;
        }
    }

    private static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

     private static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    public static Bitmap bitmapScaleResize(Bitmap bitmap, int size) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth;
        int resizedHeight;

        // 가로기준
        resizedWidth = size;
        resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);

        try {
            bitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, true);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
        }

        return bitmap;
    }

    public static boolean replaceFragment(FragmentManager fragmentManager, Fragment fragment) {
        try {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit);
            ft.replace(android.R.id.content, fragment, fragment.getClass().getName());
            ft.addToBackStack(fragment.getClass().getName());
            ft.commitAllowingStateLoss();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap setStringToBitmap(String base64) {
        try {
            byte[] decodedBytes = Base64.decode(
                    base64.substring(base64.indexOf(",") + 1),
                    Base64.DEFAULT
            );
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String setBitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLongToStringFormat(long time, String dateFormat) {
        try {
            DateFormat format = new SimpleDateFormat(dateFormat);
            return format.format(time);
        } catch (Exception e) {
            return "";
        }

    }

}