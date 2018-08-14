package com.sosoeen09.webview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WebView_JS";
    WebViewEx mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.addJavascriptInterface(new JsInterface(), "jsInterface");

        mWebView.loadUrl("file:///android_asset/webview_js.html");

        Log.e(TAG, "onCreate: main thread name: " + Thread.currentThread());
    }

    class JsInterface {
        @JavascriptInterface
        public void onWebviewToNative(String username, String password) {
            //注意，该回调发生在子线程  thread: Thread[JavaBridge,7,main]
            Log.e(TAG, "onWebviewToNative: " + String.format("username: %s password: %s", username, password) + " thread: " + Thread.currentThread());
            //子线程中可以Toast是因为用到了Binder机制
            Toast.makeText(MainActivity.this, String.format("username: %s password: %s", username, password), Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public String onNativeToWebview(String text) {
            //注意，该回调发生在子线程  thread: Thread[JavaBridge,7,main]
            Log.e(TAG, "onNativeToWebView: " + String.format("text: %s", text) + "  thread: " + Thread.currentThread());

            return "native 传递数据到 webview";
        }


    }
}
