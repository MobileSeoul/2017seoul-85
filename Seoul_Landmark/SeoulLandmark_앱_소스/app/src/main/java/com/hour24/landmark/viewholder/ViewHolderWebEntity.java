package com.hour24.landmark.viewholder;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.WebEntity;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;

import com.hour24.landmark.fragment.WebFragment;
import com.hour24.landmark.model.VisionModel;
import com.hour24.landmark.util.Utils;


public class ViewHolderWebEntity {

    private MainActivity activity;
    private View view;
    private FragmentManager fragmentManager;

    private LinearLayout main;
    private TextView description;

    public ViewHolderWebEntity(MainActivity activity, View view, FragmentManager fragmentManager) {
        this.activity = activity;
        this.view = view;
        this.fragmentManager = fragmentManager;

        initLayout();
    }

    private void initLayout() {
        main = (LinearLayout) view.findViewById(R.id.main);
        description = (TextView) view.findViewById(R.id.description);
    }

    public void bind(int position, VisionModel record) {
        final WebEntity data = record.getWebEntity();
        description.setText(data.getDescription());

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.google.co.kr/search?q=" + data.getDescription();

                WebFragment fragment = new WebFragment();
                Bundle bundle = new Bundle();
                bundle.putString(MainConst.WEB_SEARCH_URL, url);
                fragment.setArguments(bundle);
                Utils.replaceFragment(fragmentManager, fragment);
            }
        });
    }
}
