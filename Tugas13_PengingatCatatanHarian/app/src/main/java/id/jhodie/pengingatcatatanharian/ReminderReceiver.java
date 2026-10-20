package id.jhodie.pengingatcatatanharian;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        String catatan = intent.getStringExtra("catatan");

        if (catatan == null || catatan.trim().isEmpty()) {
            catatan = "Ada catatan yang perlu dibaca.";
        }

        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.putExtra("show_dialog", true);
        openIntent.putExtra("catatan", catatan);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                2,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                MainActivity.CHANNEL_ID
        )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Pengingat Catatan Harian")
                .setContentText(catatan)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(catatan))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}