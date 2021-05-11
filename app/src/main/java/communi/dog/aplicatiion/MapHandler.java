package communi.dog.aplicatiion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.api.IGeoPoint;
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


import static android.content.Context.LOCATION_SERVICE;

public class MapHandler {
    private static final double MAP_DEFAULT_ZOOM = 18.0;
    private static final double MAP_MAX_ZOOM = 20.0;
    private static final double MAP_MIN_ZOOM = 9.0;
    static final int DEFAULT_MARKER_ICON_ID = 0;
    private final MapView mMapView;
    private final Activity mCalledActivity;
    private final ArrayList<MarkerDescriptor> mapMarkers = new ArrayList<>();
    private Location currentLocation = null;

    /**
     * @param mapView        the founded mapView
     * @param calledActivity the activity that holds the mapView as context
     */
    public MapHandler(MapView mapView, Activity calledActivity) {
        this.mMapView = mapView;
        this.mCalledActivity = calledActivity;
    }

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

    public void initMap() {
        // initialize the map
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mMapView.setMultiTouchControls(true);
        mMapView.getController().setZoom(MAP_DEFAULT_ZOOM);
        mMapView.setMaxZoomLevel(MAP_MAX_ZOOM);
        mMapView.setMinZoomLevel(MAP_MIN_ZOOM);

        // enable user location
        LocationManager mLocationManager = (LocationManager) mCalledActivity.getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener);

        final MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Intent intent = new Intent(mCalledActivity, AddMarkerActivity.class);
                intent.putExtra("marker_latitude", p.getLatitude());
                intent.putExtra("marker_longitude", p.getLongitude());
                intent.putExtra("map_old_state", currentState());
                intent.putExtra("userId", mCalledActivity.getIntent().getStringExtra("userId"));

                mCalledActivity.startActivity(intent);
                return false;
            }
        };
        mMapView.getOverlays().add(new MapEventsOverlay(mReceive));

        addMyLocationIconOnMap();
        addScaleBarOnMap();
    }

    private void addMyLocationIconOnMap() {
        // set my location on the map
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mCalledActivity), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setOptionsMenuEnabled(true);
        // todo: make the purple circle disappear

        // add to map
        mMapView.getOverlays().add(mLocationOverlay);
    }

    private void addScaleBarOnMap() {
        final DisplayMetrics dm = mCalledActivity.getResources().getDisplayMetrics();
        // set scale bar
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        // add to map
        mMapView.getOverlays().add(mScaleBarOverlay);
    }

    boolean mapToCurrentLocation() {
        if (currentLocation == null) return false;
        GeoPoint myPosition = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        centerMap(myPosition, true);
        return true;
    }

    void centerMap(IGeoPoint newCenter, boolean animate) {
        if (animate) {
            mMapView.getController().animateTo(newCenter);
        } else {
            mMapView.setExpectedCenter(newCenter);
        }
        mMapView.getController().setZoom(MAP_DEFAULT_ZOOM);
    }

    void addMarker(String title, double latitude, double longitude, int iconId) {
        MarkerDescriptor descriptor = new MarkerDescriptor(latitude, longitude,
                title, iconId);
        addMarker(descriptor);
    }

    private void addMarker(MarkerDescriptor descriptor) {
        GeoPoint point = new GeoPoint(descriptor.latitude, descriptor.longitude);
        Marker myMarker = new Marker(mMapView);
        Drawable icon = descriptor.iconId == DEFAULT_MARKER_ICON_ID ?
                mMapView.getRepository().getDefaultMarkerIcon() :
                ResourcesCompat.getDrawable(mCalledActivity.getResources(), descriptor.iconId, mCalledActivity.getTheme());

        myMarker.setPosition(point);
        myMarker.setTitle(descriptor.title);
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        myMarker.setIcon(icon);
        // todo: make marker's icon smaller when zooming out

        mapMarkers.add(descriptor);
        mMapView.getOverlays().add(myMarker);
    }

    void restoreState(MapState oldState) {
        if (oldState == null) {
            return;
        }
        for (MarkerDescriptor descriptor : oldState.markersDescriptors) {
            addMarker(descriptor);
        }
        GeoPoint mapCenter = new GeoPoint(oldState.mapCenterLatitude, oldState.mapCenterLongitude);
        centerMap(mapCenter, false);
        mMapView.getController().setZoom(oldState.zoom);
    }

    MapState currentState() {
        return new MapState(mapMarkers, mMapView.getMapCenter(), mMapView.getZoomLevelDouble());
    }

    /**
     * class that stores the information of a map
     */
    public static class MapState implements Serializable {
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
    }

    /**
     * class that stores the information of a marker
     */
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
