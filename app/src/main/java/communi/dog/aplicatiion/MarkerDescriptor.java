package communi.dog.aplicatiion;

import java.io.Serializable;

/**
 * class that stores the information of a marker
 */
public class MarkerDescriptor implements Serializable {
    private final double latitude;
    private final double longitude;
    private final String text;
    private final String id;
    private final String userId;
    private final boolean isDogsitter;
    private final boolean isFood;
    private final boolean isMedication;

    // todo: match arguments order to the addMarker method
    MarkerDescriptor(String text, double latitude, double longitude, boolean isDogsitter, boolean isFood, boolean isMedication, String creatorUserId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.text = text;
        this.userId = creatorUserId;
        this.id = generateMarkerId(creatorUserId);
        this.isDogsitter = isDogsitter;
        this.isFood = isFood;
        this.isMedication = isMedication;
    }

    private String generateMarkerId(String userId) {
        // todo: maybe not just the user id? if not than this field is redundant
        return userId;
    }

    public boolean isDogsitter() {
        return isDogsitter;
    }

    public boolean isFood() {
        return isFood;
    }

    public boolean isMedication() {
        return isMedication;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
