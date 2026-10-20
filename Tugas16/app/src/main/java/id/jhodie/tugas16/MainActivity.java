package id.jhodie.tugas16;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etNama, etUsia, etHobi;
    Button btnSimpan, btnReset;
    TextView tvHasil;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNama = findViewById(R.id.etNama);
        etUsia = findViewById(R.id.etUsia);
        etHobi = findViewById(R.id.etHobi);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnReset = findViewById(R.id.btnReset);

        tvHasil = findViewById(R.id.tvHasil);

        preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        loadData();

        btnSimpan.setOnClickListener(v -> saveData());

        btnReset.setOnClickListener(v -> clearData());
    }

    private void saveData() {

        String nama = etNama.getText().toString().trim();
        String usia = etUsia.getText().toString().trim();
        String hobi = etHobi.getText().toString().trim();

        if (nama.isEmpty() || usia.isEmpty() || hobi.isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("nama", nama);
        editor.putString("usia", usia);
        editor.putString("hobi", hobi);

        editor.apply();

        tampilkanData();

        Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
    }

    private void loadData() {

        String nama = preferences.getString("nama", "");
        String usia = preferences.getString("usia", "");
        String hobi = preferences.getString("hobi", "");

        etNama.setText(nama);
        etUsia.setText(usia);
        etHobi.setText(hobi);

        tampilkanData();
    }

    private void tampilkanData() {

        String nama = preferences.getString("nama", "-");
        String usia = preferences.getString("usia", "-");
        String hobi = preferences.getString("hobi", "-");

        tvHasil.setText(
                "Nama : " + nama +
                        "\nUsia : " + usia +
                        "\nHobi : " + hobi
        );
    }

    private void clearData() {

        preferences.edit().clear().apply();

        etNama.setText("");
        etUsia.setText("");
        etHobi.setText("");

        tvHasil.setText("Belum ada data.");

        Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
    }
}