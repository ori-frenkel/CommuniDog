package communi.dog.aplicatiion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private final DatabaseReference IdsRef;
    private final DatabaseReference usersRef;
    private final DatabaseReference mapStateRef;
    private final HashMap<String, User> users;
    private final HashSet<String> allIDs;
    private User currentUser;
    private FirebaseUser currentFbUser;
    private final MapState mapState;
    private final SharedPreferences sp;
    private final FirebaseAuth mAuth;

    private final MutableLiveData<User> currentUSerMutableLiveData = new MutableLiveData<>();
    public final LiveData<User> currentUSerLiveData = currentUSerMutableLiveData;


    public DB(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        this.IdsRef = database.getReference("ID's");
        this.usersRef = database.getReference("Users");
        this.mapStateRef = database.getReference("MapState");
        this.allIDs = new HashSet<>();
        this.users = new HashMap<>();
        this.currentUser = new User();
        this.refreshDataUsers();
        this.refreshDataMapState();
        this.mapState = MapState.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        readLastLocationFromSp();
    }

    public FirebaseAuth getUsersAuthenticator() {
        return mAuth;
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
        readDataIdsInUse((users, allIds) -> {
        });
    }

    public void refreshDataMapState() {
        readDataMapState(mapState -> {
        });
    }


    private void readDataMapState(DB.FirebaseCallbackMapState firebaseCallback) {
        ValueEventListener valueEventListenerMarkers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, MarkerDescriptor> markersDescriptors = new HashMap<>();
                for (DataSnapshot ds : snapshot.child("markersDescriptors").getChildren()) {
                    if (ds != null) {
                        MarkerDescriptor marker = ds.getValue(MarkerDescriptor.class);
                        markersDescriptors.put(ds.getKey(), marker);
                    }
                }
                mapState.setMarkersDescriptors(markersDescriptors);
                firebaseCallback.onCallbackMapState(mapState);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        mapStateRef.addValueEventListener(valueEventListenerMarkers);
    }


    private void readDataIdsInUse(DB.FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        User user = ds.getValue(User.class);
                        users.put(user.getId(), user);
//                        String id = ds.child("id").getValue(String.class);
//                        String password = ds.child("password").getValue(String.class); // todo: delete - no need to save password anymore
//                        String email = ds.child("email").getValue(String.class);
//                        String name = ds.child("userName").getValue(String.class);
//                        String dogName = ds.child("userDogName").getValue(String.class);
//                        dogName = dogName != null ? dogName : "";
//                        String phoneNumber = ds.child("phoneNumber").getValue(String.class);
//                        phoneNumber = phoneNumber != null ? phoneNumber : "";
//                        String description = ds.child("userDescription").getValue(String.class);
//                        description = description != null ? description : "";
//                        users.put(id, new User(id, email, password, name, phoneNumber, dogName, description));
                    }
                }
                setCurrentUser(currentFbUser);
                firebaseCallback.onCallback(users, allIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addValueEventListener(valueEventListenerUsers);

        ValueEventListener valueEventListenerIds = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allIDs.clear();
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
        IdsRef.addValueEventListener(valueEventListenerIds);
    }

    private interface FirebaseCallback {
        void onCallback(HashMap<String, User> users, HashSet<String> allIds);
    }

    private interface FirebaseCallbackMapState {
        void onCallbackMapState(MapState mapState);
    }

    public void addUser(String userId, String userEmail, String userName) {
        User newUser = new User(userId, userEmail, userName);
        this.usersRef.child(userId).setValue(newUser);
    }

    public void updateUser(String userId, String userEmail, String userName, String phoneNumber, String dogName, String userDescription,
                           boolean isManager, boolean isApproved) {
        User newUser = new User(userId, userEmail, userName, phoneNumber, dogName, userDescription, isManager, isApproved);
        this.usersRef.child(userId).setValue(newUser).addOnSuccessListener(aVoid -> {
            if (newUser.getId().equals(currentUser.getId())) {
                currentUser = newUser;
            } else {
                Log.d("sameUserCheck", "not the current user");
            }
        });
    }

    public boolean idDoubleUser(String id) {
        return users.containsKey(id);
    }

    public boolean idExistsInDB(String id) {
        return allIDs.contains(id);
    }

    public void setCurrentUser(FirebaseUser user) {
        if (user != null) {
            this.currentFbUser = user;
            if (users.containsKey(user.getUid())) {
                this.currentUser = users.get(user.getUid());
                currentUSerMutableLiveData.setValue(currentUser);
            }
        }
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public User getUser(String userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        return null;
    }

    public void logoutUser() {
        mAuth.signOut();
        this.currentFbUser = null;
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
