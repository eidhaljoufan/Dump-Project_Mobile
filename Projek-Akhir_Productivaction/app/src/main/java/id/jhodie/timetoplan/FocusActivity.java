package id.jhodie.timetoplan;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class FocusActivity extends AppCompatActivity {

    private TextView txtTimer;
    private TextView txtMode;
    private Button btnStart;
    private Button btnPause;
    private Button btnReset;
    private Toolbar topBar;

    private CountDownTimer countDownTimer;
    private long timeLeft;
    private boolean timerRunning = false;
    private boolean isWorkMode = true;

    private static final long WORK_TIME = 25 * 60 * 1000;
    private static final long BREAK_TIME = 5 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        txtTimer = findViewById(R.id.txtTimer);
        txtMode = findViewById(R.id.txtMode);
        btnStart = findViewById(R.id.btnStartTimer);
        btnPause = findViewById(R.id.btnPauseTimer);
        btnReset = findViewById(R.id.btnResetTimer);
        topBar = findViewById(R.id.topBarFocus);

        topBar.setNavigationOnClickListener(v -> finish());

        timeLeft = WORK_TIME;
        updateTimerText();

        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnReset.setOnClickListener(v -> resetTimer());
    }

    private void startTimer() {
        if (timerRunning) return;

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                switchMode();
            }
        }.start();

        timerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
        }
    }

    private void resetTimer() {
        pauseTimer();
        timeLeft = isWorkMode ? WORK_TIME : BREAK_TIME;
        updateTimerText();
    }

    private void switchMode() {
        isWorkMode = !isWorkMode;
        timeLeft = isWorkMode ? WORK_TIME : BREAK_TIME;
        txtMode.setText(isWorkMode ? "Work Session" : "Break Session");
        txtMode.setTextColor(getResources().getColor(isWorkMode ? R.color.accent : R.color.primaryLight));
        
        Toast.makeText(this, isWorkMode ? "Time to focus!" : "Time for a break!", Toast.LENGTH_LONG).show();
        updateTimerText();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        txtTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
