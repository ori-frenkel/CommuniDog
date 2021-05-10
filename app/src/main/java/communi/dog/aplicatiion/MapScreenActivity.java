package communi.dog.aplicatiion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class MapScreenActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private final double MAP_ZOOM = 18.0;
    private MapView mMapView = null;
    private final ArrayList<Marker> mapMarkers = new ArrayList<>();
    private Location currentLocation = null;

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
        btnMyProfile.setOnClickListener(v ->
                Toast.makeText(this, "link to my profile screen", Toast.LENGTH_SHORT).show());

        ImageView btnMoreInfo = findViewById(R.id.buttonMoreInfoMapActivity);
        btnMoreInfo.setOnClickListener(v ->
                Toast.makeText(this, "link to more info screen", Toast.LENGTH_SHORT).show());

        ImageView btnBack = findViewById(R.id.buttonTempBackToRegister);
        btnBack.setOnClickListener(v -> {
            Toast.makeText(this, "this is a temp button for development", Toast.LENGTH_SHORT).show();
            goBackToLoginScreen();
        });


        if (activityIntent.hasExtra("map_old_state")) {
            restoreMapState((MapState) activityIntent.getSerializableExtra("map_old_state"));
        } else if (savedInstanceState == null) {
            // new map
            if (!mapToCurrentLocation()) {
                // todo: set initial coordinates using database?
                IGeoPoint initialLocation = new GeoPoint(32.1007, 34.8070);
                mapToLocation(initialLocation, false);
            }
        }

        if (activityIntent.getBooleanExtra("add_marker", false)) {
            addMarker(activityIntent.getStringExtra("marker_description"),
                    activityIntent.getDoubleExtra("marker_latitude", 0),
                    activityIntent.getDoubleExtra("marker_longitude", 0));
//            mapToLocation(mapMarkers.get(mapMarkers.size() - 1).getPosition());
        }
    }

    private void initializeMap() {
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();

        // initialize the map
        mMapView = findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mMapView.setMultiTouchControls(true);
        mMapView.getController().setZoom(MAP_ZOOM);

        // enable user location
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener);

        // set my location on the map
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);

        // set scale bar
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        // set event receiver
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
                intent.putExtra("map_old_state", new MapState(mapMarkers, mMapView.getMapCenter()));
                startActivity(intent);
                return false;
            }
        };

        // add map overlays
        mMapView.getOverlays().add(mLocationOverlay);
        mMapView.getOverlays().add(mScaleBarOverlay);
        mMapView.getOverlays().add(new MapEventsOverlay(mReceive));
    }

    private boolean mapToCurrentLocation() {
        if (currentLocation == null) return false;
        GeoPoint myPosition = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        mapToLocation(myPosition, true);
        return true;
    }

    private void mapToLocation(IGeoPoint location, boolean animate) {
        if (animate) {
            mMapView.getController().animateTo(location);
        } else {
            mMapView.setExpectedCenter(location);
        }
        mMapView.getController().setZoom(MAP_ZOOM);
    }

    private void goBackToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("map_old_state", new MapState(mapMarkers, mMapView.getMapCenter()));
        startActivity(intent);
    }

    private void addMarker(String title, double aLatitude, double aLongitude) {
        GeoPoint point = new GeoPoint(aLatitude, aLongitude);
        Marker myMarker = new Marker(mMapView);

        myMarker.setPosition(point);
        myMarker.setTitle(title);
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        mMapView.getOverlays().add(myMarker);
        mapMarkers.add(myMarker);
    }

    private void restoreMapState(@Nullable MapState oldState) {
        if (oldState == null) {
            return;
        }
        for (MapState.MarkerDescriptor descriptor : oldState.markersDescriptors) {
            addMarker(descriptor.title, descriptor.latitude, descriptor.longitude);
        }
        mapToLocation(oldState.currLocation, false);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        System.out.println("MainActivity.onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putSerializable("markers", new MapState(mapMarkers, mMapView.getMapCenter()));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        System.out.println("MainActivity.onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        Serializable oldState = savedInstanceState.getSerializable("markers");
        if (!(oldState instanceof MapState)) {
            return; // ignore
        }
        restoreMapState((MapState) oldState);
    }

    private static class MapState implements Serializable {
        ArrayList<MarkerDescriptor> markersDescriptors;
        IGeoPoint currLocation;

        public MapState(ArrayList<Marker> markers, IGeoPoint currLocation) {
            this.currLocation = currLocation;

            this.markersDescriptors = new ArrayList<>();
            for (Marker marker : markers) {
                this.markersDescriptors.add(new MarkerDescriptor(marker));
            }
        }

        public static class MarkerDescriptor implements Serializable {
            double latitude;
            double longitude;
            String title;

            MarkerDescriptor(Marker marker) {
                this.latitude = marker.getPosition().getLatitude();
                this.longitude = marker.getPosition().getLongitude();
                this.title = marker.getTitle();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToLoginScreen();
    }
}
