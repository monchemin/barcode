package com.devicom.vintest;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    Button okbutton;
    static final String SCAN = "com.devicom.vinreader";
    static final int SCAN_REQUEST = 1;
    WebView mWebView;
    String barCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //okbutton = findViewById(R.id.okbutton);
        mWebView = (WebView)findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "Android");

        /*
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://gvo_beta.autousagee.ca/test-vin/intent.html?vin=123456";
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(url));
                startActivity(urlIntent);
                finish();
            }
        }); */
        String url = "http://gvo_beta.autousagee.ca/test-vin/testwebview.html";
        //String url = "https://autopropulsion.com";
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new MyWebViewClient());

       // lunchScanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.clearCache(true);
    }

    public void lunchScanner() {
        try {
            //this intent is used to call start for bar code
            Intent in = new Intent(SCAN);
            in.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(in, 0);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText( this, "No scanner found", "Download Scanner code Activity?"," Yes", "No", Toast.LENGTH_LONG ).show();
            Toast.makeText(this, "soucis", Toast.LENGTH_SHORT).show();

        }
    }

    @JavascriptInterface
    public void scanner() {
        Toast.makeText( this, "Content:", Toast.LENGTH_LONG ).show();
        Intent scannerIntent = new Intent(SCAN);
        startActivityForResult(scannerIntent, SCAN_REQUEST);
    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent in ) {
            // TODO Auto-generated method stub
            /*
            if( requestCode == 0 ){
                if( resultCode == RESULT_OK ){
                    //use to get scan result
                    String contents = in.getStringExtra( "SCAN_RESULT" );
                    String format =  in.getStringExtra( "SCAN_RESULT_FORMAT" ) ;
                    Toast toast = Toast.makeText( this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG );
                    toast.show();
                }
            } */

            if( requestCode == SCAN_REQUEST ){
               // Toast.makeText(this, "ok : ", Toast.LENGTH_SHORT).show();
                if( resultCode == RESULT_OK ){

                     barCode = in.getStringExtra( "BARCODE" );

                    Toast.makeText(this, "barcode : " + barCode, Toast.LENGTH_SHORT).show();

                    JSONObject message = new JSONObject();
                    try {
                        message.put("barCode" , barCode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    mWebView.loadUrl("javascript:barCodeResult(" + message + ")");

                }
            }
        }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (Uri.parse(url).getHost().equals("autopropulsion.com")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
           // mWebView.loadUrl("javascript:barCodeResult(" + barCode + ")");

        }
    }
}
