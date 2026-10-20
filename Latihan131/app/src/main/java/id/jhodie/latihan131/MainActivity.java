package id.jhodie.latihan131;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private static final String ACTION_ALARM = "id.jhodie.latihan12.ALARM_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        requestNotificationPermission();

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(ACTION_ALARM);

        pendingIntent = PendingIntent.getBroadcast(
                this,
                12,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        startButton.setOnClickListener(v -> {

            if (!isNotificationAllowed()) {
                Toast.makeText(this, "Izinkan notifikasi dulu!", Toast.LENGTH_SHORT).show();
                requestNotificationPermission();
                return;
            }

            long triggerTime = System.currentTimeMillis() + (60 * 1000);

            if (alarmManager == null) {
                Toast.makeText(this, "AlarmManager tidak tersedia!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Versi aman: tidak pakai setExact dulu biar tidak crash di Redmi/Android 14
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );

            Toast.makeText(
                    this,
                    "Pengingat aktif. Tunggu sekitar 1 menit.",
                    Toast.LENGTH_LONG
            ).show();
        });

        stopButton.setOnClickListener(v -> {
            if (alarmManager != null && pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                Toast.makeText(this, "Pengingat dihentikan!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNotificationAllowed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }
}