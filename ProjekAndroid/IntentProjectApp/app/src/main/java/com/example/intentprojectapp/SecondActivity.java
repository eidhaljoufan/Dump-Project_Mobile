package com.example.intentprojectapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
// Menyambungkan TextView untuk menampilkan hasil
        TextView textViewHasil = findViewById(R.id.textViewHasil);
// Menerima data yang dikirim dari MainActivity
        Intent intent = getIntent();
        String nim = intent.getStringExtra("NIM MAHASISWA");
        String nama = intent.getStringExtra("NAMA MAHASISWA");
        String jurusan = intent.getStringExtra("JURUSAN MAHASISWA");
// Menampilkan hasil di TextView
        textViewHasil.setText ("NIM: " + nim + "\nNama: " + nama + "\nJurusan: " + jurusan);
    }
}