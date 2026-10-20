package id.jhodie.workorder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WorkOrderActivity extends AppCompatActivity {

    private int cableQty = 150;
    private int clampQty = 4;

    private TextView tvCableQty;
    private TextView tvClampQty;
    private EditText etOpmValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order);

        tvCableQty = findViewById(R.id.tvCableQty);
        tvClampQty = findViewById(R.id.tvClampQty);
        Button btnCableMinus = findViewById(R.id.btnCableMinus);
        Button btnCablePlus = findViewById(R.id.btnCablePlus);
        Button btnClampMinus = findViewById(R.id.btnClampMinus);
        Button btnClampPlus = findViewById(R.id.btnClampPlus);
        etOpmValue = findViewById(R.id.etOpmValue);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnCableMinus.setOnClickListener(v -> {
            if (cableQty > 0) {
                cableQty -= 10;
                tvCableQty.setText(getString(R.string.cable_qty_format, cableQty));
            }
        });

        btnCablePlus.setOnClickListener(v -> {
            cableQty += 10;
            tvCableQty.setText(getString(R.string.cable_qty_format, cableQty));
        });

        btnClampMinus.setOnClickListener(v -> {
            if (clampQty > 0) {
                clampQty--;
                tvClampQty.setText(getString(R.string.clamp_qty_format, clampQty));
            }
        });

        btnClampPlus.setOnClickListener(v -> {
            clampQty++;
            tvClampQty.setText(getString(R.string.clamp_qty_format, clampQty));
        });

        btnSubmit.setOnClickListener(v -> {
            String opmVal = etOpmValue.getText().toString().trim();
            if (opmVal.isEmpty()) {
                Toast.makeText(this, R.string.toast_fill_opm, Toast.LENGTH_SHORT).show();
                return;
            }
            String summary = getString(R.string.toast_summary_format, cableQty, clampQty, opmVal);
            Toast.makeText(this, summary, Toast.LENGTH_LONG).show();
        });
    }
}
