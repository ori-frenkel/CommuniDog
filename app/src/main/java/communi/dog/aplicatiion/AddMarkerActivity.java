package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddMarkerActivity extends AppCompatActivity {
    private String userId;
    private Intent incomingIntent = null;
    private MapState mapState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        incomingIntent = getIntent();
        ImageView buttonSaveMarker = findViewById(R.id.buttonSaveMarker);
        ImageView buttonDeleteMarker = findViewById(R.id.buttonDeleteMarker);

        mapState = CommuniDogApp.getInstance().getMapState();


        boolean isEdit = incomingIntent.hasExtra("old_marker_description");
        userId = incomingIntent.getStringExtra("userId");
        final double latitude = incomingIntent.getDoubleExtra("marker_latitude", 0);
        final double longitude = incomingIntent.getDoubleExtra("marker_longitude", 0);

        if (isEdit) {
            buttonDeleteMarker.setVisibility(View.VISIBLE);
            buttonDeleteMarker.setClickable(true);
            final MarkerDescriptor oldMarker = (MarkerDescriptor) incomingIntent.getSerializableExtra("old_marker_description");
            ((TextView) findViewById(R.id.textViewAddMarkerPageTitle)).setText(getText(R.string.edit_marker_page_title));
            ((CheckBox) findViewById(R.id.checkboxDogsitter)).setChecked(oldMarker.isDogsitter());
            ((CheckBox) findViewById(R.id.checkboxFood)).setChecked(oldMarker.isFood());
            ((CheckBox) findViewById(R.id.checkboxMedication)).setChecked(oldMarker.isMedication());
        } else {
            buttonDeleteMarker.setVisibility(View.GONE);
            buttonDeleteMarker.setClickable(false);
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
            MarkerDescriptor newMarker = new MarkerDescriptor(
                    getMarkerTitle(isDogsitter, isFood, isMedication), latitude, longitude,
                    isDogsitter, isFood, isMedication, userId);

            mapState.addMarker(newMarker);
            backToMap();
        });

        buttonDeleteMarker.setOnClickListener(v -> {
            if (!isEdit) return;
            final MarkerDescriptor oldMarker = (MarkerDescriptor) incomingIntent.getSerializableExtra("old_marker_description");
            mapState.removeMarker(oldMarker);
            backToMap();
        });


        ImageView buttonCancel = findViewById(R.id.buttonCancelMarker);
        buttonCancel.setOnClickListener(view -> {
            backToMap();
        });
    }

    private String getMarkerTitle(boolean isDogsitter, boolean isFood, boolean isMedication) {
        String msg = "User " + userId + " offers:\n";
        if (isDogsitter) msg += "Dogsitter services\n";
        if (isFood) msg += "Extra food\n";
        if (isMedication) msg += "Extra medication\n";
        // todo: add user contacts to the marker's message
        msg += "In order to contact him ........";
        return msg;
    }

    private void backToMap() {
        Intent backToMapIntent = new Intent(this, MapScreenActivity.class);
        backToMapIntent.putExtra("userId", userId);
        startActivity(backToMapIntent);
    }
}
