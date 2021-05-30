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
        final MarkerDescriptor markerToEdit = mapState.getMarker(incomingIntent.getStringExtra("marker_id_to_edit"));

        userId = incomingIntent.getStringExtra("userId");

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
                        newText, latitude, longitude, isDogsitter, isFood, isMedication, userId);
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
        buttonCancel.setOnClickListener(view -> {
            backToMap();
        });
    }

    private String getMarkerTitle(boolean isDogsitter, boolean isFood, boolean isMedication) {
        String msg = "User " + userId + " offers:\n";
        User user = CommuniDogApp.getInstance().getDb().getUser();
        if (isDogsitter) msg += "Dogsitter services\n";
        if (isFood) msg += "Extra food\n";
        if (isMedication) msg += "Extra medication\n";
        // todo: add user contacts to the marker's message
//        String contacts = "";
//        if (!user.getEmail().isEmpty()) contacts += "Email - " + user.getEmail() + "\n";
//        if (!user.getPhoneNumber().isEmpty()) contacts += "Phone - " + user.getPhoneNumber() + "\n";
//        if (!contacts.isEmpty()) msg += "In order to contact him:\n" + contacts;
        return msg;
    }

    private void backToMap() {
        Intent backToMapIntent = new Intent(this, MapScreenActivity.class);
        backToMapIntent.putExtra("userId", userId);
        startActivity(backToMapIntent);
    }
}
