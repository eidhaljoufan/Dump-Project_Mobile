package id.jhodie.timetoplan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HabitInsightActivity extends AppCompatActivity {

    private TextView txtSchedule;
    private TextView txtRecommendation;
    private TextView txtTodo;
    private TextView txtNotes;
    private TextView txtFocus;
    private TextView txtInsight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_insight);

        txtRecommendation = findViewById(R.id.txtRecommendation);
        txtSchedule = findViewById(R.id.txtSchedule);
        txtTodo = findViewById(R.id.txtTodo);
        txtNotes = findViewById(R.id.txtNotes);
        txtFocus = findViewById(R.id.txtFocus);
        txtInsight = findViewById(R.id.txtInsight);

        increaseUsage("insight");
        loadInsight();
    }

    private void increaseUsage(String key){

        SharedPreferences pref =
                getSharedPreferences("usage", MODE_PRIVATE);

        int value = pref.getInt(key,0);

        pref.edit()
                .putInt(key,value+1)
                .apply();

    }
    private void loadInsight(){

        SharedPreferences pref =
                getSharedPreferences("usage", MODE_PRIVATE);

        int schedule = pref.getInt("schedule",0);
        int todo = pref.getInt("todo",0);
        int notes = pref.getInt("notes",0);
        int focus = pref.getInt("focus",0);

        txtSchedule.setText("Schedule : " + schedule);
        txtTodo.setText("Todo : " + todo);
        txtNotes.setText("Notes : " + notes);
        txtFocus.setText("Focus : " + focus);

        String insight;

        if (schedule >= todo && schedule >= notes && schedule >= focus) {

            insight = "You mostly organize your activities using Schedule Planner.";

        } else if (todo >= schedule && todo >= notes && todo >= focus) {

            insight = "You rely on Todo List to stay productive.";

        } else if (notes >= schedule && notes >= todo && notes >= focus) {

            insight = "You frequently capture ideas using Notes.";

        } else {

            insight = "You spend most of your productive time using Focus Timer.";

        }

        txtInsight.setText(insight);

        String recommendation;

        int min = Math.min(
                Math.min(schedule, todo),
                Math.min(notes, focus)
        );

        if (min == schedule) {

            recommendation = "Try planning more activities using Schedule Planner.";

        } else if (min == todo) {

            recommendation = "Create more Todo items to improve task management.";

        } else if (min == notes) {

            recommendation = "Use Notes more often to capture your ideas.";

        } else {

            recommendation = "Increase your Focus Timer sessions for better concentration.";

        }

        txtRecommendation.setText(recommendation);

    }


}