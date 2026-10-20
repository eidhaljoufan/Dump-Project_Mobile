package id.jhodie.portofolioprojek;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webViewPortfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webViewPortfolio = findViewById(R.id.webViewPortfolio);

        WebSettings webSettings = webViewPortfolio.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webViewPortfolio.setWebViewClient(new WebViewClient());

        webViewPortfolio.loadUrl("file:///android_asset/portfolio.html");
    }

    @Override
    public void onBackPressed() {
        if (webViewPortfolio.canGoBack()) {
            webViewPortfolio.goBack();
        } else {
            super.onBackPressed();
        }
    }
}