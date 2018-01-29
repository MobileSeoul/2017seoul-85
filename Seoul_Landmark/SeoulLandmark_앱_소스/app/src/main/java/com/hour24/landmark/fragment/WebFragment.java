package com.hour24.landmark.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.hour24.landmark.R;
import com.hour24.landmark.activity.MainActivity;
import com.hour24.landmark.constant.MainConst;
import com.hour24.landmark.util.WebViewSettings;

public class WebFragment extends BaseFragment implements View.OnClickListener {

    private MainActivity activity;

    private View rootView;
    private ProgressBar progressBar;
    private WebView webView;
    private FloatingActionButton fabShare;

    private String url;

    public WebFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        initLayout(view);
        initVariable();
        eventListener();
    }

    private void initLayout(View view) {
        webView = (WebView) view.findViewById(R.id.web_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        fabShare = (FloatingActionButton) view.findViewById(R.id.fab_share);

        WebViewSettings.setWebViewSettings(activity, webView, progressBar);

        fabShare.setOnClickListener(this);
    }

    private void initVariable() {

        Bundle bundle = getArguments();
        url = bundle.getString(MainConst.WEB_SEARCH_URL);
        webView.loadUrl(url);
    }

    private void eventListener() {
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                //This is the filter
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        getFragmentManager().popBackStack();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_share:
                String currentUrl = webView.getUrl();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, currentUrl);
                Intent chooser = Intent.createChooser(intent, getString(R.string.web_share));
                startActivity(chooser);
                break;
        }
    }
}
