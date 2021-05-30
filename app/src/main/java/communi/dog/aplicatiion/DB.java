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
import java.util.Objects;

public class DB implements Serializable {
    private FirebaseDatabase database;
    private DatabaseReference IdsRef;
    private DatabaseReference usersRef;
    private HashSet<User> users;
    private HashMap<String, String> allUsers;
    private HashSet<String> allIDs;
    private User currentUser;

    public DB() {
        this.database = FirebaseDatabase.getInstance();
        this.IdsRef = database.getReference("ID's");
        this.usersRef = database.getReference("Users");
        this.allUsers = new HashMap<>();
        this.allIDs = new HashSet<String>();
        this.users = new HashSet<>();
        this.currentUser = new User();
        this.refreshDataUsers();
    }

    public void refreshDataUsers() {
        readDataIdsInUse(new DB.FirebaseCallback() {
            @Override
            public void onCallback(HashSet<User> users, HashSet<String> allIds) {
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

    private void readDataIdsInUse(DB.FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListenerUsers = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds != null) {
                        String id = ds.child("id").getValue(String.class);
                        String password = ds.child("password").getValue(String.class);
                        String email = ds.child("email").getValue(String.class);
                        String name = ds.child("email").getValue(String.class);
                        users.add(new User(id, email, password, name));
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

    public void updateUser(String userId, String userEmail, String userPassword) {
        User newUser = new User(userId, userEmail, userPassword);
        this.usersRef.child(userId).setValue(newUser);
    }

    public void deleteUser(String userId) {
        this.usersRef.child(userId).setValue(null);
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
}
