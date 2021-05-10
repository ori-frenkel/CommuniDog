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


        ImageView buttonSaveMarker = findViewById(R.id.buttonSaveMarker);
        buttonSaveMarker.setOnClickListener(view -> {
            Intent saveAdditionIntent = new Intent(this, MapScreenActivity.class);
            saveAdditionIntent.putExtra("add_marker", true);
            saveAdditionIntent.putExtra("marker_latitude", latitude);
            saveAdditionIntent.putExtra("marker_longitude", longitude);
            saveAdditionIntent.putExtra("marker_title", getMarkerTitle());
            saveAdditionIntent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
            saveAdditionIntent.putExtra("marker_logo_res", getMarkerLogoBySelectedRadio());
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

    private String getMarkerTitle() {
        String userId = getIntent().getStringExtra("userId");
        final RadioGroup typeRadioGroup = findViewById(R.id.radioMarkerType);
        switch (typeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioBtnMarkerTypeDogisiter: {
                return "User " + userId + " is free for dogisiter";
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
