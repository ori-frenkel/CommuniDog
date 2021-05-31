package communi.dog.aplicatiion;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class DB implements Serializable {
    private FirebaseDatabase database;
    private DatabaseReference IdsRef;
    private DatabaseReference usersRef;
    private DatabaseReference mapStateRef;
    private HashSet<User> users;
    private HashMap<String, String> allUsers;
    private HashSet<String> allIDs;
    private User currentUser;
    private MapState mapState;

    public DB() {
        this.database = FirebaseDatabase.getInstance();
        this.IdsRef = database.getReference("ID's");
        this.usersRef = database.getReference("Users");
        this.mapStateRef = database.getReference("MapState");
        this.allUsers = new HashMap<>();
        this.allIDs = new HashSet<String>();
        this.users = new HashSet<>();
        this.currentUser = new User();
        this.refreshDataUsers();
        this.refreshDataMapState();
        this.mapState = new MapState();
    }

    public void refreshDataUsers() {
        readDataIdsInUse(new DB.FirebaseCallback() {
            @Override
            public void onCallback(HashSet<User> users, HashSet<String> allIds) {
            }
        });
    }

    public void refreshDataMapState() {
        readDataMaoState(new DB.FirebaseCallbackMapState() {
            @Override
            public void onCallbackMapState(MapState mapState) {
            }
        });
    }

    public void refreshDataGetUser(String userId) {
        readDataGetUser(new DB.FirebaseCallbackUser() {
            @Override
            public void onCallbackUser(User currentUser) {
            }
        }, userId);
    }

    private void readDataMaoState(DB.FirebaseCallbackMapState firebaseCallback) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, MarkerDescriptor> markersDescriptor = new HashMap<>();
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
                        markersDescriptor.put(ds.getKey(), newMarkerDescriptor);
                    }
                }
                mapState = new MapState();
                mapState.setMarkersDescriptors(markersDescriptor);
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
                        String name = ds.child("name").getValue(String.class);
                        String dogName = ds.child("dogName").getValue(String.class);
                        dogName = dogName != null ? dogName : "";
                        String phoneNumber = ds.child("phoneNumber").getValue(String.class);
                        phoneNumber = phoneNumber != null ? phoneNumber : "";
                        String description = ds.child("description").getValue(String.class);
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

    private void readDataGetUser(DB.FirebaseCallbackUser firebaseCallback, String userId) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = (String) snapshot.child(userId).child("email").getValue();
                String pass = (String) snapshot.child(userId).child("password").getValue();
                String name = (String) snapshot.child(userId).child("name").getValue();
                currentUser = new User(userId, email, pass, name);
                firebaseCallback.onCallbackUser(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListenerUsers);
    }

    private interface FirebaseCallback {
        void onCallback(HashSet<User> users, HashSet<String> allIds);
    }

    private interface FirebaseCallbackMapState {
        void onCallbackMapState(MapState mapState);
    }

    private interface FirebaseCallbackUser {
        void onCallbackUser(User currentUser);
    }

    public void addUser(String userId, String userEmail, String userPassword, String userName) {
        User newUser = new User(userId, userEmail, userPassword, userName);
        this.usersRef.child(userId).setValue(newUser);
    }

    public boolean isUserExists(String userId, String userPassword) {
        for (User user: users){
            if (user.getId().equals(userId) && user.getPassword().equals(userPassword)){
                return true;
            }
        }
        return false;
    }

    public void updateUser(String userId, String userEmail, String userPassword, String userName, String phoneNumber, String dogName, String userDescription) {
        User newUser = new User(userId, userEmail, userPassword, userName, phoneNumber, dogName, userDescription);
        this.usersRef.child(userId).setValue(newUser);
    }

    public void deleteUser(String userId) {
        this.usersRef.child(userId).setValue(null);
        //todo update local db
    }

    public boolean idDoubleUser(String id) {
        for (User user: users){
            if (user.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public boolean idExistsInDB(String id) {
        return allIDs.contains(id);
    }

    void restoreState(DBState oldState) {
        if (oldState == null) {
            return;
        }
        this.allUsers = new HashMap<>(oldState.allUsers);
        this.allIDs = new HashSet<String>(oldState.allIDs);
    }

    DBState currentState() {
        return new DBState(allUsers, allIDs);
    }

    public static class DBState implements Serializable {
        HashMap<String, String> allUsers;
        private HashSet<String> allIDs;

        public DBState(HashMap<String, String> allUsers, HashSet<String> allIDs) {
            this.allUsers = allUsers;
            this.allIDs = allIDs;
        }
    }

    public void setCurrentUser(String userId){
        for (User user: users){
            if (user.getId().equals(userId)){
                this.currentUser = user;
            }
        }
    }

    public User getUser(){
        return this.currentUser;
    }

    public MapState getMapState() {
        return mapState;
    }

    public void addMarkerDescriptor(MarkerDescriptor markerDescriptor){
        this.mapStateRef.child("markersDescriptors").child(markerDescriptor.getId()).setValue(markerDescriptor);
    }

    public void setMarker(MarkerDescriptor marker){
        this.mapStateRef.child("markersDescriptors").child(marker.getId()).setValue(marker);
    }

    public void removeMarker(String markerId){
        this.mapStateRef.child("markersDescriptors").child(markerId).setValue(null);
    }

    //    public void setMapState(MapState mapState){
//        this.mapState = mapState;
//        this.mapStateRef.child("mapCenterLatitude").setValue(mapState.getMapCenterLatitude());
//        this.mapStateRef.child("mapCenterLongitude").setValue(mapState.getMapCenterLongitude());
//        this.mapStateRef.child("zoom").setValue(mapState.getZoom());
//        HashMap<String, MarkerDescriptor> markersDescriptors = mapState.getMarkersDescriptors();
//        for (Map.Entry<String, MarkerDescriptor> entry : markersDescriptors.entrySet()) {
//            System.out.println(entry.getKey());
//            this.mapStateRef.child("markersDescriptors").child(entry.getKey()).setValue(entry.getValue());
//        }
//    }
}
