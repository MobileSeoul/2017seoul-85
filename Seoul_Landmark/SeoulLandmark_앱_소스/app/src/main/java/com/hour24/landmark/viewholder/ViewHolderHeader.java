package com.hour24.landmark.viewholder;

import android.view.View;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.hour24.landmark.R;
import com.hour24.landmark.model.VisionModel;

public class ViewHolderHeader {


    private View view;
    private TextView header;

    public ViewHolderHeader(View view) {
        this.view = view;
        initLayout();
    }

    private void initLayout() {
        // header
        header = (TextView) view.findViewById(R.id.header);

    }

    public void bind(int position, VisionModel record) {
        EntityAnnotation data = record.getLandmarkAnnotation();
        header.setText(record.getHeader());
    }
}
