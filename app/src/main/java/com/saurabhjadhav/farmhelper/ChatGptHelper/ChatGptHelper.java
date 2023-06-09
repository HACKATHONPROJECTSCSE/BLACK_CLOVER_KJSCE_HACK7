package com.saurabhjadhav.farmhelper.ChatGptHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.saurabhjadhav.farmhelper.R;

import java.util.Locale;
import java.util.UUID;

public class ChatGptHelper extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    Intent data;
    TextToSpeech textToSpeech;

    ImageView mic,speak;

    int paragraphCount;
    public String USER_AGENT = "(Android " + Build.VERSION.RELEASE + ") Chrome/110.0.5481.63 Mobile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat_gpt_helper);

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);

        mic = findViewById(R.id.mic);
        speak = findViewById(R.id.speak);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please Wait...");
        paragraphCount = 1;

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString(USER_AGENT);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMicPressed();
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSpeakerPressed();
            }
        });


        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("https://chat.openai.com");

        someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    data = result.getData();
                }
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {

            }

            @Override
            public void onError(String utteranceId) {

            }
        };

        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                progressBar.setProgress(newProgress);
                progressDialog.show();
                if (newProgress == 100) {
                    progressDialog.dismiss();
                }

                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();

        } else {
            closeApp();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                closeApp();
                break;
            case R.id.mic:
                onMicPressed();
                break;
            case R.id.speak:
                onSpeakerPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSpeakerPressed() {
        String allParagraph, paragraphLength;

        try {

            allParagraph = "var paragraphs = document.getElementsByTagName('p');"
                    + "var combinedText='';"
                    + "for (var i =" + paragraphCount + "-1; i < paragraphs.length; i++) {"
                    + "combinedText+= paragraphs[i].textContent;"
                    + " }combinedText;" ;

            webView.evaluateJavascript(allParagraph, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                    String paragraphString = value;
                    String utteranceId = UUID.randomUUID().toString();
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.speak(paragraphString, TextToSpeech.QUEUE_FLUSH, null, utteranceId);

                }
            });
            paragraphLength = "(function() { return document.getElementsByTagName('p').length; })();";
            webView.evaluateJavascript(paragraphLength, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    paragraphCount = Integer.parseInt(value);

                }
            });


        } catch (Exception e) {
            Toast.makeText(ChatGptHelper.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onMicPressed() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something");

        try {
            someActivityResultLauncher.launch(intent);
            webView.requestFocus();
        } catch (Exception e) {
            Toast.makeText(ChatGptHelper.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void closeApp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Exit ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.requestFocus();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            String str = data.getStringArrayListExtra("android.speech.extra.RESULTS").get(0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(function() { var d = document.getElementsByTagName('textarea').length; document.getElementsByTagName('textarea')[d-1].value='");
            stringBuilder.append(str);
            stringBuilder.append("';document.querySelector('button.absolute').disabled = false;");
            stringBuilder.append("document.querySelector('button.absolute').click(); })();");
            webView.evaluateJavascript(stringBuilder.toString(), null);
        } catch (Exception e) {
            Toast.makeText(ChatGptHelper.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}