package com.hour24.landmark.model;

import lombok.Data;

@Data
public class TitleModel {

    String title;
    boolean isFocus;

    public TitleModel(String title, boolean isFocus) {
        this.title = title;
        this.isFocus = isFocus;
    }
}
