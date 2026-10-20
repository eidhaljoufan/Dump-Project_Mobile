package id.jhodie.tugas19;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ResepAdapter.OnItemClickListener {
    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "resep.json";

    private RecyclerView recyclerView;
    private ResepAdapter adapter;
    private List<Resep> resepList;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);

        resepList = new ArrayList<>();
        adapter = new ResepAdapter(this, resepList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupSwipeToDelete();
        copyJsonFromAssetsIfNeeded();
        loadResepFromInternal();

        fabAdd.setOnClickListener(v -> showAddEditDialog(-1));
    }

    private void copyJsonFromAssetsIfNeeded() {
        File file = new File(getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            try {
                InputStream is = getAssets().open(FILE_NAME);
                byte[] buffer = new byte[is.available()];
                int bytesRead = is.read(buffer);
                is.close();

                if (bytesRead > 0) {
                    FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                    fos.write(buffer);
                    fos.close();
                    Log.d(TAG, "Seed JSON disalin ke internal storage");
                }
            } catch (Exception e) {
                Log.e(TAG, "Gagal menyalin seed JSON: " + e.getMessage(), e);
            }
        }
    }

    private void loadResepFromInternal() {
        resepList.clear();
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            int bytesRead = fis.read(buffer);
            fis.close();

            if (bytesRead > 0) {
                String jsonStr = new String(buffer, StandardCharsets.UTF_8);
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Resep resep = Resep.fromJsonObject(obj);
                    resepList.add(resep);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Gagal load JSON internal: " + e.getMessage(), e);
            Toast.makeText(this, "Gagal memuat data resep", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveResepToInternal() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Resep resep : resepList) {
                jsonArray.put(resep.toJsonObject());
            }
            String jsonStr = jsonArray.toString();
            FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(jsonStr.getBytes(StandardCharsets.UTF_8));
            fos.close();
            Log.d(TAG, "Data resep disimpan, total: " + resepList.size());
        } catch (Exception e) {
            Log.e(TAG, "Gagal simpan JSON internal: " + e.getMessage(), e);
            Toast.makeText(this, "Gagal menyimpan data resep", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAbsoluteAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    resepList.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    saveResepToInternal();
                    Toast.makeText(MainActivity.this, "Resep dihapus", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showAddEditDialog(int position) {
        boolean isEdit = (position >= 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Edit Resep" : "Tambah Resep");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_resep, null);
        EditText etNamaResep = view.findViewById(R.id.etNamaResep);
        EditText etBahan = view.findViewById(R.id.etBahan);
        EditText etInstruksi = view.findViewById(R.id.etInstruksi);
        EditText etKategori = view.findViewById(R.id.etKategori);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        LinearLayout layoutBahanList = view.findViewById(R.id.layoutBahanList);
        LinearLayout layoutLangkahList = view.findViewById(R.id.layoutLangkahList);
        Button btnTambahBahan = view.findViewById(R.id.btnTambahBahan);
        Button btnTambahLangkah = view.findViewById(R.id.btnTambahLangkah);

        List<String> bahanList = new ArrayList<>();
        List<Langkah> langkahList = new ArrayList<>();

        if (isEdit) {
            Resep resep = resepList.get(position);
            etNamaResep.setText(resep.getNamaResep());
            etKategori.setText(resep.getKategori().getNama());
            ratingBar.setRating((float) resep.getRating());

            bahanList.addAll(resep.getDaftarBahan());
            langkahList.addAll(resep.getLangkah());

            updateBahanView(layoutBahanList, bahanList);
            updateLangkahView(layoutLangkahList, langkahList);
        }

        builder.setView(view);
        builder.setPositiveButton("Simpan", null);
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String namaResep = etNamaResep.getText().toString().trim();
                String kategori = etKategori.getText().toString().trim();
                float rating = ratingBar.getRating();

                if (TextUtils.isEmpty(namaResep)) {
                    etNamaResep.setError("Nama resep tidak boleh kosong");
                    return;
                }

                if (bahanList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Minimal 1 bahan harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (langkahList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Minimal 1 langkah harus diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(kategori)) {
                    etKategori.setError("Kategori tidak boleh kosong");
                    return;
                }

                Kategori kategoriObj = new Kategori(kategori, kategori.toUpperCase().substring(0, Math.min(3, kategori.length())));

                if (isEdit) {
                    Resep resep = resepList.get(position);
                    resep.setNamaResep(namaResep);
                    resep.setDaftarBahan(bahanList);
                    resep.setLangkah(langkahList);
                    resep.setKategori(kategoriObj);
                    resep.setRating(rating);
                    adapter.notifyItemChanged(position);
                } else {
                    Resep newResep = new Resep(namaResep, bahanList, langkahList, kategoriObj, rating);
                    resepList.add(newResep);
                    adapter.notifyItemInserted(resepList.size() - 1);
                }

                saveResepToInternal();
                Toast.makeText(MainActivity.this, isEdit ? "Resep diupdate" : "Resep ditambahkan", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        btnTambahBahan.setOnClickListener(v -> {
            String bahan = etBahan.getText().toString().trim();
            if (!TextUtils.isEmpty(bahan)) {
                bahanList.add(bahan);
                updateBahanView(layoutBahanList, bahanList);
                etBahan.setText("");
            } else {
                Toast.makeText(this, "Masukkan bahan terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });

        btnTambahLangkah.setOnClickListener(v -> {
            String instruksi = etInstruksi.getText().toString().trim();
            if (!TextUtils.isEmpty(instruksi)) {
                langkahList.add(new Langkah(langkahList.size() + 1, instruksi));
                updateLangkahView(layoutLangkahList, langkahList);
                etInstruksi.setText("");
            } else {
                Toast.makeText(this, "Masukkan instruksi langkah terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void updateBahanView(LinearLayout layout, List<String> bahanList) {
        layout.removeAllViews();
        for (String bahan : bahanList) {
            TextView tv = new TextView(this);
            tv.setText("• " + bahan);
            tv.setPadding(16, 8, 16, 8);
            layout.addView(tv);
        }
    }

    private void updateLangkahView(LinearLayout layout, List<Langkah> langkahList) {
        layout.removeAllViews();
        for (Langkah langkah : langkahList) {
            TextView tv = new TextView(this);
            tv.setText(langkah.getUrutan() + ". " + langkah.getInstruksi());
            tv.setPadding(16, 8, 16, 8);
            layout.addView(tv);
        }
    }

    @Override
    public void onItemClick(int position) {
        showAddEditDialog(position);
    }

    @Override
    public void onItemLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Resep");
        builder.setMessage("Apakah Anda yakin ingin menghapus resep ini?");

        builder.setPositiveButton("Ya", (dialog, which) -> {
            resepList.remove(position);
            adapter.notifyItemRemoved(position);
            saveResepToInternal();
            Toast.makeText(MainActivity.this, "Resep dihapus", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Tidak", (dialog, which) -> {
            adapter.notifyItemChanged(position);
            dialog.dismiss();
        });

        builder.show();
    }
}