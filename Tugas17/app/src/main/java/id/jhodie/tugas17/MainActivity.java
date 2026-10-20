package id.jhodie.tugas17;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvStatus;
    private EditText etTaskInput;
    private ListView lvTasks;
    private ArrayList<String> taskList;
    private ArrayAdapter<String> adapter;
    private int selectedPosition = -1;

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi view
        tvStatus = findViewById(R.id.tvStatus);
        etTaskInput = findViewById(R.id.etTaskInput);
        lvTasks = findViewById(R.id.lvTasks);

        // Inisialisasi daftar tugas
        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, taskList);
        lvTasks.setAdapter(adapter);
        lvTasks.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Menangani klik item untuk memilih tugas yang akan dihapus
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                tvStatus.setText("Status: Tugas terpilih: " + taskList.get(position));
            }
        });

        // Memeriksa izin merekam audio
        checkRecordAudioPermission();
    }

    private void checkRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin merekam audio diberikan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Izin merekam audio diperlukan untuk input suara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method untuk memulai input suara
    public void startSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id-ID");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ucapkan perintah Anda");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            tvStatus.setText("Status: Mendengarkan...");
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String command = result.get(0);
                processVoiceCommand(command);
            }
        }
    }

    // Memproses perintah suara
    private void processVoiceCommand(String command) {
        tvStatus.setText("Status: Memproses perintah...");
        String commandLower = command.toLowerCase();

        if (commandLower.contains("tambah") || commandLower.contains("tambahkan")) {
            // Ekstrak tugas dari perintah
            String task = command;
            String[] keywords = {"tambah", "tambahkan", "tugas"};
            for (String keyword : keywords) {
                if (commandLower.contains(keyword)) {
                    int index = commandLower.indexOf(keyword) + keyword.length();
                    if (index < command.length()) {
                        task = command.substring(index).trim();
                        // Hapus karakter ":" atau "-" jika ada
                        task = task.replaceAll("^[:\\-\\s]+", "");
                        break;
                    }
                }
            }

            // Jika tidak ada keyword yang ditemukan atau task kosong, gunakan seluruh perintah
            if (task.isEmpty() || task.equals(command)) {
                task = command;
            }

            addTaskToList(task);
            Toast.makeText(this, "Tugas ditambahkan: " + task, Toast.LENGTH_SHORT).show();

        } else if (commandLower.contains("hapus") || commandLower.contains("delete") || commandLower.contains("remove")) {
            // Ekstrak tugas yang akan dihapus
            String taskToDelete = command;
            String[] keywords = {"hapus", "delete", "remove"};
            for (String keyword : keywords) {
                if (commandLower.contains(keyword)) {
                    int index = commandLower.indexOf(keyword) + keyword.length();
                    if (index < command.length()) {
                        taskToDelete = command.substring(index).trim();
                        taskToDelete = taskToDelete.replaceAll("^[:\\-\\s]+", "");
                        break;
                    }
                }
            }

            if (taskToDelete.isEmpty() || taskToDelete.equals(command)) {
                taskToDelete = command;
            }

            deleteTaskByName(taskToDelete);

        } else if (commandLower.contains("lihat") || commandLower.contains("tampilkan") || commandLower.contains("show")) {
            // Tampilkan daftar tugas
            showTaskList();
        } else {
            // Jika perintah tidak dikenali, tambahkan sebagai tugas
            addTaskToList(command);
            Toast.makeText(this, "Tugas ditambahkan: " + command, Toast.LENGTH_SHORT).show();
        }

        tvStatus.setText("Status: Siap");
    }

    // Method untuk menambah tugas
    public void addTask(View view) {
        String task = etTaskInput.getText().toString().trim();
        if (!task.isEmpty()) {
            addTaskToList(task);
            etTaskInput.setText("");
            tvStatus.setText("Status: Tugas ditambahkan");
        } else {
            Toast.makeText(this, "Silakan masukkan tugas terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTaskToList(String task) {
        if (!task.isEmpty()) {
            taskList.add(task);
            adapter.notifyDataSetChanged();
            tvStatus.setText("Status: Tugas ditambahkan: " + task);
        }
    }

    // Method untuk menghapus tugas yang dipilih
    public void deleteTask(View view) {
        if (selectedPosition != -1 && selectedPosition < taskList.size()) {
            String deletedTask = taskList.get(selectedPosition);
            taskList.remove(selectedPosition);
            adapter.notifyDataSetChanged();
            selectedPosition = -1;
            tvStatus.setText("Status: Tugas dihapus: " + deletedTask);
            Toast.makeText(this, "Tugas dihapus: " + deletedTask, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Silakan pilih tugas yang akan dihapus", Toast.LENGTH_SHORT).show();
        }
    }

    // Method untuk menghapus tugas berdasarkan nama
    private void deleteTaskByName(String taskName) {
        boolean found = false;
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).toLowerCase().contains(taskName.toLowerCase()) ||
                    taskName.toLowerCase().contains(taskList.get(i).toLowerCase())) {
                String deletedTask = taskList.get(i);
                taskList.remove(i);
                adapter.notifyDataSetChanged();
                found = true;
                tvStatus.setText("Status: Tugas dihapus: " + deletedTask);
                Toast.makeText(this, "Tugas dihapus: " + deletedTask, Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if (!found) {
            Toast.makeText(this, "Tugas tidak ditemukan: " + taskName, Toast.LENGTH_SHORT).show();
            tvStatus.setText("Status: Tugas tidak ditemukan");
        }
    }

    // Method untuk menampilkan daftar tugas
    private void showTaskList() {
        if (taskList.isEmpty()) {
            Toast.makeText(this, "Belum ada tugas", Toast.LENGTH_SHORT).show();
            tvStatus.setText("Status: Belum ada tugas");
        } else {
            StringBuilder sb = new StringBuilder("Daftar Tugas:\n");
            for (int i = 0; i < taskList.size(); i++) {
                sb.append((i + 1)).append(". ").append(taskList.get(i)).append("\n");
            }
            Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
            tvStatus.setText("Status: Menampilkan daftar tugas");
        }
    }
}