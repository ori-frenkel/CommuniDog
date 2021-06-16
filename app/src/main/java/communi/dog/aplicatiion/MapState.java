package communi.dog.aplicatiion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;

/**
 * class that stores the information of a map
 */
public class MapState implements Serializable {
    // todo: select initial values
    public static final float DEF_LATITUDE = 32.1007f;
    public static final float DEF_LONGITUDE = 34.8070f;
    private final HashMap<String, MarkerDescriptor> markersDescriptors;
    private double mapCenterLatitude;
    private double mapCenterLongitude;
    private double zoom;

    private static MapState instance = null;

    public static MapState getInstance() {
        if (instance == null) {
            instance = new MapState();
        }
        return instance;
    }

    private MapState() {
        this.markersDescriptors = new HashMap<>();
        this.mapCenterLatitude = DEF_LATITUDE;
        this.mapCenterLongitude = DEF_LONGITUDE;
        this.zoom = 18;
    }

    public void setCenter(IGeoPoint newCenter) {
        setCenter(newCenter.getLatitude(), newCenter.getLongitude());
    }

    public void setCenter(double latitude, double longitude) {
        mapCenterLatitude = latitude;
        mapCenterLongitude = longitude;
        if (CommuniDogApp.getInstance().getDb() != null) {
            CommuniDogApp.getInstance().getDb().saveLocationToSp(latitude, longitude);
        }
    }

    public IGeoPoint getCenter() {
        return new GeoPoint(mapCenterLatitude, mapCenterLongitude);
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public void addMarker(MarkerDescriptor toAdd) {
        markersDescriptors.put(toAdd.getId(), toAdd);
        mapCenterLatitude = toAdd.getLatitude();
        mapCenterLongitude = toAdd.getLongitude();
    }

    public boolean hasMarker(String idToSearch) {
        return markersDescriptors.containsKey(idToSearch);
    }

    public void removeMarker(String idToRemove) {
        markersDescriptors.remove(idToRemove);
    }

    public HashMap<String, MarkerDescriptor> getMarkersDescriptors() {
        return new HashMap<>(markersDescriptors);
    }

    public MarkerDescriptor getMarker(String markerId) {
        if (markersDescriptors.containsKey(markerId)) return markersDescriptors.get(markerId);
        return null;
    }

    public void setMarkersDescriptors(HashMap<String, MarkerDescriptor> markersDescriptors) {
        this.markersDescriptors.clear();
        this.markersDescriptors.putAll(markersDescriptors);
    }
}
