package com.hour24.landmark.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.api.services.vision.v1.model.WebImage;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;

import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.util.view.PhotoViewPager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class PhotoViewFragment extends BaseFragment {

    private MainActivity activity;

    private PhotoViewPager viewPager;
    private FloatingActionButton fabSearch;
    private Dialog progressDialog;

    private List<WebImage> records;
    private int position = 0;
    private int selectPosition = 0;

    private ViewPagerAdapter adapter;

    public PhotoViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int color = ContextCompat.getColor(getActivity(), R.color.color_land_mark);
            window.setStatusBarColor(color);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        initLayout(view);
    }

    private void initLayout(View view) {
        fabSearch = (FloatingActionButton) view.findViewById(R.id.fab_search);
        viewPager = (PhotoViewPager) view.findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        viewPager.setCurrentItem(position, false);

        // 이미지 다운로드를 받기위한 position
        selectPosition = position;

        // progress
        progressDialog = new Dialog(activity, R.style.DialogTransparent);
        progressDialog.addContentView(new ProgressBar(activity),
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        // viewpager adapter
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncHttpClient client = new AsyncHttpClient();
                client.get(records.get(selectPosition).getUrl(), new FileAsyncHttpResponseHandler(activity) {

                    @Override
                    public void onProgress(long bytesWritten, long totalSize) {
                        if (!progressDialog.isShowing()) {
                            progressDialog.show();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, File response) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        activity.callVisionApi(MainConst.REQ_RE_SEARCH_PHOTOVIEW, file);
                        getFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void setRecords(List<WebImage> records, int position) {
        this.records = records;
        this.position = position;
    }

    public class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            Picasso.with(activity).load(records.get(position).getUrl()).into(photoView);
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
