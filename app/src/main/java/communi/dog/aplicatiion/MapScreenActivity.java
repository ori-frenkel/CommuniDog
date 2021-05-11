package communi.dog.aplicatiion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import communi.dog.aplicatiion.MapHandler.MapState;

import static communi.dog.aplicatiion.MapHandler.DEFAULT_MARKER_ICON_ID;

public class MapScreenActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapHandler mMapHandler;

    // user info
    private String userId;
    private User currentUser;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        final Intent activityIntent = getIntent();
        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mMapHandler = new MapHandler(findViewById(R.id.mapView), this);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        });

        mMapHandler.initMap();

        ImageView btCenterMap = findViewById(R.id.buttonCenterMap);
        btCenterMap.setOnClickListener(v -> mMapHandler.mapToCurrentLocation());

        ImageView btnMyProfile = findViewById(R.id.buttonMyProfileInMapActivity);
        btnMyProfile.setOnClickListener(v -> {
            Toast.makeText(this, "link to my profile screen", Toast.LENGTH_SHORT).show();
            Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
            myProfileIntent.putExtra("userId", currentUser.getId());
            myProfileIntent.putExtra("password", currentUser.getPassword());
            myProfileIntent.putExtra("email", currentUser.getEmail());
            myProfileIntent.putExtra("map_old_state", mMapHandler.currentState());
            startActivity(myProfileIntent);
        });

        ImageView btnMoreInfo = findViewById(R.id.buttonMoreInfoMapActivity);
        btnMoreInfo.setOnClickListener(v ->
                Toast.makeText(this, "link to more info screen", Toast.LENGTH_SHORT).show());

        if (activityIntent.hasExtra("map_old_state")) {
            mMapHandler.restoreState((MapState) activityIntent.getSerializableExtra("map_old_state"));
        } else if (savedInstanceState == null) {
            // new map
            if (!mMapHandler.mapToCurrentLocation()) {
                // todo: set initial coordinates using database?
                IGeoPoint initialLocation = new GeoPoint(32.1007, 34.8070);
                mMapHandler.centerMap(initialLocation, false);
            }
        }

        if (activityIntent.getBooleanExtra("add_marker", false)) {
            mMapHandler.addMarker(activityIntent.getStringExtra("marker_title"),
                    activityIntent.getDoubleExtra("marker_latitude", 0),
                    activityIntent.getDoubleExtra("marker_longitude", 0),
                    activityIntent.getIntExtra("marker_icon_res", DEFAULT_MARKER_ICON_ID));
        }

        // DB
        userId = activityIntent.getStringExtra("userId");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("Users");
        //todo: why not save only the id and pass in to MyProfile screen? why do we need here all the rest?
        currentUser = new User();

        readDataUsers(new MapScreenActivity.FirebaseCallback() {
            @Override
            public void onCallback(User user) {
            }
        });
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        System.out.println("MainActivity.onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putSerializable("map_old_state", mMapHandler.currentState());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        System.out.println("MainActivity.onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        Serializable oldState = savedInstanceState.getSerializable("map_old_state");
        if (!(oldState instanceof MapState)) {
            return; // ignore
        }
        mMapHandler.restoreState((MapState) oldState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // todo: exit the app or logout?
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("map_old_state", mMapHandler.currentState());
        startActivity(intent);
    }


    // DB
    private interface FirebaseCallback {
        void onCallback(User user);

    }

    private void readDataUsers(MapScreenActivity.FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        if (userId.equals(ds.child("id").getValue())) {
                            String email = (String) ds.child("email").getValue();
                            String password = (String) ds.child("password").getValue();
                            currentUser = new User(userId, email, password);
                        }
                    }
                }
                firebaseCallback.onCallback(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);
    }
}
