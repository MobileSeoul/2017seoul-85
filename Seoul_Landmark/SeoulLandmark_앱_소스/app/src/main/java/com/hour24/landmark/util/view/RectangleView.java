package com.hour24.landmark.util.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Vertex;
import com.hour24.landmark.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RectangleView extends ImageView {

    private Context context;

    private ImageView imageView;

    private Canvas canvas;
    private Paint paintDefault;
    private Bitmap bitmap;

    private int width = 0;
    private int height = 0;

    public RectangleView(Context context) {
        this(context, null);
    }

    public RectangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void init(ImageView imageView) {
        this.imageView = imageView;

        // imageView 사이즈 저장
        width = imageView.getWidth();
        height = imageView.getHeight();

        paintInit();
        canvasInit();

    }

    // Drawing Init
    private void paintInit() {
        // Draw 되기 전 페인트
        paintDefault = new Paint();
        paintDefault.reset();
        paintDefault.setColor(Color.parseColor("#e5ffffff"));
        paintDefault.setStyle(Paint.Style.STROKE);
        paintDefault.setStrokeWidth((int) Utils.getDPfromPX(context, 3));
    }

    // Canvas Init
    private void canvasInit() {

        try {
            // width, height 가 0일경우 처리
            width = (width == 0) ? getWidth() : width;
            height = (height == 0) ? getHeight() : height;

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 리셋 - 초기 인식 상태로 돌림
    public void reset() {

        // 지우개
        Paint paintClear = new Paint();
        paintClear.setColor(Color.TRANSPARENT);
        paintClear.setAlpha(0);
        paintClear.setAntiAlias(true);
        paintClear.setDither(true);
        paintClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Rect rect = new Rect(0, 0, width, height);
        canvas.drawRect(rect, paintClear);

        invalidate();

    }

    // 인식된 결과에 박스처리
    public void setBoundingPoly(List<EntityAnnotation> entityAnnotations) {

        try {

            // get Vertexes
            if (entityAnnotations != null) {
                for (EntityAnnotation annotation : entityAnnotations) {

                    boolean isNull = false;

                    List<Vertex> vertexes = annotation.getBoundingPoly().getVertices();
                    // left = x 가장 작은값
                    // top = y 가장 작은값
                    // right = x 가장 큰값
                    // bottom = y 가장 큰
                    List<Integer> minX = new ArrayList<Integer>();
                    List<Integer> minY = new ArrayList<Integer>();
                    List<Integer> maxX = new ArrayList<Integer>();
                    List<Integer> maxY = new ArrayList<Integer>();

                    for (Vertex vertex : vertexes) {
                        // x, y 둘중 하나라도 null 일경우 isNull true
                        if (vertex.getX() == null || vertex.getY() == null) {
                            isNull = true;
                        } else {
                            minX.add(vertex.getX());
                            minY.add(vertex.getY());
                            maxX.add(vertex.getX());
                            maxY.add(vertex.getY());
                        }
                    }

                    // isNull 이 false 일때
                    if (!isNull) {
                        int left = Collections.min(minX);
                        int right = Collections.min(minY);
                        int top = Collections.max(maxX);
                        int bottom = Collections.max(maxY);

                        // Make Rect Object
                        Rect rect = new Rect();

                        // rect 이 그려질수 있는 조건 판단
                        rect.set((int) Utils.getDPfromPX(context, left),
                                (int) Utils.getDPfromPX(context, right),
                                (int) Utils.getDPfromPX(context, top),
                                (int) Utils.getDPfromPX(context, bottom));

                        // draw Rect
                        canvas.drawRect(rect, paintDefault);
                        invalidate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
