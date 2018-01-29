package com.hour24.landmark.model;

import android.graphics.Bitmap;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.WebEntity;
import com.google.api.services.vision.v1.model.WebImage;
import com.google.api.services.vision.v1.model.WebPage;

import java.util.List;

import lombok.Data;

@Data
public class HistoryModel {

    private String title;
    private String date;
    private Bitmap image;

    private boolean isBinding;
}
