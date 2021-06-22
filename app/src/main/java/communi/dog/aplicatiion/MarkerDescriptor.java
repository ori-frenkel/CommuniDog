package communi.dog.aplicatiion;

import java.io.Serializable;

/**
 * class that stores the information of a marker
 */
public class MarkerDescriptor implements Serializable {
    private double latitude;
    private double longitude;
    private String text;
    private String id; //final
    private boolean isDogsitter;
    private boolean isFood;
    private boolean isMedication;

    MarkerDescriptor(String text, double latitude, double longitude, boolean isDogsitter, boolean isFood, boolean isMedication, String creatorUserId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.text = text;
        this.id = generateMarkerId(creatorUserId);
        this.isDogsitter = isDogsitter;
        this.isFood = isFood;
        this.isMedication = isMedication;
    }

    // empty constructor for FireBase redundant
    public MarkerDescriptor() {
    }

    private String generateMarkerId(String userId) {
        // todo: maybe not just the user id? if not than this field is redundant
        return userId;
    }

    public void setNewLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setServices(boolean isDogsitter, boolean isFood, boolean isMedication) {
        this.isDogsitter = isDogsitter;
        this.isFood = isFood;
        this.isMedication = isMedication;
    }

    public void setText(String text) {
        this.text = text;
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
        User user = CommuniDogApp.getInstance().getDb().getUser(id);
        if (user != null) {
            text = generateText(user);
        }

        return text;
    }

    private String generateText(User user) {
        String msg = user.getUserName() + " offers:\n";
        if (isDogsitter) msg += "Dogsitter services\n";
        if (isFood) msg += "Extra food\n";
        if (isMedication) msg += "Extra medication\n";
        String contacts = "";
        if (!user.getEmail().isEmpty())
            contacts += "Email - " + user.getEmail() + "\n";
        if (!user.getPhoneNumber().isEmpty())
            contacts += "Phone - " + user.getPhoneNumber() + "\n";
        if (!contacts.isEmpty()) msg += "In order to contact:\n" + contacts;
        return msg;
    }
}
