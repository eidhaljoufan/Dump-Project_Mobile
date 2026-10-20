package id.jhodie.timetoplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout cardSchedule;
    private LinearLayout cardTodo;
    private LinearLayout cardNotes;
    private LinearLayout cardFocus;
    private MaterialCardView cardAccessibility;
    private MaterialCardView cardSummary;
    private android.widget.TextView txtSummary;

    private DrawerLayout drawerLayout;
    private Toolbar topBar;
    private NavigationView navigationView;

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        topBar = findViewById(R.id.topBar);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.bringToFront();
        navigationView.requestLayout();

        cardSchedule = findViewById(R.id.cardSchedule);
        cardTodo = findViewById(R.id.cardTodo);
        cardNotes = findViewById(R.id.cardNotes);
        cardFocus = findViewById(R.id.cardFocus);
        cardAccessibility = findViewById(R.id.cardAccessibility);
        cardSummary = findViewById(R.id.cardSummary);
        txtSummary = findViewById(R.id.txtSummary);

        textToSpeech = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {

                int result = textToSpeech.setLanguage(new Locale("id", "ID"));

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {

                    textToSpeech.setLanguage(Locale.US);

                }

            }

        });

        topBar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            if (id == R.id.nav_random) {
                startActivity(new Intent(this, RandomTaskActivity.class));
            }

            if (id == R.id.nav_insight) {
                startActivity(new Intent(this, HabitInsightActivity.class));
            }

            if (id == R.id.nav_theme) {
                startActivity(new Intent(this, SettingsActivity.class));
            }

            if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            }

            if (id == R.id.nav_exit) {
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        cardSummary.setOnClickListener(v -> {
            animateCard(v);
            startActivity(new Intent(this, HabitInsightActivity.class));
            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        });

        cardSchedule.setOnClickListener(v -> {

            animateCard(v);

            increaseUsage("schedule");

            startActivity(new Intent(this, ScheduleActivity.class));

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );

        });

        cardTodo.setOnClickListener(v -> {

            animateCard(v);

            increaseUsage("todo");

            startActivity(new Intent(this, TodoActivity.class));

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );

        });

        cardNotes.setOnClickListener(v -> {

            animateCard(v);

            increaseUsage("notes");

            startActivity(new Intent(this, NotesActivity.class));

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );

        });

        cardFocus.setOnClickListener(v -> {
            animateCard(v);
            increaseUsage("focus");
            startActivity(new Intent(this, FocusActivity.class));
            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );

        });

        cardAccessibility.setOnClickListener(v -> {

            animateCard(v);

            increaseUsage("voice");

            startActivity(new Intent(this, VoiceAssistantActivity.class));

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSummary();
    }

    private void loadSummary() {
        SharedPreferences pref = getSharedPreferences("usage", MODE_PRIVATE);

        int schedule = pref.getInt("schedule", 0);
        int todo = pref.getInt("todo", 0);
        int notes = pref.getInt("notes", 0);
        int focus = pref.getInt("focus", 0);

        int total = schedule + todo + notes + focus;

        if (total == 0) {
            txtSummary.setText("Start your productive journey today!");
            return;
        }

        String topFeature;
        if (schedule >= todo && schedule >= notes && schedule >= focus) {
            topFeature = "Planning Schedules";
        } else if (todo >= schedule && todo >= notes && todo >= focus) {
            topFeature = "Managing Tasks";
        } else if (notes >= schedule && notes >= todo && notes >= focus) {
            topFeature = "Capturing Notes";
        } else {
            topFeature = "Staying Focused";
        }

        String stats = String.format(Locale.getDefault(),
                "You have completed %d activities. Your main focus is %s.",
                total, topFeature);

        txtSummary.setText(stats);
    }

    private void applyTheme() {
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        boolean darkMode = pref.getBoolean("dark_mode", false);
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void increaseUsage(String key) {

        SharedPreferences pref =
                getSharedPreferences("usage", MODE_PRIVATE);

        int value = pref.getInt(key, 0);

        pref.edit()
                .putInt(key, value + 1)
                .apply();

    }

    private void animateCard(View view) {

        view.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(80)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80));

    }

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );

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