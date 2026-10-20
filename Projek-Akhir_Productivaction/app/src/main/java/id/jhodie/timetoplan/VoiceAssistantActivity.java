package id.jhodie.timetoplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class VoiceAssistantActivity extends AppCompatActivity {

    private Toolbar topBar;
    private Button btnPrev;
    private Button btnPlay;
    private Button btnNext;
    private Button btnStop;

    private TextView txtSection;
    private TextView txtProgress;
    private TextView txtStatus;
    private ProgressBar progressVoice;

    private TextToSpeech tts;

    private int currentIndex = 0;

    private final String[] sections = {

            "Selamat datang di ProductivAction.",

            "Schedule Planner membantu mengatur jadwal harian.",

            "Todo List membantu mengelola daftar tugas.",

            "Notes digunakan untuk menyimpan ide dan catatan penting.",

            "Focus Timer membantu belajar menggunakan metode Pomodoro.",

            "Random Task memberikan rekomendasi aktivitas produktif.",

            "Habit Insight akan menganalisis kebiasaan produktivitas pengguna."

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voice_assistant);

        topBar = findViewById(R.id.topBarVoice);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnStop = findViewById(R.id.btnStop);

        txtStatus = findViewById(R.id.txtStatus);
        txtSection = findViewById(R.id.txtSection);
        txtProgress = findViewById(R.id.txtProgress);

        progressVoice = findViewById(R.id.progressVoice);

        topBar.setNavigationOnClickListener(v -> finish());

        progressVoice.setMax(sections.length);

        updateUI();

        tts = new TextToSpeech(this, status -> {

            if(status == TextToSpeech.SUCCESS){

                tts.setLanguage(new Locale("id","ID"));

                applyVoiceSpeed();

            }

        });

        btnPrev.setOnClickListener(v -> previousSection());
        btnPlay.setOnClickListener(v -> speak());
        btnNext.setOnClickListener(v -> nextSection());

        btnStop.setOnClickListener(v -> {
            tts.stop();
            txtStatus.setText("Status : Stopped");
        });

    }

    private void speak(){

        if(currentIndex >= sections.length){
            currentIndex = 0;
            updateUI();
        }

        txtStatus.setText("Status : Playing");
        tts.speak(
                sections[currentIndex],
                TextToSpeech.QUEUE_FLUSH,
                null,
                "VOICE_SECTION"
        );
    }

    private void previousSection(){
        if(currentIndex > 0){
            currentIndex--;
            updateUI();
            speak();
        }
    }

    private void nextSection(){

        if(currentIndex < sections.length - 1){

            currentIndex++;

            updateUI();

            speak();

        }

    }

    private void updateUI(){
        txtSection.setText(sections[currentIndex]);
        txtProgress.setText((currentIndex + 1) + " / " + sections.length);
        progressVoice.setProgress(currentIndex + 1);
        txtStatus.setText("Status : Ready");
    }

    private void applyVoiceSpeed(){

        SharedPreferences pref =
                getSharedPreferences("settings", MODE_PRIVATE);

        String speed =
                pref.getString("voice_speed","normal");

        switch (speed){
            case "slow":
                tts.setSpeechRate(0.7f);
                break;

            case "fast":
                tts.setSpeechRate(1.3f);
                break;

            default:
                tts.setSpeechRate(1.0f);
        }

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {

                runOnUiThread(() -> {

                    txtStatus.setText("Status : Completed");

                    if(currentIndex < sections.length - 1){
                        currentIndex++;
                        updateUI();
                        speak();
                    }else{
                        txtStatus.setText("Status : Finished");
                    }

                });

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}