package com.example.kalkulatormbg;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText etAngkaPertama, etAngkaKedua;
    private RadioGroup radioGroupOperasi;
    private RadioButton rbTambah, rbKurang, rbKali, rbBagi;
    private Button btnHitung;
    private TextView tvHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sambungkan elemen UI
        etAngkaPertama = findViewById(R.id.etAngkaPertama);
        etAngkaKedua = findViewById(R.id.etAngkaKedua);
        radioGroupOperasi = findViewById(R.id.radioGroupOperasi);
        rbTambah = findViewById(R.id.rbTambah);
        rbKurang = findViewById(R.id.rbKurang);
        rbKali = findViewById(R.id.rbKali);
        rbBagi = findViewById(R.id.rbBagi);
        btnHitung = findViewById(R.id.btnHitung);
        tvHasil = findViewById(R.id.tvHasil);

        // Listener tombol hitung
        btnHitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitung();
            }
        });
    }

    // Method untuk melakukan perhitungan
    private void hitung() {
        String input1 = etAngkaPertama.getText().toString();
        String input2 = etAngkaKedua.getText().toString();

        // Validasi input
        if (input1.isEmpty() || input2.isEmpty()) {
            Toast.makeText(this, "Mohon masukkan kedua angka", Toast.LENGTH_SHORT).show();
            return;
        }

        double angkaPertama = Double.parseDouble(input1);
        double angkaKedua = Double.parseDouble(input2);
        double hasil = 0;
        boolean operasiValid = false;

        // Cek operasi yang dipilih
        int idDipilih = radioGroupOperasi.getCheckedRadioButtonId();
        if (idDipilih == rbTambah.getId()) {
            hasil = angkaPertama + angkaKedua;
            operasiValid = true;
        } else if (idDipilih == rbKurang.getId()) {
            hasil = angkaPertama - angkaKedua;
            operasiValid = true;
        } else if (idDipilih == rbKali.getId()) {
            hasil = angkaPertama * angkaKedua;
            operasiValid = true;
        } else if (idDipilih == rbBagi.getId()) {
            if (angkaKedua == 0) {
                Toast.makeText(this, "Pembagi tidak boleh nol", Toast.LENGTH_SHORT).show();
                return;
            }
            hasil = angkaPertama / angkaKedua;
            operasiValid = true;
        }

        // Tampilkan hasil
        if (operasiValid) {
            tvHasil.setText("Hasil: " + hasil);
        } else {
            Toast.makeText(this, "Pilih operasi yang valid", Toast.LENGTH_SHORT).show();
        }
    }
}
