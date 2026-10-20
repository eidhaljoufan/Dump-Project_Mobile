package id.jhodie.latihan132;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DialogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String taskName = getIntent().getStringExtra("task_name");

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Tugas")
                .setMessage("Apakah Anda sudah menyelesaikan tugas: " + taskName + "?")
                .setPositiveButton("Ya", (dialog, which) -> finish())
                .setNegativeButton("Belum", (dialog, which) -> finish())
                .setCancelable(false).show();
    }
}