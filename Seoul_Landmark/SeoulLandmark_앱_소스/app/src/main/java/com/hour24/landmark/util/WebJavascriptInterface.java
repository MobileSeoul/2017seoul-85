package com.hour24.landmark.util;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by N16326 on 2017. 8. 7..
 */

public class WebJavascriptInterface {

    private Context context;
    private WebView webView;

    public WebJavascriptInterface(Context context, WebView webView) {
        this.context = context;
        this.webView = webView;
    }

    @JavascriptInterface
    public void appLoadUrl(String url) {
        Log.e("sjjang", "appLoadUrl : " + url);
    }
}
