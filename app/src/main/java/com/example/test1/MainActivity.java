package com.example.test1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.test1.Translator;


public class MainActivity extends AppCompatActivity {

    WebView webView;
    Button translateButton;
    Button retranslateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        translateButton = findViewById(R.id.translateButton);
        retranslateButton = findViewById(R.id.retranslatebutton);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://kakuyomu.jp");

        retranslateButton.setOnClickListener(v-> {
            webView.evaluateJavascript(
                    "javascript:(" +
                            "function() {" +
                            "   const target = document.querySelector('.widget-episodeBody');" +
                            "   if (target) {" +
                            "       const text = target.innerText;" +
                            "       window.Android.translateText2(text);" +
                            "   } else {" +
                            "       alert('본문을 찾을 수 없습니다.');" +
                            "   }" +
                            "})()",
                    null
            );

        });
        translateButton.setOnClickListener(v -> {
            webView.evaluateJavascript(
                    "javascript:(" +
                            "function() {" +
                            "   const target = document.querySelector('.widget-episodeBody');" +
                            "   if (target) {" +
                            "       const text = target.innerText;" +
                            "       window.Android.translateText(text);" +
                            "   } else {" +
                            "       alert('본문을 찾을 수 없습니다.');" +
                            "   }" +
                            "})()",
                    null
            );
            retranslateButton.setVisibility(View.VISIBLE);
        });


    }

    class WebAppInterface {
        @JavascriptInterface
        public void translateText(String originalText) {
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, "실시간 번역 시작", Toast.LENGTH_SHORT).show()
            );

            // clear 기존 텍스트
            runOnUiThread(() -> webView.evaluateJavascript(
                    "document.querySelector('.widget-episodeBody').innerText = '';",
                    null
            ));

            StreamingTranslator.translate(originalText, new StreamingTranslator.StreamCallback() {
                @Override
                public void onChunk(String chunk) {
                    String safeChunk = chunk.replace("'", "\\'").replace("\n", "\\n");
                    runOnUiThread(() ->
                            webView.evaluateJavascript(
                                    "document.querySelector('.widget-episodeBody').innerText += '" + safeChunk + "';",
                                    null
                            )
                    );
                }

                @Override
                public void onComplete() {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "번역 완료", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "번역 오류: " + error, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }

        @JavascriptInterface
        public void translateText2(String originalText) {
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, "재 번역 시작", Toast.LENGTH_SHORT).show()
            );

            // clear 기존 텍스트
            runOnUiThread(() -> webView.evaluateJavascript(
                    "document.querySelector('.widget-episodeBody').innerText = '';",
                    null
            ));

            StreamingTranslator.retranslate(originalText, new StreamingTranslator.StreamCallback() {
                @Override
                public void onChunk(String chunk) {
                    String safeChunk = chunk.replace("'", "\\'").replace("\n", "\\n");
                    runOnUiThread(() ->
                            webView.evaluateJavascript(
                                    "document.querySelector('.widget-episodeBody').innerText += '" + safeChunk + "';",
                                    null
                            )
                    );
                }

                @Override
                public void onComplete() {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "번역 완료", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "번역 오류: " + error, Toast.LENGTH_LONG).show()
                    );
                }
            });
        }
    }


}
