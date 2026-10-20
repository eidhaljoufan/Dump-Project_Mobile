package com.example.intentprojectapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Menyambungkan variabel dengan UI
        EditText editTextNIM = findViewById(R.id.editTextNIM);
        EditText editTextNama = findViewById(R.id.editTextNama);
        EditText editTextJurusan = findViewById(R.id.editTextJurusan);

        Button buttonLanjut = findViewById(R.id.buttonLanjut);
        // Set listener untuk tombol
        buttonLanjut.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        // Mengambil input dari EditText
            String nim = editTextNIM.getText().toString();
            String nama = editTextNama.getText().toString();
            String jurusan = editTextJurusan.getText().toString();
        // Membuat Intent untuk pindah ke SecondActivity
            Intent intent = new Intent (MainActivity.this, SecondActivity.class);
            intent.putExtra("NIM MAHASISWA", nim);
            intent.putExtra("NAMA MAHASISWA", nama);
            intent.putExtra("JURUSAN_MAHASISWA", jurusan);
        // Menjalankan SecondActivity
            startActivity(intent);
            }
        });

        Button buttonOpenWeb = findViewById(R.id.buttonOpenWeb);
        buttonOpenWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://my.unpam.ac.id/";
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                var packageManager = getPackageManager();
                if (webIntent.resolveActivity(packageManager) != null) {
                    Intent chooserIntent = Intent.createChooser(webIntent, "Pilih aplikasi untuk membuka halaman web");
                    startActivity(chooserIntent);
                } else {
                    // Handle jika tidak ada aplikasi yang dapat menangani intent
                    Toast.makeText(MainActivity.this, "Tidak ada aplikasi browser yang tersedia. Silahkan install aplikasi browser terlebih dahulu.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}