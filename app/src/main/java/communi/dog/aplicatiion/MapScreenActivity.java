package communi.dog.aplicatiion;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;


public class MapScreenActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapHandler mMapHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        });

        mMapHandler = new MapHandler(findViewById(R.id.mapView), CommuniDogApp.getInstance().getMapState());

        mMapHandler.setLongPressCallback(p -> {
            Intent intent = new Intent(this, AddMarkerActivity.class);
            String userId = CommuniDogApp.getInstance().getDb().getUser().getId();
            intent.putExtra("new_latitude", p.getLatitude());
            intent.putExtra("new_longitude", p.getLongitude());
            if (CommuniDogApp.getInstance().getMapState().hasMarker(userId)) {
                Log.i(MapHandler.class.getSimpleName(), "edit existing marker");
                intent.putExtra("marker_id_to_edit", userId);
            } else {
                Log.i(MapHandler.class.getSimpleName(), "create new marker");
            }
            this.startActivity(intent);
        });

        ImageView btCenterMap = findViewById(R.id.buttonCenterMap);
        btCenterMap.setOnClickListener(v -> mMapHandler.mapToCurrentLocation());

        ImageView btnMyProfile = findViewById(R.id.buttonMyProfileInMapActivity);
        btnMyProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfilePage.class)));

        ImageView btnMoreInfo = findViewById(R.id.buttonMoreInfoMapActivity);
        btnMoreInfo.setOnClickListener(v ->
                Toast.makeText(this, "link to more info screen", Toast.LENGTH_SHORT).show());
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest =
                new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.putExtra("LOGOUT", true);
                    startActivity(intent1);
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Close the app?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
