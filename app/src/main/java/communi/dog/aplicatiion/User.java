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

    public User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserDogName() {
        return userDogName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserDogName(String userDogName) {
        this.userDogName = userDogName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getUserDescription() {
        return userDescription;
    }
}
