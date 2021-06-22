package communi.dog.aplicatiion;

import androidx.annotation.NonNull;

public class User {
    private String id;
    private String email;
    private String phoneNumber;
    private String userName;
    private String userDogName;
    private String userDescription;

    public User(String id, String email, String userName) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.userDogName = "";
        this.phoneNumber = "";
        this.userDescription = "";
    }

    public User(String id, String email, String userName, String phoneNumber, String dogName, String userDescription) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.userDogName = dogName;
        this.phoneNumber = phoneNumber;
        this.userDescription = userDescription;
    }

    // empty constructor for FireStore
    public User() {
    }

    public String getId() {
        return id;
    }

    @NonNull
    public String getUserName() {
        return noNull(userName);
    }

    @NonNull
    public String getUserDogName() {
        return noNull(userDogName);
    }

    @NonNull
    public String getEmail() {
        return noNull(email);
    }

    @NonNull
    public String getPhoneNumber() {
        return noNull(phoneNumber);
    }

    public String getUserDescription() {
        return noNull(userDescription);
    }

    @NonNull
    private String noNull(String val) {
        return val == null ? "" : val;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserDogName(String userDogName) {
        this.userDogName = userDogName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }
}
