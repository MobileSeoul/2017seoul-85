package com.hour24.landmark.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.hour24.landmark.fragment.PictureFragment;
import com.hour24.landmark.R;
import com.hour24.landmark.fragment.LandmarkFragment;
import com.hour24.landmark.model.TitleModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Context context;

    private LinearLayout llTitleLayout;
    private RecyclerView rvTitle;
    private ViewPager viewPager;

    private Fragment viewPagerFragment;
    private PictureFragment pictureFragment;
    private LandmarkFragment landmarkFragment;

    private TitleAdapter titleAdapter;
    private List<TitleModel> titles;

    private PictureFragment.GetTakePictureCallBack getTakePictureCallBack;
    private LandmarkFragment.GetLandMarkDataCallBack getLandMarkDataCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        initLayout();
        initVariable();
        eventListener();

    }

    private void initLayout() {

        llTitleLayout = (LinearLayout) findViewById(R.id.ll_title_layout);
        rvTitle = (RecyclerView) findViewById(R.id.rv_title);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        setStatusBarColor(0, llTitleLayout);
    }

    private void initVariable() {

        titles = new ArrayList<TitleModel>();
        titles.add(new TitleModel(getString(R.string.title_list_picture), true));
        titles.add(new TitleModel(getString(R.string.title_list_land_mark), false));

        titleAdapter = new TitleAdapter();
        rvTitle.setAdapter(titleAdapter);
        rvTitle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        // ViewPage Adapter
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

    }

    private void eventListener() {

        // viewPager Listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitleFocus(position);
                setStatusBarColor(position, llTitleLayout);
                titleAdapter.notifyDataSetChanged();

                if (position == 1) {
                    if (landmarkFragment != null) {
                        if (PictureFragment.bitmap == null) {
                            landmarkFragment.setVisibleDataLayout(false);
                        } else {
                            landmarkFragment.setVisibleDataLayout(true);
                        }
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    // 이미지를 가져옴
    @Override
    public void getRequestImage(final int requestCode, final Uri uri) {
        if (uri != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getTakePictureCallBack != null) {
                        getTakePictureCallBack.getPicture(requestCode, uri);
                    } else {
                        Toast.makeText(context, getString(R.string.toast_image_get_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(context, getString(R.string.toast_image_get_fail), Toast.LENGTH_SHORT).show();
        }
    }

    // Get Image
    public void getTakePicture(int requestCode, PictureFragment.GetTakePictureCallBack getTakePictureCallBack) {
        this.getTakePictureCallBack = getTakePictureCallBack;
        getImage(requestCode);
    }

    public void getLandMarkData(LandmarkFragment.GetLandMarkDataCallBack getLandMarkDataCallBack) {
        this.getLandMarkDataCallBack = getLandMarkDataCallBack;
    }

    public void setLandMarkData(BatchAnnotateImagesResponse response) {
        if (getLandMarkDataCallBack != null) {
            getLandMarkDataCallBack.getLandMarkData(response);
        }
    }

    // 재검색, 히스토리검색
    public void callVisionApi(int requestCode, Object object) {
        if (pictureFragment != null) {
            viewPager.setCurrentItem(0, true);
            pictureFragment.callVisionApi(requestCode, object);
        }
    }

    public void setStatusBarColor(int position, View view) {
        int color = ContextCompat.getColor(context, R.color.color_picture);

        switch (position) {
            case 0:
                color = ContextCompat.getColor(context, R.color.color_picture);
                break;
            case 1:
                color = ContextCompat.getColor(context, R.color.color_land_mark);
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }

        view.setBackgroundColor(color);
    }

    public void setTitleFocus(int position) {
        for (int i = 0; i < titles.size(); i++) {
            TitleModel record = titles.get(i);
            if (i == position) {
                record.setFocus(true);
            } else {
                record.setFocus(false);
            }
        }
    }

    public void setMoveViewPager(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }

    public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder> {

        public TitleAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_title_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            TitleModel record = titles.get(position);

            holder.titleOn.setText(record.getTitle());
            holder.titleOff.setText(record.getTitle());

            if (record.isFocus()) {
                holder.titleOn.setVisibility(View.VISIBLE);
                holder.titleOff.setVisibility(View.GONE);
            } else {
                holder.titleOn.setVisibility(View.GONE);
                holder.titleOff.setVisibility(View.VISIBLE);
            }

            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTitleFocus(position);
                    notifyDataSetChanged();

                    viewPager.setCurrentItem(position, true);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return titles.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public LinearLayout main;
            public TextView titleOn;
            public TextView titleOff;

            public ViewHolder(View itemView) {
                super(itemView);
                main = (LinearLayout) itemView.findViewById(R.id.main);
                titleOn = (TextView) itemView.findViewById(R.id.title_on);
                titleOff = (TextView) itemView.findViewById(R.id.title_off);
            }
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    viewPagerFragment = new PictureFragment();
                    pictureFragment = (PictureFragment) viewPagerFragment;
                    break;
                case 1:
                    viewPagerFragment = new LandmarkFragment();
                    landmarkFragment = (LandmarkFragment) viewPagerFragment;
                    break;
                default:
            }
            return viewPagerFragment;
        }


        @Override
        public int getCount() {
            return 2;
        }
    }
}
