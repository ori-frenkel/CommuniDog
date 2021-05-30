package communi.dog.aplicatiion;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;

/**
 * class that stores the information of a map
 */
public class MapState implements Serializable {
    private HashMap<String, MarkerDescriptor> markersDescriptors; // final?
    private double mapCenterLatitude;
    private double mapCenterLongitude;
    private double zoom;

    public MapState() {
        this.markersDescriptors = new HashMap<>();
        // todo: select initial values
        this.mapCenterLatitude = 32.1007;
        this.mapCenterLongitude = 34.8070;
        this.zoom = 18;

    }

    public void setCenter(IGeoPoint newCenter) {
        mapCenterLatitude = newCenter.getLatitude();
        mapCenterLongitude = newCenter.getLongitude();
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
        this.markersDescriptors = markersDescriptors;
    }
}
