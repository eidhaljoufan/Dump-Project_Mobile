package id.jhodie.Tugas18;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView textMaterial;
    private String currentMaterial = "Silakan pilih materi terlebih dahulu.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMath = findViewById(R.id.btnMath);
        Button btnLanguage = findViewById(R.id.btnLanguage);
        Button btnScience = findViewById(R.id.btnScience);
        Button btnRead = findViewById(R.id.btnRead);
        Button btnStop = findViewById(R.id.btnStop);
        textMaterial = findViewById(R.id.textMaterial);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("id", "ID"));
            }
        });

        btnMath.setOnClickListener(v -> {
            currentMaterial = "Matematika dasar. Dua tambah tiga sama dengan lima. Penjumlahan adalah proses menggabungkan dua angka.";
            textMaterial.setText(currentMaterial);
            speak("Materi matematika dipilih.");
        });

        btnLanguage.setOnClickListener(v -> {
            currentMaterial = "Bahasa Indonesia. Sinonim adalah kata yang memiliki arti mirip. Antonim adalah kata yang memiliki arti berlawanan.";
            textMaterial.setText(currentMaterial);
            speak("Materi bahasa dipilih.");
        });

        btnScience.setOnClickListener(v -> {
            currentMaterial = "Sains dasar. Gaya adalah tarikan atau dorongan yang dapat membuat benda bergerak.";
            textMaterial.setText(currentMaterial);
            speak("Materi sains dipilih.");
        });

        btnRead.setOnClickListener(v -> speak(currentMaterial));

        btnStop.setOnClickListener(v -> {
            if (tts != null && tts.isSpeaking()) {
                tts.stop();
                Toast.makeText(this, "Suara dihentikan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}