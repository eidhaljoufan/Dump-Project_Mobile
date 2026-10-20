package id.jhodie.timetoplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    private EditText edtNotes;
    private Button btnSaveNotes;
    private ListView listNotes;
    private Toolbar topBar;

    private ArrayList<String> notesList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        edtNotes = findViewById(R.id.edtNotes);
        btnSaveNotes = findViewById(R.id.btnSaveNotes);
        listNotes = findViewById(R.id.listNotes);
        topBar = findViewById(R.id.topBarNotes);

        topBar.setNavigationOnClickListener(v -> finish());

        preferences = getSharedPreferences("notes_data", MODE_PRIVATE);
        notesList = new ArrayList<>();
        loadNotes();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesList);
        listNotes.setAdapter(adapter);

        btnSaveNotes.setOnClickListener(v -> {
            String note = edtNotes.getText().toString().trim();
            if (note.isEmpty()) {
                Toast.makeText(this, "Masukkan catatan", Toast.LENGTH_SHORT).show();
                return;
            }

            notesList.add(0, note); // Add to top
            saveNotes();
            adapter.notifyDataSetChanged();
            edtNotes.setText("");
            Toast.makeText(this, "Catatan ditambahkan", Toast.LENGTH_SHORT).show();
        });

        listNotes.setOnItemLongClickListener((parent, view, position, id) -> {
            notesList.remove(position);
            saveNotes();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void saveNotes() {
        JSONArray array = new JSONArray();
        for (String note : notesList) {
            array.put(note);
        }
        preferences.edit().putString("notes_list", array.toString()).apply();
    }

    private void loadNotes() {
        String json = preferences.getString("notes_list", "");
        if (!json.isEmpty()) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    notesList.add(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
