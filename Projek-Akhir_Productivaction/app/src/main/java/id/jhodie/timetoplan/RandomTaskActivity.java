package id.jhodie.timetoplan;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

public class RandomTaskActivity extends AppCompatActivity {

    private static final String PREF_NAME = "RandomTask";
    private static final String KEY_ALL_TASKS = "allTasks";

    private androidx.appcompat.widget.Toolbar topBar;
    private android.widget.TextView txtRandomTask;
    private android.widget.Button btnGenerate;
    private android.widget.Button btnAddTask;
    private android.widget.EditText edtNewTask;
    private ListView listTasks;

    private ArrayList<String> taskList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Random random = new Random();
    private int lastRandom = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_task);

        topBar = findViewById(R.id.topBarRandom);
        txtRandomTask = findViewById(R.id.txtRandomTask);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnAddTask = findViewById(R.id.btnAddTask);
        edtNewTask = findViewById(R.id.edtNewTask);
        listTasks = findViewById(R.id.listTasks);

        loadTasks();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listTasks.setAdapter(adapter);

        topBar.setNavigationOnClickListener(v -> finish());

        btnGenerate.setOnClickListener(v -> generateTask());

        btnAddTask.setOnClickListener(v -> {
            String task = edtNewTask.getText().toString().trim();
            if (task.isEmpty()) {
                edtNewTask.setError("Masukkan task");
                return;
            }
            taskList.add(task);
            saveTasks();
            adapter.notifyDataSetChanged();
            edtNewTask.setText("");
            Toast.makeText(this, "Task ditambahkan", Toast.LENGTH_SHORT).show();
        });

        listTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            taskList.remove(position);
            saveTasks();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Task dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });

        generateTask();
    }

    private void generateTask() {
        if (taskList.isEmpty()) {
            txtRandomTask.setText("Task tidak tersedia");
            return;
        }

        int randomIndex;
        if (taskList.size() == 1) {
            randomIndex = 0;
        } else {
            do {
                randomIndex = random.nextInt(taskList.size());
            } while (randomIndex == lastRandom);
        }

        lastRandom = randomIndex;
        txtRandomTask.setText(taskList.get(randomIndex));
    }

    private void saveTasks() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (String task : taskList) {
            array.put(task);
        }
        preferences.edit().putString(KEY_ALL_TASKS, array.toString()).apply();
    }

    private void loadTasks() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String json = preferences.getString(KEY_ALL_TASKS, "");

        if (json.isEmpty()) {
            // First time run, load defaults
            loadDefaultTasks();
            saveTasks();
        } else {
            try {
                JSONArray array = new JSONArray(json);
                taskList.clear();
                for (int i = 0; i < array.length(); i++) {
                    taskList.add(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDefaultTasks() {
        taskList.add("Membaca buku 30 menit");
        taskList.add("Jogging 20 menit");
        taskList.add("Minum 2 gelas air");
        taskList.add("Meditasi 10 menit");
        taskList.add("Merapikan meja kerja");
        taskList.add("Membersihkan kamar");
        taskList.add("Belajar Java");
        taskList.add("Belajar Android");
        taskList.add("Belajar UI Design");
        taskList.add("Stretching 15 menit");
        taskList.add("Jalan kaki sore");
        taskList.add("Membaca artikel teknologi");
        taskList.add("Menonton video edukasi");
        taskList.add("Menulis jurnal");
        taskList.add("Review target minggu ini");
        taskList.add("Belajar Bahasa Inggris");
        taskList.add("Merapikan file laptop");
        taskList.add("Minum vitamin");
        taskList.add("Menghubungi keluarga");
        taskList.add("Latihan pernapasan");
        taskList.add("Belajar Git");
        taskList.add("Belajar Database");
        taskList.add("Mengerjakan latihan coding");
        taskList.add("Istirahat tanpa gadget");
        taskList.add("Membuat to-do list besok");
    }
}
