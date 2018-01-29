package com.hour24.landmark.model;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import com.google.api.services.vision.v1.model.WebEntity;
import com.google.api.services.vision.v1.model.WebImage;
import com.google.api.services.vision.v1.model.WebPage;


import java.util.List;

import lombok.Data;

@Data
public class VisionModel {

    private int position;
    private int viewType;
    private String header;

    private EntityAnnotation landmarkAnnotation;
    private WebPage pagesWithMatchingImage;
    private List<WebImage> fullMatchingImages;
    private List<WebImage> visuallySimilarImage;
    private WebEntity webEntity;

    // image Match
    private boolean isCrawling = false;
    private String pageUrl;
    private String headTitle;
    private String headFavicon;
    private String ogTitle;
    private String ogDescription;
    private String ogImage;

}
