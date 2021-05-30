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
    private Intent incomingIntent = null;
    private MapState mapState;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        incomingIntent = getIntent();
        currentUser = CommuniDogApp.getInstance().getDb().getUser();
        ImageView buttonSaveMarker = findViewById(R.id.buttonSaveMarker);
        ImageView buttonDeleteMarker = findViewById(R.id.buttonDeleteMarker);

        mapState = CommuniDogApp.getInstance().getMapState();
        final MarkerDescriptor markerToEdit = mapState.getMarker(incomingIntent.getStringExtra("marker_id_to_edit"));

        if (markerToEdit != null) {
            buttonDeleteMarker.setVisibility(View.VISIBLE);
            buttonDeleteMarker.setClickable(true);
            buttonSaveMarker.setImageResource(R.drawable.ic_save_marker);
            ((TextView) findViewById(R.id.textViewAddMarkerPageTitle)).setText(getText(R.string.edit_marker_page_title));
            ((CheckBox) findViewById(R.id.checkboxDogsitter)).setChecked(markerToEdit.isDogsitter());
            ((CheckBox) findViewById(R.id.checkboxFood)).setChecked(markerToEdit.isFood());
            ((CheckBox) findViewById(R.id.checkboxMedication)).setChecked(markerToEdit.isMedication());
        } else {
            buttonDeleteMarker.setVisibility(View.GONE);
            buttonDeleteMarker.setClickable(false);
            buttonSaveMarker.setImageResource(R.drawable.ic_add_marker);
            ((TextView) findViewById(R.id.textViewAddMarkerPageTitle)).setText(getText(R.string.add_marker_page_title));
        }

        buttonSaveMarker.setOnClickListener(view -> {
            boolean isDogsitter = ((CheckBox) findViewById(R.id.checkboxDogsitter)).isChecked();
            boolean isFood = ((CheckBox) findViewById(R.id.checkboxFood)).isChecked();
            boolean isMedication = ((CheckBox) findViewById(R.id.checkboxMedication)).isChecked();
            if (!(isDogsitter || isFood || isMedication)) {
                Toast.makeText(this, "check at least one service", Toast.LENGTH_SHORT).show();
                return;
            }

            final double latitude = incomingIntent.getDoubleExtra("new_latitude", -1);
            final double longitude = incomingIntent.getDoubleExtra("new_longitude", -1);
            String newText = getMarkerTitle(isDogsitter, isFood, isMedication);

            if (markerToEdit != null) {
                // edit existing marker
                if (latitude * longitude >= 0) {
                    markerToEdit.setNewLocation(latitude, longitude);
                }
                markerToEdit.setServices(isDogsitter, isFood, isMedication);
                markerToEdit.setText(newText);

            } else {
                // add new marker
                MarkerDescriptor newMarker = new MarkerDescriptor(
                        newText, latitude, longitude, isDogsitter, isFood, isMedication, currentUser.getId());
                mapState.addMarker(newMarker);
            }
            backToMap();
        });

        buttonDeleteMarker.setOnClickListener(v -> {
            if (markerToEdit == null) return;
            mapState.removeMarker(markerToEdit.getId());
            backToMap();
        });

        ImageView buttonCancel = findViewById(R.id.buttonCancelMarker);
        buttonCancel.setOnClickListener(view -> backToMap());
    }

    private String getMarkerTitle(boolean isDogsitter, boolean isFood, boolean isMedication) {
        String msg = "User " + currentUser.getUserName() + " offers:\n";
        if (isDogsitter) msg += "Dogsitter services\n";
        if (isFood) msg += "Extra food\n";
        if (isMedication) msg += "Extra medication\n";
        String contacts = "";
        if (!currentUser.getEmail().isEmpty())
            contacts += "Email - " + currentUser.getEmail() + "\n";
        if (!currentUser.getPhoneNumber().isEmpty())
            contacts += "Phone - " + currentUser.getPhoneNumber() + "\n";
        if (!contacts.isEmpty()) msg += "In order to contact him:\n" + contacts;
        return msg;
    }

    private void backToMap() {
        Intent backToMapIntent = new Intent(this, MapScreenActivity.class);
        startActivity(backToMapIntent);
    }
}
