package com.abhiandroid.quizgameapp.views;

/**
 * Developed by AbhiAndroid.com
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.abhiandroid.quizgameapp.R;
import com.abhiandroid.quizgameapp.interfaces.WebViewClickListener;
import com.abhiandroid.quizgameapp.utils.AppLog;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyWebViewPanel extends RelativeLayout implements View.OnTouchListener, Handler.Callback {
    LayoutInflater mLayoutInflater;
    public Context mContext;
    View view, temp_bg_frame;
    WebView webView;
    String data;
    WebViewClickListener clickListener;
    private static final int CLICK_ON_WEBVIEW = 1;
    private static final int CLICK_ON_URL = 2;
    private final Handler handler = new Handler(this);
    private WebViewClient client;
    private static boolean webview_isClickable=true;

    public MyWebViewPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * Initializing Activity and context instances.
         */
        mContext = getApplicationContext();
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mLayoutInflater.inflate(R.layout.option_webview_layout, this);

        /**
         * Initialize Views.
         */
        webView = findViewById(R.id.webview);
        temp_bg_frame = findViewById(R.id.temp_bg_frame);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setTextSize(WebSettings.TextSize.SMALLER);
/////////////////////
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        // wv.setBackgroundColor(0);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setBackgroundColor(Color.TRANSPARENT);
        /////////////////////////

        webView.setOnTouchListener(this);
        client = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                handler.sendEmptyMessage(CLICK_ON_URL);
                return false;
            }
        };
        //Set "Client" on webview
        webView.setWebViewClient(new WebClient());

    }

    class WebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            webView.loadUrl("javascript:AndroidFunction.resize(document.body.scrollHeight)");
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            handler.sendEmptyMessage(CLICK_ON_URL);
            return false;
        }
    }

    /**
     * WebView interface to communicate with Javascript
     */
    public class WebAppInterface {

        @JavascriptInterface
        public void resize(final float height) {
            float webViewHeight = (height * getResources().getDisplayMetrics().density);
            //webViewHeight is the actual height of the WebView in pixels as per device screen density
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.webview && event.getAction() == MotionEvent.ACTION_DOWN) {
            handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 0);
        }
        return false;
    }


    @Override
    public boolean handleMessage(Message msg) {

         if(webview_isClickable)
        clickListener.onClick();
        AppLog.getInstance().printLog(mContext, "webview clicked");
        return false;
    }


    /**
     * Load HTML data in webview
     *
     * @param data
     */
    public void loadDataInWebView(String data) {
       // webView.loadData("<html><body  align='center'>" + data + "</body></html>", "text/html", "UTF-8");
       // webView.loadDataWithBaseURL(null, "<html><body  align='center'>" + data + "</body></html>", "text/html", "utf-8", null);
       this.data=data;

       webView.loadDataWithBaseURL(null, "<html><body  align='center'>" + data + "</body></html>", "text/html; charset=utf-8", "utf-8", null);

    }


    /**
     * Disable/Enable clicklistener for all views after click
     */
    public void setClickable(boolean isClickable) {
        webView.setClickable(isClickable);
        webview_isClickable = isClickable;
    }


    /**
     * Set webview background color transparent and showing color on temprory frame.
     * Set Temprory Frame background color. Just used to set background color only.
     *
     * @param color
     */
    public void setBgColor(int color) {
      //  webView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        GradientDrawable bgShape = (GradientDrawable) temp_bg_frame.getBackground();
        bgShape.setColor(color);

    }
    public void setTextColor(){



        //webView.loadDataWithBaseURL(null, text, "text/html; charset=utf-8", "utf-8", null);
        webView.loadDataWithBaseURL(null, "<font color=\"#ffffff\" align='center'>"+data+"</font>", "text/html; charset=utf-8", "utf-8", null);
    }
    /**
     * Set webview background color.
     *
     * @param color
     */
    public void setBgColor(String color) {

       // webView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
     //   webView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        GradientDrawable bgShape = (GradientDrawable) temp_bg_frame.getBackground();
        bgShape.setColor(getResources().getColor(R.color.colorPrimary));
    }

    /**
     * Set click listener on webview window.
     *
     * @param clickListener
     */
    public void setOnClickWindow(WebViewClickListener clickListener) {

        this.clickListener = clickListener;
    }

}

/**
 * Developed by AbhiAndroid.com
 */

