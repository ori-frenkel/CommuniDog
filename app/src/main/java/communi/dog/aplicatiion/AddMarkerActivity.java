package communi.dog.aplicatiion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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


        final EditText markerText = findViewById(R.id.editTextMarkerDescription);
        markerText.setText("");
        ImageView buttonSaveMarker = findViewById(R.id.buttonSaveMarker);
        buttonSaveMarker.setOnClickListener(view -> {
            Intent newIntent = new Intent(this, MapScreenActivity.class);

            newIntent.putExtra("add_marker", true);
            newIntent.putExtra("marker_latitude", latitude);
            newIntent.putExtra("marker_longitude", longitude);
            newIntent.putExtra("marker_title", markerText.getText().toString());
            newIntent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
            newIntent.putExtra("marker_logo_res", getMarkerLogoBySelectedRadio());
            startActivity(newIntent);
        });

        findViewById(R.id.addMarkerActivityConstraint).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            markerText.requestFocus();
            imm.hideSoftInputFromWindow(markerText.getWindowToken(), 0);
            markerText.clearFocus();
        });

        ImageView buttonCancel = findViewById(R.id.buttonCancelMarker);
        buttonCancel.setOnClickListener(view -> {
            Intent newIntent = new Intent(this, MapScreenActivity.class);
            newIntent.putExtra("add_marker", false);
            newIntent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
            startActivity(newIntent);
        });
    }


    private int getMarkerLogoBySelectedRadio() {
        final RadioGroup typeRadioGroup = findViewById(R.id.radioMarkerType);
        switch (typeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioBtnMarkerTypeDogisiter: {
                return R.drawable.man_carrying_dog_with_belt;
            }
            case R.id.radioBtnMarkerTypeFood: {
                return R.drawable.dog_smelling_a_bone;
            }
            case R.id.radioBtnMarkerTypeMedicine: {
                return R.drawable.dog_with_first_aid_kit_bag;
            }
            default:
                return 0;
        }

    }

}
