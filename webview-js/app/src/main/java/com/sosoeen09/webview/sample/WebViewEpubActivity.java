package com.sosoeen09.webview.sample;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class WebViewEpubActivity extends AppCompatActivity {

  private static final String TAG = "WebViewEpubActivity";
  WebViewEx webViewEx;

  long startTime;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web_view_epub);

    webViewEx = findViewById(R.id.webView);

//    webViewEx.setWebViewClient(new WebViewClient() {
//      @Override
//      public void onPageFinished(WebView view, String url) {
//        super.onPageFinished(view, url);
//        Log.e(TAG, "spend time: " + (System.currentTimeMillis() - startTime));
//      }
//
//      @Override
//      public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        super.onPageStarted(view, url, favicon);
//        startTime = System.currentTimeMillis();
//
//      }
//    });

//    webViewEx.loadUrl("file:///android_asset/hongloumeng/OEBPS/Text/Section0002.xhtml");
//    webViewEx.loadUrl("file:///android_asset/jimi/OPS/1.html");
    webViewEx.getSettings().setJavaScriptEnabled(true);
    webViewEx.loadUrl("https://wxpay.wxutil.com/mch/pay/h5.v2.php");

//    AssetManager assetManager = getAssets();
//    try {
//      // find InputStream for book
//      InputStream epubInputStream = assetManager
//          .open("fengtian.epub");
//
//      // Load Book from inputStream
//      Book book = (new EpubReader()).readEpub(epubInputStream);
//
//      // Log the book's authors
//      Log.i("epublib", "author(s): " + book.getMetadata().getAuthors());
//
//      // Log the book's title
//      Log.i("epublib", "title: " + book.getTitle());
//
//      // Log the book's coverimage property
//      Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage()
//          .getInputStream());
//      Log.i("epublib", "Coverimage is " + coverImage.getWidth() + " by "
//          + coverImage.getHeight() + " pixels");
//
//      // Log the tale of contents
//      logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
//    } catch (IOException e) {
//      Log.e("epublib", e.getMessage());
//    }

  }

  private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
    if (tocReferences == null) {
      return;
    }
    for (TOCReference tocReference : tocReferences) {
      StringBuilder tocString = new StringBuilder();
      for (int i = 0; i < depth; i++) {
        tocString.append("\t");
      }
      tocString.append(tocReference.getTitle());
      Log.i("epublib", tocString.toString());

      logTableOfContents(tocReference.getChildren(), depth + 1);
    }
  }
}
