package id.jhodie.timetoplan;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private EditText edtSchedule;
    private Button btnTime, btnSaveSchedule;
    private ListView listSchedule;
    private Toolbar topBar;

    private ArrayList<String> schedules;
    private ScheduleAdapter adapter;

    private SharedPreferences preferences;
    private String selectedTime = "08:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        edtSchedule = findViewById(R.id.edtSchedule);
        btnTime = findViewById(R.id.btnTime);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);
        listSchedule = findViewById(R.id.listSchedule);
        topBar = findViewById(R.id.topBarSchedule);

        topBar.setNavigationOnClickListener(v -> finish());

        preferences = getSharedPreferences("schedule_data", MODE_PRIVATE);
        schedules = new ArrayList<>();
        loadData();

        adapter = new ScheduleAdapter(this, schedules);
        listSchedule.setAdapter(adapter);

        btnTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(
                    this,
                    (view, hour, minute) -> {
                        selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                        btnTime.setText(selectedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show();
        });

        btnSaveSchedule.setOnClickListener(v -> {
            String activity = edtSchedule.getText().toString().trim();
            if (activity.isEmpty()) {
                Toast.makeText(this, "Please enter an activity", Toast.LENGTH_SHORT).show();
                return;
            }

            schedules.add(selectedTime + "  •  " + activity);
            Collections.sort(schedules); // Sort by time
            adapter.notifyDataSetChanged();
            saveData();
            edtSchedule.setText("");
            Toast.makeText(this, "Schedule added", Toast.LENGTH_SHORT).show();
        });

        listSchedule.setOnItemLongClickListener((parent, view, position, id) -> {
            schedules.remove(position);
            adapter.notifyDataSetChanged();
            saveData();
            Toast.makeText(this, "Schedule deleted", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        StringBuilder builder = new StringBuilder();
        for (String item : schedules) {
            builder.append(item).append(";;");
        }
        editor.putString("schedule", builder.toString());
        editor.apply();
    }

    private void loadData() {
        String data = preferences.getString("schedule", "");
        if (!data.isEmpty()) {
            String[] items = data.split(";;");
            for (String item : items) {
                if (!item.isEmpty()) {
                    schedules.add(item);
                }
            }
            Collections.sort(schedules);
        }
    }

    private static class ScheduleAdapter extends ArrayAdapter<String> {
        public ScheduleAdapter(Context context, ArrayList<String> schedules) {
            super(context, 0, schedules);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_schedule, parent, false);
            }

            String item = getItem(position);
            TextView txtTime = convertView.findViewById(R.id.txtItemTime);
            TextView txtActivity = convertView.findViewById(R.id.txtItemActivity);

            if (item != null && item.contains("  •  ")) {
                String[] parts = item.split("  •  ");
                txtTime.setText(parts[0]);
                txtActivity.setText(parts[1]);
            }

            return convertView;
        }
    }
}
