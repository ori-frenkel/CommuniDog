package communi.dog.aplicatiion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;

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
            Intent saveAdditionIntent = new Intent(this, MapScreenActivity.class);
            saveAdditionIntent.putExtra("add_marker", true);
            saveAdditionIntent.putExtra("marker_latitude", latitude);
            saveAdditionIntent.putExtra("marker_longitude", longitude);
            saveAdditionIntent.putExtra("marker_title", getMarkerTitle());
            saveAdditionIntent.putExtra("marker_icon_res", getMarkerLogoBySelectedRadio());
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


    private int getMarkerLogoBySelectedRadio() {
        final RadioGroup typeRadioGroup = findViewById(R.id.radioMarkerType);
        switch (typeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioBtnMarkerTypeDogsitter: {
                return R.drawable.dogsitter_marker_icon;
            }
            case R.id.radioBtnMarkerTypeFood: {
                return R.drawable.share_food_marker_icon;
            }
            case R.id.radioBtnMarkerTypeMedicine: {
                return R.drawable.share_madicine_marker_icon;
            }
            default:
                // the default icon value
                return 0;
        }
    }

    private String getMarkerTitle() {
        String userId = getIntent().getStringExtra("userId");
        final RadioGroup typeRadioGroup = findViewById(R.id.radioMarkerType);
        switch (typeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioBtnMarkerTypeDogsitter: {
                return "User " + userId + " is available as a dogsitter";
            }
            case R.id.radioBtnMarkerTypeFood: {
                return "User " + userId + " has extra food to share";
            }
            case R.id.radioBtnMarkerTypeMedicine: {
                return "User " + userId + " has extra medicines to share";
            }
            default:
                return "";
        }
    }

}
