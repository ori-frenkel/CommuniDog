package communi.dog.aplicatiion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class MapScreenActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final double MAP_ZOOM = 18.0;
    private final double MAP_MAX_ZOOM = 20.0;
    private final double MAP_MIN_ZOOM = 9.0;
    private MapView mMapView = null;
    private final ArrayList<MapState.MarkerDescriptor> mapMarkers = new ArrayList<>();
    private Location currentLocation = null;

    // user info
    private String userId;
    private User currentUser;
    private DatabaseReference usersRef;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            currentLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        System.out.println("MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        final Intent activityIntent = getIntent();
        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        });

        initializeMap();

        ImageView btCenterMap = findViewById(R.id.buttonCenterMap);
        btCenterMap.setOnClickListener(v -> mapToCurrentLocation());

        ImageView btnMyProfile = findViewById(R.id.buttonMyProfileInMapActivity);
        btnMyProfile.setOnClickListener(v -> {
            Toast.makeText(this, "link to my profile screen", Toast.LENGTH_SHORT).show();
            Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
            myProfileIntent.putExtra("id", currentUser.getId());
            myProfileIntent.putExtra("password", currentUser.getPassword());
            myProfileIntent.putExtra("email", currentUser.getEmail());
            myProfileIntent.putExtra("map_old_state", currMapState());
            //todo: add map old state
            startActivity(myProfileIntent);
        });

        ImageView btnMoreInfo = findViewById(R.id.buttonMoreInfoMapActivity);
        btnMoreInfo.setOnClickListener(v ->
                Toast.makeText(this, "link to more info screen", Toast.LENGTH_SHORT).show());

        if (activityIntent.hasExtra("map_old_state")) {
            restoreMapState((MapState) activityIntent.getSerializableExtra("map_old_state"));
        } else if (savedInstanceState == null) {
            // new map
            if (!mapToCurrentLocation()) {
                // todo: set initial coordinates using database?
                IGeoPoint initialLocation = new GeoPoint(32.1007, 34.8070);
                centerMap(initialLocation, false);
            }
        }

        if (activityIntent.getBooleanExtra("add_marker", false)) {
            addMarker(activityIntent.getStringExtra("marker_title"),
                    activityIntent.getDoubleExtra("marker_latitude", 0),
                    activityIntent.getDoubleExtra("marker_longitude", 0),
                    activityIntent.getIntExtra("marker_logo_res", 0));
        }

        // DB
        userId = activityIntent.getStringExtra("userId");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("Users");
        currentUser = new User();

        readDataUsers(new MapScreenActivity.FirebaseCallback() {
            @Override
            public void onCallback(User user) {
            }
        });
    }

    private void initializeMap() {
        // initialize the map
        mMapView = findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mMapView.setMultiTouchControls(true);
        mMapView.getController().setZoom(MAP_ZOOM);
        mMapView.setMaxZoomLevel(MAP_MAX_ZOOM);
        mMapView.setMinZoomLevel(MAP_MIN_ZOOM);

        // enable user location
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener);

        final MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Intent intent = new Intent(MapScreenActivity.this, AddMarkerActivity.class);
                intent.putExtra("marker_latitude", p.getLatitude());
                intent.putExtra("marker_longitude", p.getLongitude());
                intent.putExtra("map_old_state", currMapState());
                intent.putExtra("userId", getIntent().getStringExtra("userId"));

                startActivity(intent);
                return false;
            }
        };
        mMapView.getOverlays().add(new MapEventsOverlay(mReceive));

        addMyLocationIconOnMap();
        addScaleBarOnMap();
    }

    private void addMyLocationIconOnMap() {
        // set my location on the map
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);

        // add to map
        mMapView.getOverlays().add(mLocationOverlay);
    }

    private void addScaleBarOnMap() {
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        // set scale bar
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        // add to map
        mMapView.getOverlays().add(mScaleBarOverlay);
    }

    private boolean mapToCurrentLocation() {
        if (currentLocation == null) return false;
        GeoPoint myPosition = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        centerMap(myPosition, true);
        return true;
    }

    private void centerMap(IGeoPoint newCenter, boolean animate) {
        if (animate) {
            mMapView.getController().animateTo(newCenter);
        } else {
            mMapView.setExpectedCenter(newCenter);
        }
        mMapView.getController().setZoom(MAP_ZOOM);
    }

    private void addMarker(String title, double latitude, double longitude, int iconId) {
        MapState.MarkerDescriptor descriptor = new MapState.MarkerDescriptor(latitude, longitude,
                title, iconId);
        addMarker(descriptor);
    }

    private void addMarker(MapState.MarkerDescriptor descriptor) {
        GeoPoint point = new GeoPoint(descriptor.latitude, descriptor.longitude);
        Marker myMarker = new Marker(mMapView);
        Drawable icon = descriptor.iconId == 0 ? mMapView.getRepository().getDefaultMarkerIcon() :
                getResources().getDrawable(descriptor.iconId);

        myMarker.setPosition(point);
        myMarker.setTitle(descriptor.title);
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        myMarker.setIcon(icon);
        // todo: make marker small when zooming out

        mMapView.getOverlays().add(myMarker);
        mapMarkers.add(descriptor);
    }

    private void restoreMapState(MapState oldState) {
        if (oldState == null) {
            return;
        }
        for (MapState.MarkerDescriptor descriptor : oldState.markersDescriptors) {
            addMarker(descriptor);
        }
        GeoPoint mapCenter = new GeoPoint(oldState.mapCenterLatitude, oldState.mapCenterLongitude);
        centerMap(mapCenter, false);
        mMapView.getController().setZoom(oldState.zoom);
    }

    private MapState currMapState() {
        return new MapState(mapMarkers, mMapView.getMapCenter(), mMapView.getZoomLevelDouble());
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
        outState.putSerializable("map_old_state", currMapState());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        System.out.println("MainActivity.onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        Serializable oldState = savedInstanceState.getSerializable("map_old_state");
        if (!(oldState instanceof MapState)) {
            return; // ignore
        }
        restoreMapState((MapState) oldState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("map_old_state", currMapState());
        startActivity(intent);
    }

    /**
     * class that stores the information of a marker
     */
    private static class MapState implements Serializable {
        ArrayList<MarkerDescriptor> markersDescriptors;
        double mapCenterLatitude;
        double mapCenterLongitude;
        double zoom;

        public MapState(ArrayList<MarkerDescriptor> markers, IGeoPoint mapCenter, double zoom) {
            this.mapCenterLatitude = mapCenter.getLatitude();
            this.mapCenterLongitude = mapCenter.getLongitude();
            this.zoom = zoom;
            this.markersDescriptors = new ArrayList<>();
            this.markersDescriptors = markers;
        }

        public static class MarkerDescriptor implements Serializable {
            final double latitude;
            final double longitude;
            final int iconId;
            final String title;

            MarkerDescriptor(double latitude, double longitude, String title, int iconId) {
                this.latitude = latitude;
                this.longitude = longitude;
                this.title = title;
                this.iconId = iconId;
            }
        }
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
