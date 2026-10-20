package id.jhodie.latihan161;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextAge, editTextHobby;
    private TextView textViewResult;

    private SharedPreferences preferences;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_HOBBY = "hobby";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextHobby = findViewById(R.id.editTextHobby);
        textViewResult = findViewById(R.id.textViewResult);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonClear = findViewById(R.id.buttonClear);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadPreferences();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String age = editTextAge.getText().toString().trim();
                String hobby = editTextHobby.getText().toString().trim();

                if (name.isEmpty() || age.isEmpty() || hobby.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_NAME, name);
                editor.putString(KEY_AGE, age);
                editor.putString(KEY_HOBBY, hobby);
                editor.apply();

                updateResultText(name, age, hobby);
                Toast.makeText(MainActivity.this,
                        "Preferensi berhasil disimpan!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                editTextName.setText("");
                editTextAge.setText("");
                editTextHobby.setText("");
                textViewResult.setText("Data preferensi belum disimpan");

                Toast.makeText(MainActivity.this,
                        "Preferensi dihapus!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPreferences() {
        String name = preferences.getString(KEY_NAME, null);
        String age = preferences.getString(KEY_AGE, null);
        String hobby = preferences.getString(KEY_HOBBY, null);

        if (name != null && age != null && hobby != null) {
            editTextName.setText(name);
            editTextAge.setText(age);
            editTextHobby.setText(hobby);
            updateResultText(name, age, hobby);
        } else {
            textViewResult.setText("Data preferensi belum disimpan");
        }
    }

    private void updateResultText(String name, String age, String hobby) {
        String result = "Nama: " + name + "\nUsia: " + age + "\nHobi: " + hobby;
        textViewResult.setText(result);
    }
}