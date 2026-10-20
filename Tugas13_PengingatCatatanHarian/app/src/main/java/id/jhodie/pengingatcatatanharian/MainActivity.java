package id.jhodie.pengingatcatatanharian;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText edtCatatan;
    private TimePicker timePicker;
    private Button btnSimpan;

    public static final String CHANNEL_ID = "catatan_channel";
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCatatan = findViewById(R.id.edtCatatan);
        timePicker = findViewById(R.id.timePicker);
        btnSimpan = findViewById(R.id.btnSimpan);

        createNotificationChannel();
        requestNotificationPermission();

        if (getIntent() != null && getIntent().getBooleanExtra("show_dialog", false)) {
            String catatan = getIntent().getStringExtra("catatan");
            showReadDialog(catatan);
        }

        btnSimpan.setOnClickListener(v -> setReminder());
    }

    private void setReminder() {
        String catatan = edtCatatan.getText().toString().trim();

        if (catatan.isEmpty()) {
            Toast.makeText(this, "Catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour;
        int minute;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("catatan", catatan);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Toast.makeText(this, "AlarmManager tidak tersedia!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );

                Toast.makeText(this, "Exact alarm belum diizinkan. Pengingat tetap dijadwalkan.", Toast.LENGTH_LONG).show();

                Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(settingsIntent);
                return;
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }

        Toast.makeText(this, "Pengingat berhasil diatur!", Toast.LENGTH_SHORT).show();
    }

    private void showReadDialog(String catatan) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Catatan")
                .setMessage("Catatan:\n\n" + catatan + "\n\nApakah catatan sudah dibaca?")
                .setPositiveButton("Sudah", (dialog, which) ->
                        Toast.makeText(this, "Catatan telah dibaca.", Toast.LENGTH_SHORT).show()
                )
                .setNegativeButton("Belum", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Catatan",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Channel untuk notifikasi pengingat catatan harian");

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }
}