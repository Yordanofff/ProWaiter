package BackEnd.Users;

public abstract class User {
    private String username;
    private String firstName;
    private String lastName;
    private UserType userType;
    private String password;
    public static final int MINIMUM_USERNAME_LENGTH = 5;
    public static final int MINIMUM_USERNAME_PASSWORD_LENGTH = 8;

    public User(String firstName, String lastName, String username, String password, UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }
}
