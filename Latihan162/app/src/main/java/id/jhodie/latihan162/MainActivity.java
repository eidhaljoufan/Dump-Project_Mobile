package id.jhodie.latihan162;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private EditText editTextNote;
    private TextView textViewNoteDisplay;
    private static final String TAG = "MainActivity";
    private static final String NOTES_FOLDER = "notes";
    private static final String NOTE_FILE = "personal_note.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNote = findViewById(R.id.editTextNote);
        textViewNoteDisplay =
                findViewById(R.id.textViewNoteDisplay);
        Button buttonSaveNote = findViewById(R.id.buttonSaveNote);
        Button buttonDeleteNote =
                findViewById(R.id.buttonDeleteNote);
        Button buttonUploadNote =
                findViewById(R.id.buttonUploadNote);

        // Verifikasi direktori Internal Storage
        File filesDir = getFilesDir();
        Log.d(TAG, "Direktori Internal Storage: " +
                filesDir.getAbsolutePath());

        // Membaca catatan
        String savedNote = readNoteFromFile();
        if (savedNote != null && !savedNote.isEmpty()) {
            textViewNoteDisplay.setText("Catatan: " + savedNote);
        } else {
            textViewNoteDisplay.setText("Catatan: Belum tersimpan");
        }

        buttonSaveNote.setOnClickListener(v -> {
            String note =
                    editTextNote.getText().toString().trim();
            if (note.isEmpty()) {
                Toast.makeText(this, "Catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (saveNoteToFile(note)) {
                textViewNoteDisplay.setText("Catatan: " + note);
                editTextNote.setText("");
                Toast.makeText(this, "Catatan berhasil disimpan!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal menyimpan catatan!",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonDeleteNote.setOnClickListener(v -> {
            File notesDir = new File(getFilesDir(), NOTES_FOLDER);
            File noteFile = new File(notesDir, NOTE_FILE);
            if (noteFile.exists()) {
                if (noteFile.delete()) {
                    textViewNoteDisplay.setText("Catatan: Belum tersimpan");
                            editTextNote.setText("");
                    Toast.makeText(this, "Catatan berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Catatan dihapus: " +
                            noteFile.getAbsolutePath());
                } else {
                    Toast.makeText(this, "Gagal menghapus catatan!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Gagal menghapus: " + noteFile.getAbsolutePath());
                }
            } else {
                Toast.makeText(this, "Tidak ada catatan untuk dihapus!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonUploadNote.setOnClickListener(v -> {
            File notesDir = new File(getFilesDir(), NOTES_FOLDER);
            File noteFile = new File(notesDir, NOTE_FILE);
            if (!noteFile.exists()) {
                Toast.makeText(this, "Tidak ada catatan untuk diunggah!", Toast.LENGTH_SHORT).show();
                return;
            }
            shareNoteFile(noteFile);
        });
    }

    private boolean saveNoteToFile(String note) {
        try {
            File notesDir = new File(getFilesDir(), NOTES_FOLDER);
            Log.d(TAG, "Mencoba membuat folder: " + notesDir.getAbsolutePath());
            if (!notesDir.exists()) {
                if (notesDir.mkdirs()) {
                    Log.d(TAG, "Folder berhasil dibuat: " + notesDir.getAbsolutePath());
                } else {
                    Log.e(TAG, "Gagal membuat folder: " + notesDir.getAbsolutePath());
                    return false;
                }
            }

            File noteFile = new File(notesDir, NOTE_FILE);
            FileOutputStream fos = new FileOutputStream(noteFile);
            fos.write(note.getBytes());
            fos.flush();
            fos.close();
            Log.d(TAG, "Catatan disimpan: " + noteFile.getAbsolutePath() + ", Ukuran: " + noteFile.length() + " bytes");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Gagal menyimpan: " + e.getMessage());
            return false;
        }
    }

    private String readNoteFromFile() {
        try {
            File notesDir = new File(getFilesDir(), NOTES_FOLDER);
            File noteFile = new File(notesDir, NOTE_FILE);
            if (!noteFile.exists()) {
                Log.d(TAG, "File tidak ditemukan: " + noteFile.getAbsolutePath());
                return null;
            }
            FileInputStream fis = new FileInputStream(noteFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            fis.close();
            String result = sb.toString().trim();
            Log.d(TAG, "Catatan dibaca: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Gagal membaca: " + e.getMessage());
            return null;
        }
    }

    private void shareNoteFile(File noteFile) {
        try {
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    "id.jhodie.latihan162.fileprovider", noteFile
            );
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Catatan Pribadi");

                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Bagikan Catatan"));
            Log.d(TAG, "Berbagi file: " + noteFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Gagal mengunggah: " + e.getMessage());
            Toast.makeText(this, "Gagal mengunggah: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}