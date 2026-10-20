package id.jhodie.latihan19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements BukuAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "buku.json";
    private RecyclerView recyclerView;
    private BukuAdapter adapter;
    private List<Buku> bukuList;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        bukuList = new ArrayList<>();

        adapter = new BukuAdapter(this, bukuList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupSwipeToDelete();
        copyJsonFromAssetsIfNeeded();
        loadBooksFromInternal();

        fabAdd.setOnClickListener(v -> showAddEditDialog(-1));
    }

    private void copyJsonFromAssetsIfNeeded() {
        File file = new File(getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream is = assetManager.open(FILE_NAME);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();

                FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fos.write(buffer);
                fos.close();
                Log.d(TAG, "Seed JSON sukses disalin ke internal storage.");
            } catch (Exception e) {
                Log.e(TAG, "Gagal menyalin seed JSON: " + e.getMessage());
            }
        }
    }

    private void loadBooksFromInternal() {
        bukuList.clear();
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            String jsonStr = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                bukuList.add(Buku.fromJsonObject(obj));
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Gagal load JSON internal: " + e.getMessage());
            Toast.makeText(this, "Gagal memuat data buku", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBooksToInternal() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Buku buku : bukuList) {
                jsonArray.put(buku.toJsonObject());
            }

            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes("UTF-8"));
            fos.close();
            Log.d(TAG, "Data buku disimpan, total: " + bukuList.size());
        } catch (Exception e) {
            Log.e(TAG, "Gagal simpan JSON internal: " + e.getMessage());
            Toast.makeText(this, "Gagal menyimpan data buku", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                bukuList.remove(pos);
                adapter.notifyItemRemoved(pos);
                saveBooksToInternal();
                Toast.makeText(MainActivity.this, "Buku dihapus", Toast.LENGTH_SHORT).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showAddEditDialog(int position) {
        boolean isEdit = (position >= 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Edit Buku" : "Tambah Buku");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_buku, null);
        TextInputEditText etJudul = view.findViewById(R.id.etJudul);
        TextInputEditText etPenulis = view.findViewById(R.id.etPenulis);
        TextInputEditText etTahun = view.findViewById(R.id.etTahun);
        TextInputEditText etDeskripsi = view.findViewById(R.id.etDeskripsi);

        if (isEdit) {
            Buku buku = bukuList.get(position);
            etJudul.setText(buku.getJudul());
            etPenulis.setText(buku.getPenulis());
            etTahun.setText(String.valueOf(buku.getTahun()));
            etDeskripsi.setText(buku.getDeskripsi());
        }

        builder.setView(view);
        builder.setPositiveButton("Simpan", null);
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String judul = etJudul.getText() != null ? etJudul.getText().toString().trim() : "";
            String penulis = etPenulis.getText() != null ? etPenulis.getText().toString().trim() : "";
            String tahunStr = etTahun.getText() != null ? etTahun.getText().toString().trim() : "";
            String deskripsi = etDeskripsi.getText() != null ? etDeskripsi.getText().toString().trim() : "";

            if (TextUtils.isEmpty(judul)) { etJudul.setError("Judul wajib diisi"); return; }
            if (TextUtils.isEmpty(penulis)) { etPenulis.setError("Penulis wajib diisi"); return; }
            if (TextUtils.isEmpty(tahunStr)) { etTahun.setError("Tahun wajib diisi"); return; }
            if (TextUtils.isEmpty(deskripsi)) { etDeskripsi.setError("Deskripsi wajib diisi"); return; }

            int tahun;
            try {
                tahun = Integer.parseInt(tahunStr);
            } catch (NumberFormatException ex) {
                etTahun.setError("Tahun harus berupa angka");
                return;
            }

            if (isEdit) {
                Buku buku = bukuList.get(position);
                buku.setJudul(judul);
                buku.setPenulis(penulis);
                buku.setTahun(tahun);
                buku.setDeskripsi(deskripsi);
                adapter.notifyItemChanged(position);
            } else {
                Buku newBuku = new Buku(judul, penulis, tahun, deskripsi);
                bukuList.add(newBuku);
                adapter.notifyItemInserted(bukuList.size() - 1);
            }

            saveBooksToInternal();
            dialog.dismiss();
        });
    }

    @Override
    public void onItemClick(int position) {
        showAddEditDialog(position);
    }

    @Override
    public void onItemLongClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Buku")
                .setMessage("Apakah Anda yakin ingin menghapus buku ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    bukuList.remove(position);
                    adapter.notifyItemRemoved(position);
                    saveBooksToInternal();
                    Toast.makeText(MainActivity.this, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}