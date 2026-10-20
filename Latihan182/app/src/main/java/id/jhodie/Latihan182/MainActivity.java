package id.jhodie.Latihan182;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> notes = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private TextToSpeech tts;

    private EditText editTextNote;
    private ListView listViewNotes;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNote = findViewById(R.id.editTextNote);
        buttonSave = findViewById(R.id.buttonSave);
        listViewNotes = findViewById(R.id.listViewNotes);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                notes
        );

        listViewNotes.setAdapter(adapter);

        // Inisialisasi TTS
        tts = new TextToSpeech(this,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {

                        if(status == TextToSpeech.SUCCESS){
                            tts.setLanguage(Locale.getDefault());
                        }
                    }
                });

        // Memungkinkan memilih item
        listViewNotes.setChoiceMode(
                ListView.CHOICE_MODE_SINGLE);

        // Tombol Simpan
        buttonSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String note =
                                editTextNote.getText().toString();

                        if(!note.isEmpty()){

                            notes.add(note);

                            adapter.notifyDataSetChanged();

                            editTextNote.setText("");

                            speak("Catatan tersimpan");

                        }else{

                            Toast.makeText(
                                    MainActivity.this,
                                    "Masukkan catatan terlebih dahulu",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });

        // Hapus catatan
        listViewNotes.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        notes.remove(position);

                        adapter.notifyDataSetChanged();

                        speak("Catatan dihapus");

                        return true;
                    }
                });

        // Baca catatan saat dipilih
        listViewNotes.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        String note =
                                notes.get(position);

                        speak(note);
                    }
                });

        // Baca saat fokus ke EditText
        editTextNote.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(
                            View v,
                            boolean hasFocus) {

                        if(hasFocus &&
                                editTextNote.getText()
                                        .toString()
                                        .isEmpty()) {

                            speak(
                                    "Kolom catatan. Silakan ketik untuk menambahkan catatan baru."
                            );

                        } else if(hasFocus){

                            speak(
                                    "Sedang mengedit catatan. Teks saat ini "
                                            + editTextNote.getText().toString()
                            );
                        }
                    }
                });
    }

    private void speak(String text){

        if(tts != null){

            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts != null){

            tts.stop();
            tts.shutdown();
        }
    }
}