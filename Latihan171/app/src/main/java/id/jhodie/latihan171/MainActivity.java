package id.jhodie.latihan171;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private TextView tvResult;
    private TextView tvStatus;
    private boolean isListening = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION =
            101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = findViewById(R.id.tvResult);
        tvStatus = findViewById(R.id.tvStatus);

        // Memeriksa izin merekam audio
        checkRecordAudioPermission();
    }

    private void checkRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            initSpeechRecognizer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                initSpeechRecognizer();
            } else {
                Toast.makeText(this, "Izin merekam audio diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initSpeechRecognizer() {
        speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent = new
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL
                , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                tvStatus.setText("Status: Siap untuk menerima suara");
                Toast.makeText(MainActivity.this, "Siap untuk menerima suara", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                tvStatus.setText("Status: Sedang mendengarkan");
                Toast.makeText(MainActivity.this, "Mulai mendengarkan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                tvStatus.setText("Status: Selesai mendengarkan");
                        Toast.makeText(MainActivity.this, "Selesai mendengarkan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error) {
                String errorMessage = "Terjadi kesalahan: ";
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage += "Masalah audio";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage += "Masalah klien";
                        break;
                    case
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage += "Izin tidak mencukupi";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage += "Masalah jaringan";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage += "Tidak ada hasil yang cocok";
                        break;
                    case
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        errorMessage += "Layanan pengenalan sibuk";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        errorMessage += "Masalah server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage += "Suara tidak terdeteksi";
                        break;
                }
                Toast.makeText(MainActivity.this, errorMessage,
                        Toast.LENGTH_SHORT).show();
                tvStatus.setText("Status: Siap");

                findViewById(R.id.btnStart).setVisibility(View.VISIBLE);

                findViewById(R.id.btnStop).setVisibility(View.GONE);
                isListening = false;
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION
                        );
                if (voiceResults != null &&
                        !voiceResults.isEmpty()) {
                    tvResult.setText("Hasil: " + voiceResults.get(0));
                }
                tvStatus.setText("Status: Siap");

                findViewById(R.id.btnStart).setVisibility(View.VISIBLE);

                findViewById(R.id.btnStop).setVisibility(View.GONE);
                isListening = false;
            }

            @Override
            public void onPartialResults(Bundle partialResults)
            {}

            @Override
            public void onEvent(int eventType, Bundle params)
            {}
        });
    }

    public void startSpeechRecognition(View view) {
        if (!isListening) {
            speechRecognizer.startListening(recognizerIntent);

            findViewById(R.id.btnStart).setVisibility(View.GONE);

            findViewById(R.id.btnStop).setVisibility(View.VISIBLE);
            isListening = true;
        }
    }

    public void stopSpeechRecognition(View view) {
        if (isListening) {
            speechRecognizer.stopListening();

            findViewById(R.id.btnStart).setVisibility(View.VISIBLE);

            findViewById(R.id.btnStop).setVisibility(View.GONE);
            isListening = false;
        }
    }
}