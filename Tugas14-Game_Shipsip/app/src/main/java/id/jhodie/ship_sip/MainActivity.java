package id.jhodie.ship_sip;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ArcadeGameView arcadeGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arcadeGameView = new ArcadeGameView(this);
        setContentView(arcadeGameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        arcadeGameView.resumeGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        arcadeGameView.pauseGame();
    }
}