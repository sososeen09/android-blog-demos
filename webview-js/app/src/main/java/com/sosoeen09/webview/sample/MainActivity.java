package com.sosoeen09.webview.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

    findViewById(R.id.button).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainActivity.this, WebViewEpubActivity.class));
      }
    });
  }

  class JsInterface {

    @JavascriptInterface
    public void onWebviewToNative(String username, String password) {
      //注意，该回调发生在子线程  thread: Thread[JavaBridge,7,main]
      Log.e(TAG,
          "onWebviewToNative: " + String.format("username: %s password: %s", username, password)
              + " thread: " + Thread.currentThread());
      //子线程中可以Toast是因为用到了Binder机制
      Toast.makeText(MainActivity.this,
          String.format("username: %s password: %s", username, password), Toast.LENGTH_SHORT)
          .show();
    }

    @JavascriptInterface
    public String onNativeToWebview(String text) {
      //注意，该回调发生在子线程  thread: Thread[JavaBridge,7,main]
      Log.e(TAG, "onNativeToWebView: " + String.format("text: %s", text) + "  thread: " + Thread
          .currentThread());

      return "native 传递数据到 webview";
    }


  }
}
