package id.jhodie.latihan10_2;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private List<Map<String, Object>> cart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.addJavascriptInterface(new WebAppInterface(),
                "android");
        webView.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view,
                                        WebResourceError error) {
                Toast.makeText(MainActivity.this, "Gagal memuat halaman", Toast.LENGTH_SHORT).show();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int
                    newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void addToCart(String id, String name, int price,
                              int quantity) {
            if (quantity > 10) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Maksimal 10 item",
                                Toast.LENGTH_SHORT).show());
                return;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("id", id);
            item.put("name", name);
            item.put("price", price);
            item.put("quantity", quantity);
            cart.add(item);
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, name + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show());
        }
    }
}
