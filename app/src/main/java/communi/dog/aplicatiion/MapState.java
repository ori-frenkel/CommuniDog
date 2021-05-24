package communi.dog.aplicatiion;

import org.osmdroid.api.IGeoPoint;

import java.io.Serializable;
import java.util.HashMap;

/**
 * class that stores the information of a map
 */
public class MapState implements Serializable {
    HashMap<String, MarkerDescriptor> markersDescriptors;
    double mapCenterLatitude;
    double mapCenterLongitude;
    double zoom;

    public MapState(HashMap<String, MarkerDescriptor> markers, IGeoPoint mapCenter, double zoom) {
        this.mapCenterLatitude = mapCenter.getLatitude();
        this.mapCenterLongitude = mapCenter.getLongitude();
        this.zoom = zoom;
        this.markersDescriptors = markers;
    }
}
