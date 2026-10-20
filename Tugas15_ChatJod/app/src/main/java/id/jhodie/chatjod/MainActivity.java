package id.jhodie.chatjod;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView scrollChat;
    private EditText edtPrompt;
    private Button btnSend;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String API_KEY = "AQ.Ab8RN6LAvvOGg5z_h80kuFh9SfEvqtq_AqblSqlZBV_zGH9R4Q";
    private static final String GEMINI_MODEL = "gemini-3.5-flash";
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + GEMINI_MODEL
                    + ":generateContent?key="
                    + API_KEY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatContainer = findViewById(R.id.chatContainer);
        scrollChat = findViewById(R.id.scrollChat);
        edtPrompt = findViewById(R.id.edtPrompt);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String prompt = edtPrompt.getText().toString().trim();

            if (prompt.isEmpty()) {
                Toast.makeText(this, "Pesan tidak boleh kosong.", Toast.LENGTH_SHORT).show();
                return;
            }

            edtPrompt.setText("");
            addMessage("You", prompt, true);
            addMessage("Gemini", "Sedang memproses...", false);

            sendPromptToGemini(prompt);
        });
    }

    private void sendPromptToGemini(String prompt) {
        new Thread(() -> {
            try {
                URL url = new URL(GEMINI_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);

                JSONObject textPart = new JSONObject();
                textPart.put("text", prompt);

                JSONArray partsArray = new JSONArray();
                partsArray.put(textPart);

                JSONObject contentObject = new JSONObject();
                contentObject.put("parts", partsArray);

                JSONArray contentsArray = new JSONArray();
                contentsArray.put(contentObject);

                JSONObject requestBody = new JSONObject();
                requestBody.put("contents", contentsArray);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestBody.toString().getBytes("UTF-8"));
                outputStream.close();

                int responseCode = connection.getResponseCode();

                BufferedReader reader;

                if (responseCode >= 200 && responseCode < 300) {
                    reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );
                } else {
                    reader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream())
                    );
                }

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                if (responseCode >= 200 && responseCode < 300) {
                    String result = parseGeminiResponse(response.toString());

                    mainHandler.post(() -> {
                        removeLastLoadingMessage();
                        addMessage("Gemini", result, false);
                    });

                } else {
                    mainHandler.post(() -> {
                        removeLastLoadingMessage();
                        addMessage(
                                "Error",
                                "Gagal menghubungi Gemini API.\nKode: " + responseCode + "\n\n" + response,
                                false
                        );
                    });
                }

            } catch (Exception e) {
                mainHandler.post(() -> {
                    removeLastLoadingMessage();
                    addMessage("Error", "Terjadi kesalahan: " + e.getMessage(), false);
                });
            }
        }).start();
    }

    private String parseGeminiResponse(String jsonResponse) {
        try {
            JSONObject root = new JSONObject(jsonResponse);

            JSONArray candidates = root.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);

            JSONObject content = firstCandidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");

            return parts.getJSONObject(0).getString("text");

        } catch (Exception e) {
            return "Respons berhasil diterima, tetapi gagal dibaca:\n" + jsonResponse;
        }
    }

    private void addMessage(String sender, String message, boolean isUser) {
        TextView textView = new TextView(this);

        textView.setText(sender + ":\n" + message);
        textView.setTextSize(14);
        textView.setPadding(14, 12, 14, 12);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 8, 0, 8);

        if (isUser) {
            textView.setTextColor(0xFFFFFFFF);
            textView.setBackgroundColor(0xFF007ACC);
            params.gravity = Gravity.END;
        } else {
            textView.setTextColor(0xFFD4D4D4);
            textView.setBackgroundColor(0xFF2D2D30);
            params.gravity = Gravity.START;
        }

        textView.setLayoutParams(params);
        textView.setMaxWidth(getResources().getDisplayMetrics().widthPixels - 120);

        chatContainer.addView(textView);
        scrollToBottom();
    }

    private void removeLastLoadingMessage() {
        int childCount = chatContainer.getChildCount();

        if (childCount > 0 && chatContainer.getChildAt(childCount - 1) instanceof TextView) {
            TextView lastView = (TextView) chatContainer.getChildAt(childCount - 1);

            if (lastView.getText().toString().contains("Sedang memproses...")) {
                chatContainer.removeViewAt(childCount - 1);
            }
        }
    }

    private void scrollToBottom() {
        scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
    }
}