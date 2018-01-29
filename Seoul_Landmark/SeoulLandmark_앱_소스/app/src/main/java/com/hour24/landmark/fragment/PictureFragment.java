package com.hour24.landmark.fragment;

import android.animation.Animator;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.singleton.GetImageSingleton;
import com.hour24.landmark.singleton.VisionSingleton;
import com.hour24.landmark.util.Utils;
import com.hour24.landmark.util.view.RectangleView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;


public class PictureFragment extends BaseFragment implements View.OnClickListener {

    public interface GetTakePictureCallBack {
        public void getPicture(int requestCode, Uri uri);
    }

    private MainActivity activity;

    private ImageView ivPreview;
    private FloatingActionButton fabPictureGroup;
    private FloatingActionButton fabTakeCamera;
    private FloatingActionButton fabTakeGallery;
    private RectangleView rectangleView;

    private boolean isPictureGroupOpen = false;
    private File file;
    public static Bitmap bitmap;

    public PictureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        initLayout(view);
        initVariable();
        eventListener();

//        callVisionApi(MainConst.REQ_SAMPLE, null);

    }

    private void initLayout(View view) {

        ivPreview = (ImageView) view.findViewById(R.id.iv_preview);
        fabPictureGroup = (FloatingActionButton) view.findViewById(R.id.fab_picture_group);
        fabTakeCamera = (FloatingActionButton) view.findViewById(R.id.fab_take_camera);
        fabTakeGallery = (FloatingActionButton) view.findViewById(R.id.fab_take_gallery);

        rectangleView = (RectangleView) view.findViewById(R.id.rectangle_view);

        View[] views = {fabPictureGroup, fabTakeCamera, fabTakeGallery};
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    private void initVariable() {
    }

    private void eventListener() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_picture_group:
                isPictureGroupOpen = isPictureGroupOpen ? false : true;
                setPictureAnimate("camera", isPictureGroupOpen);
                setPictureAnimate("gallery", isPictureGroupOpen);
                break;

            case R.id.fab_take_camera:
                activity.getTakePicture(GetImageSingleton.REQ_PIC_INTENT_CAMERA, new GetTakePictureCallBack() {
                    @Override
                    public void getPicture(int requestCode, Uri uri) {
                        callVisionApi(requestCode, uri);
                    }
                });
                break;

            case R.id.fab_take_gallery:
                activity.getTakePicture(GetImageSingleton.REQ_PIC_INTENT_GALLERY, new GetTakePictureCallBack() {
                    @Override
                    public void getPicture(int requestCode, Uri uri) {
                        callVisionApi(requestCode, uri);
                    }
                });
                break;
        }
    }

    private void setPictureAnimate(String type, final boolean isOpen) {

        int duration = 100;
        int translate = fabPictureGroup.getWidth() + 20;

        if ("camera".equals(type)) {
            fabTakeCamera
                    .animate()
                    .translationX(isOpen ? translate * -1 : 0)
                    .setDuration(duration)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (isOpen) {
                                fabTakeCamera.setVisibility(View.VISIBLE);
                                fabTakeGallery.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (!isOpen) {
                                fabTakeCamera.setVisibility(View.GONE);
                                fabTakeGallery.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
        } else {
            fabTakeGallery
                    .animate()
                    .translationX(isOpen ? translate : 0)
                    .setDuration(duration)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }

    }

    // 이미지를 Vision API 로 보냄
    public void callVisionApi(final int requestCode, final Object object) {

        try {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    bitmap = null;

                    rectangleView.init(ivPreview);

                    int width = (int) Utils.getScreenSize(activity).get("width");
                    int dpWidth = (int) Utils.getScreenSize(activity).get("dpWidth");

                    if (requestCode == GetImageSingleton.REQ_PIC_INTENT_GALLERY) {
                        file = new File(Utils.getRealPathFromURI(activity, (Uri) object));
                        bitmap = Utils.bitmapDecodeResize(file, width);
                    } else if (requestCode == GetImageSingleton.REQ_PIC_INTENT_CAMERA) {
                        file = new File(GetImageSingleton.getInstance(activity).getImageUrlForCamera());
                        bitmap = Utils.bitmapDecodeResize(file, width);
                    } else if (requestCode == MainConst.REQ_RE_SEARCH_HISTORY) {
                        bitmap = (Bitmap) object;
                    } else if (requestCode == MainConst.REQ_RE_SEARCH_PHOTOVIEW) {
                        bitmap = Utils.bitmapDecodeResize((File) object, width);
                    } else if (requestCode == MainConst.REQ_SAMPLE) {
                        bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.img_sample);
                    } else {
                        bitmap = null;
                    }

                    if (bitmap == null) {
                        return;
                    }

                    ivPreview.setImageBitmap(bitmap);

                    // 이미지 품질 개선을 위해 사이즈를 화면에 보여지는 것 보다 키워 보냄
                    bitmap = Utils.bitmapScaleResize(bitmap, (int) (dpWidth));

                    // 파일삭제
                    if (file != null && file.exists()) {
                        file.delete();
                    }

                    requestVisionApi();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestVisionApi() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    // CallVision Task
                    new AsyncTask<Object, Void, BatchAnnotateImagesResponse>() {

                        @Override
                        protected void onPreExecute() {
                            onProgressShow();
                        }

                        @Override
                        protected BatchAnnotateImagesResponse doInBackground(Object... params) {
                            return VisionSingleton.getInstance(activity).callCloudVision(bitmap);
                        }

                        protected void onPostExecute(BatchAnnotateImagesResponse response) {

                            rectangleView.init(ivPreview);
                            rectangleView.setBoundingPoly(response.getResponses().get(0).getLandmarkAnnotations());

                            // Result Fragment 에 데이터 전송
                            activity.setLandMarkData(response);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity activity = (MainActivity) getActivity();
                                    activity.setMoveViewPager(1);
                                }
                            }, 2000);

                            setHistoryData(bitmap, response);

                            // progress dismiss
                            onProgressDismiss();
                        }
                    }.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    private void setHistoryData(Bitmap bitmap, BatchAnnotateImagesResponse response) {

        try {
            String base64 = Utils.setBitmapToString(bitmap);

            SharedPreferences pref = activity.getSharedPreferences(MainConst.LANDMARK_HISTORY, MODE_PRIVATE);
            String data = pref.getString(MainConst.LANDMARK_HISTORY_DATA_KEY, "");

            JSONArray jsonArray;
            if ("".equals(data)) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(data);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", base64);
            String title = "Landmark";
            if (response.getResponses().get(0).getLandmarkAnnotations().get(0).getDescription() != null) {
                title = response.getResponses().get(0).getLandmarkAnnotations().get(0).getDescription();
            }
            jsonObject.put("title", title);
            jsonObject.put("date", System.currentTimeMillis());

            jsonArray.put(jsonObject);

            // 데이터 저장
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(MainConst.LANDMARK_HISTORY_DATA_KEY, jsonArray.toString());
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
