package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (getIntent().getBooleanExtra("LOGOUT", false)) {
            finish();
            return;
        }
        // todo: check if already logged in and open the relevant screen (login or map)
        startActivity(new Intent(this, LoginActivity.class));
    }
}

