package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddMarkerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        final Intent activityIntent = getIntent();
        final double latitude = activityIntent.getDoubleExtra("marker_latitude", 0);
        final double longitude = activityIntent.getDoubleExtra("marker_longitude", 0);


        ImageView buttonSaveMarker = findViewById(R.id.buttonSaveMarker);
        buttonSaveMarker.setOnClickListener(view -> {
            boolean isDogsitter = ((CheckBox) findViewById(R.id.checkboxDogsitter)).isChecked();
            boolean isFood = ((CheckBox) findViewById(R.id.checkboxFood)).isChecked();
            boolean isMedicine = ((CheckBox) findViewById(R.id.checkboxMedicine)).isChecked();

            if (!(isDogsitter || isFood || isMedicine)) {
                Toast.makeText(this, "check at least one option", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent saveAdditionIntent = new Intent(this, MapScreenActivity.class);
            saveAdditionIntent.putExtra("add_marker", true);
            saveAdditionIntent.putExtra("marker_latitude", latitude);
            saveAdditionIntent.putExtra("marker_longitude", longitude);
            saveAdditionIntent.putExtra("marker_text", getMarkerTitle(isDogsitter, isFood, isMedicine));
            saveAdditionIntent.putExtra("marker_is_dogsitter", isDogsitter);
            saveAdditionIntent.putExtra("marker_is_food", isFood);
            saveAdditionIntent.putExtra("marker_is_medicine", isMedicine);
            saveAdditionIntent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
            saveAdditionIntent.putExtra("userId", activityIntent.getStringExtra("userId"));
            startActivity(saveAdditionIntent);
        });

        ImageView buttonCancel = findViewById(R.id.buttonCancelMarker);
        buttonCancel.setOnClickListener(view -> {
            Intent cancelAdditionIntent = new Intent(this, MapScreenActivity.class);
            cancelAdditionIntent.putExtra("add_marker", false);
            cancelAdditionIntent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
            cancelAdditionIntent.putExtra("userId", activityIntent.getStringExtra("userId"));
            startActivity(cancelAdditionIntent);
        });
    }

    private String getMarkerTitle(boolean isDogsitter, boolean isFood, boolean isMedicine) {
        String userId = getIntent().getStringExtra("userId");
        String msg = "User " + userId + " offers:\n";
        if (isDogsitter) msg += "Dogsitter services\n";
        if (isFood) msg += "Extra food\n";
        if (isMedicine) msg += "Extra medicine\n";
        // todo: add user contacts to the marker's message
        msg += "In order to contact him ........";
        return msg;
    }

}
