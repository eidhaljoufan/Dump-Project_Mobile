package id.jhodie.mynotificationapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private Button btnToast, btnSnackbar, btnDialog;
    private boolean isProcessDone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToast = findViewById(R.id.btnToast);
        btnSnackbar = findViewById(R.id.btnSnackbar);
        btnDialog = findViewById(R.id.btnDialog);

        btnToast.setOnClickListener(v -> {
            if (isProcessDone) {
                Toast.makeText(
                        MainActivity.this,
                        "Proses selesai!",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        MainActivity.this,
                        "Sedang memuat...",
                        Toast.LENGTH_LONG
                ).show();
            }

            isProcessDone = !isProcessDone;
        });

        btnSnackbar.setOnClickListener(v -> {
            Snackbar.make(v, "Aksi berhasil!", Snackbar.LENGTH_LONG)
                    .setAction("Batal", view -> Toast.makeText(
                            MainActivity.this,
                            "Aksi dibatalkan!",
                            Toast.LENGTH_SHORT
                    ).show())
                    .show();
        });

        btnDialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Konfirmasi");
            builder.setMessage("Apakah Anda ingin melanjutkan?");
            builder.setPositiveButton("Lanjut", (dialog, which) -> {
                Toast.makeText(
                        MainActivity.this,
                        "Berhasil!",
                        Toast.LENGTH_SHORT
                ).show();
            });

            builder.setNegativeButton("Batal", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });
    }
}