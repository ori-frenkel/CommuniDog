package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddMarkerActivity extends AppCompatActivity {
    ImageView buttonSaveMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        final Intent intent = getIntent();
        buttonSaveMarker = findViewById(R.id.buttonSaveMarker);
        boolean isEdit = intent.hasExtra("old_marker_description");
        final double latitude = intent.getDoubleExtra("marker_latitude", 0);
        final double longitude = intent.getDoubleExtra("marker_longitude", 0);

        if (isEdit) {
            final MarkerDescriptor oldDesc = (MarkerDescriptor) intent.getSerializableExtra("old_marker_description");

            ((TextView) findViewById(R.id.textViewAddMarkerPageTitle)).setText(getText(R.string.edit_marker_page_title));
            ((CheckBox) findViewById(R.id.checkboxDogsitter)).setChecked(oldDesc.isDogsitter());
            ((CheckBox) findViewById(R.id.checkboxFood)).setChecked(oldDesc.isFood());
            ((CheckBox) findViewById(R.id.checkboxMedication)).setChecked(oldDesc.isMedication());
        } else {
            ((TextView) findViewById(R.id.textViewAddMarkerPageTitle)).setText(getText(R.string.add_marker_page_title));
        }

        buttonSaveMarker.setOnClickListener(view -> {
            boolean isDogsitter = ((CheckBox) findViewById(R.id.checkboxDogsitter)).isChecked();
            boolean isFood = ((CheckBox) findViewById(R.id.checkboxFood)).isChecked();
            boolean isMedication = ((CheckBox) findViewById(R.id.checkboxMedication)).isChecked();

            if (!(isDogsitter || isFood || isMedication)) {
                Toast.makeText(this, "check at least one option", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent saveAdditionIntent = new Intent(this, MapScreenActivity.class);
            if (isEdit) {
                saveAdditionIntent.putExtra("edit_marker", true);
            } else {
                saveAdditionIntent.putExtra("add_marker", true);
            }
            saveAdditionIntent.putExtra("marker_latitude", latitude);
            saveAdditionIntent.putExtra("marker_longitude", longitude);
            saveAdditionIntent.putExtra("marker_text", getMarkerTitle(isDogsitter, isFood, isMedication));
            saveAdditionIntent.putExtra("marker_is_dogsitter", isDogsitter);
            saveAdditionIntent.putExtra("marker_is_food", isFood);
            saveAdditionIntent.putExtra("marker_is_medication", isMedication);
            saveAdditionIntent.putExtra("map_old_state", intent.getSerializableExtra("map_old_state"));
            saveAdditionIntent.putExtra("userId", intent.getStringExtra("userId"));
            startActivity(saveAdditionIntent);
        });


        ImageView buttonCancel = findViewById(R.id.buttonCancelMarker);
        buttonCancel.setOnClickListener(view -> {
            Intent cancelAdditionIntent = new Intent(this, MapScreenActivity.class);
            cancelAdditionIntent.putExtra("add_marker", false);
            cancelAdditionIntent.putExtra("map_old_state", intent.getSerializableExtra("map_old_state"));
            cancelAdditionIntent.putExtra("userId", intent.getStringExtra("userId"));
            startActivity(cancelAdditionIntent);
        });
    }

    private String getMarkerTitle(boolean isDogsitter, boolean isFood, boolean isMedication) {
        String userId = getIntent().getStringExtra("userId");
        String msg = "User " + userId + " offers:\n";
        if (isDogsitter) msg += "Dogsitter services\n";
        if (isFood) msg += "Extra food\n";
        if (isMedication) msg += "Extra medication\n";
        // todo: add user contacts to the marker's message
        msg += "In order to contact him ........";
        return msg;
    }

}
