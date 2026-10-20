package id.jhodie.timetoplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar topBar;

    private Switch switchDark;

    private RadioButton rbSlow;
    private RadioButton rbNormal;
    private RadioButton rbFast;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        topBar = findViewById(R.id.topBarSettings);

        switchDark = findViewById(R.id.switchDark);

        rbSlow = findViewById(R.id.rbSlow);
        rbNormal = findViewById(R.id.rbNormal);
        rbFast = findViewById(R.id.rbFast);

        preferences = getSharedPreferences("settings", MODE_PRIVATE);

        loadSettings();

        topBar.setNavigationOnClickListener(v -> finish());

        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {

            preferences.edit()
                    .putBoolean("dark_mode", isChecked)
                    .apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

        });

        rbSlow.setOnClickListener(v -> saveVoiceSpeed("slow"));

        rbNormal.setOnClickListener(v -> saveVoiceSpeed("normal"));

        rbFast.setOnClickListener(v -> saveVoiceSpeed("fast"));

    }

    private void saveVoiceSpeed(String speed){

        preferences.edit()
                .putString("voice_speed", speed)
                .apply();

    }

    private void loadSettings(){

        boolean darkMode =
                preferences.getBoolean("dark_mode", false);

        switchDark.setChecked(darkMode);

        String speed =
                preferences.getString("voice_speed","normal");

        switch(speed){

            case "slow":
                rbSlow.setChecked(true);
                break;

            case "fast":
                rbFast.setChecked(true);
                break;

            default:
                rbNormal.setChecked(true);

        }

    }

}