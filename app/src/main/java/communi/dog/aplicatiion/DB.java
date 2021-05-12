package communi.dog.aplicatiion;

import android.content.Intent;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class DB implements Serializable {
    private FirebaseDatabase database;
    private DatabaseReference IdsRef;
    private DatabaseReference usersRef;
    private HashMap<String, String> allUsers;
    private HashSet<String> allIDs;
    User currentUser;

    public DB(){
        this.database = FirebaseDatabase.getInstance();
        this.IdsRef = database.getReference("ID's");
        this.usersRef = database.getReference("Users");
        this.allUsers = new HashMap<>();
        this.allIDs = new HashSet<String>();
        this.currentUser = new User();
    }

    public void refreshDataUsers(){
        readDataIdsInUse(new DB.FirebaseCallback() {
            @Override
            public void onCallback(HashMap<String, String> allUsers, HashSet<String> allIDs) {
            }
        });
    }

    public void refreshDataGetUser(String userId){
        readDataGetUser(new DB.FirebaseCallbackUser() {
            @Override
            public void onCallbackUser(User currentUser) {
            }
        }, userId);
    }

    private void readDataIdsInUse(DB.FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        String id = ds.child("id").getValue(String.class);
                        String password = ds.child("password").getValue(String.class);
                        allUsers.put(id, password);
                    }
                }
                firebaseCallback.onCallback(allUsers, allIDs);
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
                firebaseCallback.onCallback(allUsers, allIDs);
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
                String email = (String)snapshot.child(userId).child("email").getValue();
                String pass = (String)snapshot.child(userId).child("password").getValue();
                currentUser = new User(userId, email, pass);
                firebaseCallback.onCallbackUser(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListenerUsers);
    }

    public User getUser(){
        return this.currentUser;
    }

    private interface FirebaseCallback {
        void onCallback(HashMap<String, String> allUsers, HashSet<String> allIDs);
    }

    private interface FirebaseCallbackUser {
        void onCallbackUser(User currentUser);
    }

    public void addUser(String userId, String userEmail, String userPassword) {
        User newUser = new User(userId, userEmail, userPassword);
        this.usersRef.child(userId).setValue(newUser);
    }

    public boolean isUserExists(String userId, String userPassword){
        return allUsers.get(userId) != null &&
                Objects.equals(allUsers.get(userId), userPassword);
    }

    public void updateUser(String userId, String userEmail, String userPassword){
        User newUser = new User(userId, userEmail, userPassword);
        this.usersRef.child(userId).setValue(newUser);
    }

    public void deleteUser(String userId){
        this.usersRef.child(userId).setValue(null);
    }

    public boolean idDoubleUser(String id) {
        return allUsers.get(id) != null;
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

        public DBState(HashMap<String, String> allUsers, HashSet<String> allIDs){
            this.allUsers = allUsers;
            this.allIDs = allIDs;
        }
    }
}
