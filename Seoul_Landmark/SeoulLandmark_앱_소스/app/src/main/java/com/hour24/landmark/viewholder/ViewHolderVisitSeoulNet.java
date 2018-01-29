package com.hour24.landmark.viewholder;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.fragment.WebFragment;
import com.hour24.landmark.model.VisionModel;
import com.hour24.landmark.util.Utils;


public class ViewHolderVisitSeoulNet {

    private MainActivity activity;
    private View view;
    private FragmentManager fragmentManager;

    private LinearLayout main;

    private EntityAnnotation data;

    public ViewHolderVisitSeoulNet(MainActivity activity, View view, FragmentManager fragmentManager) {
        this.activity = activity;
        this.view = view;
        this.fragmentManager = fragmentManager;

        initLayout();
    }

    private void initLayout() {
        main = (LinearLayout) view.findViewById(R.id.main);
    }

    public void bind(int position, VisionModel record) {
        data = record.getLandmarkAnnotation();

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://english.visitseoul.net/search?lang=en&searchTerm=";
                url += data.getDescription();
                WebFragment fragment = new WebFragment();
                Bundle bundle = new Bundle();
                bundle.putString(MainConst.WEB_SEARCH_URL, url);
                fragment.setArguments(bundle);
                Utils.replaceFragment(fragmentManager, fragment);
            }
        });
    }
}
