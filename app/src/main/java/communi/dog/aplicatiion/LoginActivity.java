package communi.dog.aplicatiion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Intent activityIntent = getIntent();

        EditText idEditText = findViewById(R.id.input_id_login);

        TextView to_register_btn = findViewById(R.id.register_now);
        to_register_btn.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        ImageView btnGoToMap = findViewById(R.id.buttonTempGoToMap);
        btnGoToMap.setOnClickListener((v) -> {
            Intent intent = new Intent(this, MapScreenActivity.class);
            if (activityIntent.hasExtra("map_old_state")) {
                intent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
            }
            startActivity(intent);
        });

        findViewById(R.id.loginConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            idEditText.requestFocus();
            imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
            idEditText.clearFocus();
        });
    }
}