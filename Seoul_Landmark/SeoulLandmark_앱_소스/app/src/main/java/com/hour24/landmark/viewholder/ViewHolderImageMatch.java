package com.hour24.landmark.viewholder;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.WebPage;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.fragment.WebFragment;
import com.hour24.landmark.model.VisionModel;
import com.hour24.landmark.util.Utils;
import com.hour24.landmark.util.view.CircleTransform;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewHolderImageMatch {

    private MainActivity activity;
    private View view;
    private FragmentManager fragmentManager;

    private WebPage data;

    private LinearLayout llMain;
    private LinearLayout llContent;
    private LinearLayout llNonPass;
    private LinearLayout llPass;
    private ProgressBar progress;
    private ImageView ivHeadFavicon;
    private ImageView ivOgImage;
    private TextView tvNonPassUrl;
    private TextView tvHeadTitle;
    private TextView tvOgTitle;
    private TextView tvOgDescription;

    private int imageWidth;
    private int faviconWidth;
    private int position;

    public ViewHolderImageMatch(MainActivity activity, View view, FragmentManager fragmentManager) {
        this.activity = activity;
        this.view = view;
        this.fragmentManager = fragmentManager;

        initLayout();
        initVariable();
    }

    private void initLayout() {
        llMain = (LinearLayout) view.findViewById(R.id.ll_main);
        llContent = (LinearLayout) view.findViewById(R.id.ll_content);
        llNonPass = (LinearLayout) view.findViewById(R.id.ll_non_pass);
        llPass = (LinearLayout) view.findViewById(R.id.ll_pass);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        ivOgImage = (ImageView) view.findViewById(R.id.iv_og_image);
        ivHeadFavicon = (ImageView) view.findViewById(R.id.iv_head_favicon);
        tvNonPassUrl = (TextView) view.findViewById(R.id.tv_non_pass_url);
        tvHeadTitle = (TextView) view.findViewById(R.id.tv_head_title);
        tvOgTitle = (TextView) view.findViewById(R.id.tv_og_title);
        tvOgDescription = (TextView) view.findViewById(R.id.tv_og_description);
    }

    private void initVariable() {
        imageWidth = (int) Utils.getDPfromPX(activity, 130);
        faviconWidth = (int) Utils.getDPfromPX(activity, 15);
    }

    public void bind(int position, final VisionModel record) {
        this.position = position;
        setData(record);
    }

    private void setData(final VisionModel data) {
        Log.e("sjjang", data.toString());

        if (data != null && data.isCrawling()) {

            String headFavicon = "";
            String ogImage = "";

            try {
                llContent.setVisibility(View.VISIBLE);

                String headTitle = setViewGone(tvHeadTitle, data.getHeadTitle());
                headFavicon = setViewGone(ivHeadFavicon, data.getHeadFavicon());
                String ogTitle = setViewGone(tvOgTitle, data.getOgTitle());
                String ogDescription = setViewGone(tvOgDescription, data.getOgDescription());
                ogImage = setViewGone(ivOgImage, data.getOgImage());

                if (ogTitle == null) {
                    llPass.setVisibility(View.GONE);
                    llNonPass.setVisibility(View.VISIBLE);
                    tvNonPassUrl.setText(data.getPageUrl());
                } else {
                    llPass.setVisibility(View.VISIBLE);
                    llNonPass.setVisibility(View.GONE);
                    tvHeadTitle.setText(headTitle);
                    tvOgTitle.setText(ogTitle);
                    tvOgDescription.setText(ogDescription != null ? ogDescription.trim() : ogDescription);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Picasso.with(activity).load(ogImage).centerCrop().resize(imageWidth, imageWidth).transform(new CircleTransform()).into(ivOgImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(activity).load(R.drawable.img_intro).centerCrop().resize(imageWidth, imageWidth).into(ivOgImage);
                }
            });

            Picasso.with(activity).load(headFavicon).centerCrop().resize(faviconWidth, faviconWidth).into(ivHeadFavicon, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(activity).load(R.drawable.ic_link_black_48dp).centerCrop().resize(faviconWidth, faviconWidth).into(ivHeadFavicon);
                }
            });

            llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebFragment fragment = new WebFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(MainConst.WEB_SEARCH_URL, data.getPageUrl());
                    fragment.setArguments(bundle);
                    Utils.replaceFragment(fragmentManager, fragment);
                }
            });

            progress.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.VISIBLE);
        }

    }

    private String setViewGone(View view, String content) {
        if (content != null && content.length() > 0) {
            view.setVisibility(View.VISIBLE);
            return content;
        } else {
            view.setVisibility(View.GONE);
            return null;
        }
    }
}
