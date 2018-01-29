package com.hour24.landmark.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hour24.landmark.R;

public class WebViewSettings {

    public static void setWebViewSettings(Context context, WebView webView, ProgressBar progressBar) {

        setWebSettings(webView);
        setWebViewClient(context, webView, progressBar);
        setWebChromeClient(context, webView, progressBar);
        setAddJavascriptInterface(context, webView);

    }

    private static void setWebSettings(WebView webView) {

        // Setting - Script
        webView.getSettings().setJavaScriptEnabled(true);

        // Setting - Web
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportMultipleWindows(true);

        // Setting - Local Storage & Inner Database
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // zoom
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
    }

    private static void setWebViewClient(final Context context, WebView webView, final ProgressBar progressBar) {

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(final WebView view, String url, Bitmap favicon) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(final WebView view, final String url) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return shouldOverrideUrlLoading(view, url, null);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString(), request);
            }

            private boolean shouldOverrideUrlLoading(WebView view, String url, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }


            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                try {
                    if (context != null) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                                .setMessage(context.getString(R.string.web_certificate))
                                .setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        handler.proceed();
                                    }
                                })
                                .setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        handler.cancel();
                                    }
                                })
                                .setCancelable(false);
                        alertDialogBuilder.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void setWebChromeClient(final Context context, WebView webView, final ProgressBar progressBar) {

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progressBar != null) {
                    progressBar.setProgress(progress);
                }
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView webView = new WebView(view.getContext());
                view.addView(webView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(webView);
                resultMsg.sendToTarget();
                return true;
            }
        });
    }

    private static void setAddJavascriptInterface(Context context, WebView webView) {
        webView.addJavascriptInterface(new WebJavascriptInterface(context, webView), "android");
    }
}
