package communi.dog.aplicatiion;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class DB implements Serializable {
    private final static String SP_NAME = "local_db";
    private final static String SP_CURR_LATITUDE = "latitude";
    private final static String SP_CURR_LONGITUDE = "longitude";

    private FirebaseDatabase database;
    private DatabaseReference IdsRef;
    private DatabaseReference usersRef;
    private DatabaseReference mapStateRef;
    private HashSet<User> users;
    private HashSet<String> allIDs;
    private User currentUser;
    private MapState mapState;
    private final SharedPreferences sp;

    public DB(Context context) {
        this.database = FirebaseDatabase.getInstance();
        this.sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        this.IdsRef = database.getReference("ID's");
        this.usersRef = database.getReference("Users");
        this.mapStateRef = database.getReference("MapState");
        this.allIDs = new HashSet<>();
        this.users = new HashSet<>();
        this.currentUser = new User();
        this.refreshDataUsers();
        this.refreshDataMapState();
        this.mapState = MapState.getInstance();

        readLastLocationFromSp();
    }

    private void readLastLocationFromSp() {
        double lat = sp.getFloat(SP_CURR_LATITUDE, MapState.DEF_LATITUDE);
        double lon = sp.getFloat(SP_CURR_LONGITUDE, MapState.DEF_LONGITUDE);
        mapState.setCenter(lat, lon);
    }

    public void saveLocationToSp(double latitude, double longitude) {
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putFloat(SP_CURR_LATITUDE, (float) latitude);
        spEditor.putFloat(SP_CURR_LONGITUDE, (float) longitude);
        spEditor.apply();
    }

    public void refreshDataUsers() {
        readDataIdsInUse(new DB.FirebaseCallback() {
            @Override
            public void onCallback(HashSet<User> users, HashSet<String> allIds) {
            }
        });
    }

    public void refreshDataMapState() {
        readDataMapState(new DB.FirebaseCallbackMapState() {
            @Override
            public void onCallbackMapState(MapState mapState) {
            }
        });
    }


    private void readDataMapState(DB.FirebaseCallbackMapState firebaseCallback) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, MarkerDescriptor> markersDescriptors = new HashMap<>();
                for (DataSnapshot ds : snapshot.child("markersDescriptors").getChildren()) {
                    if (ds != null) {
                        Double latitude = ds.child("latitude").getValue(Double.class);
                        Double longitude = ds.child("longitude").getValue(Double.class);
                        String text = ds.child("text").getValue(String.class);
                        String id = ds.child("id").getValue(String.class);
                        Boolean isDogSitter = ds.child("dogsitter").getValue(Boolean.class);
                        Boolean isFood = ds.child("food").getValue(Boolean.class);
                        Boolean isMedication = ds.child("medication").getValue(Boolean.class);
                        MarkerDescriptor newMarkerDescriptor = new MarkerDescriptor(text, latitude, longitude, isDogSitter, isFood, isMedication, id);
                        markersDescriptors.put(ds.getKey(), newMarkerDescriptor);
                    }
                }
                mapState.setMarkersDescriptors(markersDescriptors);
                readLastLocationFromSp();
                firebaseCallback.onCallbackMapState(mapState);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        mapStateRef.addListenerForSingleValueEvent(valueEventListenerUsers);
    }

    private void readDataIdsInUse(DB.FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        String id = ds.child("id").getValue(String.class);
                        String password = ds.child("password").getValue(String.class);
                        String email = ds.child("email").getValue(String.class);
                        String name = ds.child("userName").getValue(String.class);
                        String dogName = ds.child("userDogName").getValue(String.class);
                        dogName = dogName != null ? dogName : "";
                        String phoneNumber = ds.child("phoneNumber").getValue(String.class);
                        phoneNumber = phoneNumber != null ? phoneNumber : "";
                        String description = ds.child("userDescription").getValue(String.class);
                        description = description != null ? description : "";
                        users.add(new User(id, email, password, name, phoneNumber, dogName, description));
                    }
                }
                firebaseCallback.onCallback(users, allIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListenerUsers);

        ValueEventListener valueEventListenerIds = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        allIDs.add((String) ds.getValue());
                    }
                }
                firebaseCallback.onCallback(users, allIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        IdsRef.addListenerForSingleValueEvent(valueEventListenerIds);
    }

    private interface FirebaseCallback {
        void onCallback(HashSet<User> users, HashSet<String> allIds);
    }

    private interface FirebaseCallbackMapState {
        void onCallbackMapState(MapState mapState);
    }

    public void addUser(String userId, String userEmail, String userPassword, String userName) {
        User newUser = new User(userId, userEmail, userPassword, userName);
        this.usersRef.child(userId).setValue(newUser);
    }

    public boolean isUserExists(String userId, String userPassword) {
        for (User user : users) {
            if (user.getId().equals(userId) && user.getPassword().equals(userPassword)) {
                return true;
            }
        }
        return false;
    }

    public void updateUser(String userId, String userEmail, String userPassword, String userName, String phoneNumber, String dogName, String userDescription) {
        User newUser = new User(userId, userEmail, userPassword, userName, phoneNumber, dogName, userDescription);
        this.usersRef.child(userId).setValue(newUser);
    }

    public boolean idDoubleUser(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean idExistsInDB(String id) {
        return allIDs.contains(id);
    }

    public void setCurrentUser(String userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                this.currentUser = user;
            }
        }
    }

    public User getUser() {
        return this.currentUser;
    }

    public void resetUser() {
        this.currentUser = new User();
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.remove(SP_CURR_LATITUDE);
        spEditor.remove(SP_CURR_LONGITUDE);
        spEditor.apply();
    }

    public MapState getMapState() {
        return mapState;
    }

    /**
     * if the descriptor already in the bd, update it, else add a new one
     */
    public void updateMarkerDescriptor(MarkerDescriptor marker) {
        this.mapStateRef.child("markersDescriptors").child(marker.getId()).setValue(marker);
    }

    public void removeMarker(String markerId) {
        this.mapStateRef.child("markersDescriptors").child(markerId).setValue(null);
    }
}
