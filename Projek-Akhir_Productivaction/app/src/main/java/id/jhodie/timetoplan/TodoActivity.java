package id.jhodie.timetoplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class TodoActivity extends AppCompatActivity {

    private EditText edtTask;
    private Button btnAdd, btnRandom, btnSpeak;
    private ListView listTodo;
    private int lastRandom = -1;
    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;

    private SharedPreferences preferences;

    private TextToSpeech textToSpeech;
    private final Random random = new Random();

    private final String[] randomTasks = {
            "Membaca buku 10-30 menit",
            "Jogging selama 20 menit",
            "Berjalan menggunakan transportasi umum",
            "Membersihkan meja belajar",
            "Minum air putih 2 gelas",
            "Belajar Mobile Programming",
            "Review materi kuliah",
            "Menulis jurnal harian",
            "Merapikan kamar",
            "Mendengarkan podcast edukasi",
            "Latihan public speaking",
            "Meditasi selama 10 menit",
            "Menyiram tanaman",
            "Membaca artikel teknologi",
            "Belajar bahasa Inggris",
            "Mengerjakan tugas kuliah",
            "Stretching selama 15 menit",
            "Membersihkan folder komputer",
            "Membuat daftar pengeluaran",
            "Menghubungi orang tua",
            "Belajar menggunakan Android Studio",
            "Menonton video pembelajaran",
            "Membaca berita terbaru",
            "Bersepeda santai",
            "Istirahat tanpa menggunakan gadget selama 30 menit"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        edtTask = findViewById(R.id.edtTask);
        btnAdd = findViewById(R.id.btnAdd);
        btnRandom = findViewById(R.id.btnRandom);
        btnSpeak = findViewById(R.id.btnSpeak);
        listTodo = findViewById(R.id.listTodo);

        preferences = getSharedPreferences("todo_data", MODE_PRIVATE);

        tasks = new ArrayList<>();

        loadData();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tasks
        );

        listTodo.setAdapter(adapter);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(new Locale("id", "ID"));
            }
        });

        btnRandom.setOnClickListener(v -> {
            int index;
            do {
                index = random.nextInt(randomTasks.length);
            } while (index == lastRandom);
            lastRandom = index;
            String task = randomTasks[index];
            tasks.add(task);
            adapter.notifyDataSetChanged();
            saveData();

            Toast.makeText(
                    this,
                    "Task dipilih: " + task,
                    Toast.LENGTH_SHORT
            ).show();

        });

        btnAdd.setOnClickListener(v -> {

            String task = edtTask.getText().toString().trim();

            if (task.isEmpty()) {
                Toast.makeText(this,
                        "Masukkan tugas terlebih dahulu",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            tasks.add(task);

            adapter.notifyDataSetChanged();

            saveData();

            edtTask.setText("");

        });

        listTodo.setOnItemClickListener((parent, view, position, id) -> {

            String item = tasks.get(position);

            if (!item.startsWith("✓ ")) {
                tasks.set(position, "✓ " + item);
            }

            adapter.notifyDataSetChanged();

            saveData();

        });

        listTodo.setOnItemLongClickListener((parent, view, position, id) -> {

            tasks.remove(position);

            adapter.notifyDataSetChanged();

            saveData();

            Toast.makeText(
                    this,
                    "Task dihapus",
                    Toast.LENGTH_SHORT
            ).show();

            return true;
        });

        btnSpeak.setOnClickListener(v -> speakTasks());

    }

    private void speakTasks() {

        if (tasks.isEmpty()) {

            textToSpeech.speak(
                    "Belum ada tugas.",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
            );

            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("Hari ini terdapat ");

        builder.append(tasks.size());

        builder.append(" tugas. ");

        for (String task : tasks) {
            builder.append(task.replace("✓", ""));
            builder.append(". ");
        }

        textToSpeech.speak(
                builder.toString(),
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
        );

    }

    private void saveData() {

        SharedPreferences.Editor editor = preferences.edit();

        StringBuilder builder = new StringBuilder();

        for (String task : tasks) {
            builder.append(task).append(";;");
        }

        editor.putString("tasks", builder.toString());

        editor.apply();

    }

    private void loadData() {

        String data = preferences.getString("tasks", "");

        if (!data.isEmpty()) {

            String[] result = data.split(";;");

            for (String item : result) {

                if (!item.isEmpty()) {
                    tasks.add(item);
                }

            }

        }

    }

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();

    }

}