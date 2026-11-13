package com.oor.hesabat;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.webkit.*;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {
  private WebView webView;
  private ValueCallback<Uri[]> fileCallback;
  private static final int FILE_REQUEST = 1001;

  public class AndroidBridge {
    @JavascriptInterface public void saveFile(String name, String data) {
      try {
        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists()) dir.mkdirs();
        File out = new File(dir, name.replaceAll("[^\\p{L}\\p{N}._-]", "_"));
        FileOutputStream fos = new FileOutputStream(out);
        fos.write(bytes); fos.close();
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "تم حفظ الملف في التنزيلات", Toast.LENGTH_LONG).show());
      } catch (Exception e) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "تعذر حفظ الملف", Toast.LENGTH_LONG).show());
      }
    }
    @JavascriptInterface public void printPage() {
      runOnUiThread(() -> {
        PrintManager pm = (PrintManager)getSystemService(PRINT_SERVICE);
        PrintDocumentAdapter adapter = webView.createPrintDocumentAdapter("حسابات أور");
        pm.print("حسابات أور", adapter, new PrintAttributes.Builder().build());
      });
    }
  }

  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT <= 28 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2001);
    webView = new WebView(this);
    setContentView(webView);
    WebSettings ws = webView.getSettings();
    ws.setJavaScriptEnabled(true);
    ws.setDomStorageEnabled(true);
    ws.setAllowFileAccess(true);
    ws.setAllowContentAccess(true);
    ws.setSupportZoom(false);
    ws.setBuiltInZoomControls(false);
    webView.addJavascriptInterface(new AndroidBridge(), "Android");
    webView.setWebChromeClient(new WebChromeClient() {
      @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
        if (fileCallback != null) fileCallback.onReceiveValue(null);
        fileCallback = callback;
        Intent intent = params.createIntent();
        try { startActivityForResult(intent, FILE_REQUEST); return true; }
        catch (Exception e) { fileCallback = null; return false; }
      }
    });
    webView.setWebViewClient(new WebViewClient());
    webView.loadUrl("file:///android_asset/index.html");
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == FILE_REQUEST && fileCallback != null) {
      Uri[] results = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
      fileCallback.onReceiveValue(results); fileCallback = null;
    }
  }
  @Override public void onBackPressed() {
    if (webView.canGoBack()) webView.goBack(); else super.onBackPressed();
  }
}