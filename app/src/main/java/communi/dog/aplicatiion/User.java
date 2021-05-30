package communi.dog.aplicatiion;

public class User {
    private String id;
    private String email;
    private String password;
    private String phoneNumber;
    private String userName;
    private String userDogName;
    private String userDescription;

    public User(String id, String email, String password, String userName) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.userDogName = "";
        this.phoneNumber = "";
        this.userDescription = "";
    }

    public User(String id, String email, String password, String userName, String phoneNumber, String dogName, String userDescription) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.userDogName = dogName;
        this.phoneNumber = phoneNumber;
        this.userDescription = userDescription;
    }

    public User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDogName() {
        return userDogName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getUserDescription() {
        return userDescription;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }
}
