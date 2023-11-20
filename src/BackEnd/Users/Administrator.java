package BackEnd.Users;

public class Administrator extends User {
    public static final UserType userType = UserType.ADMIN;

    public Administrator(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password, userType);
    }

    public Administrator() {
        this.setUserType(userType);
    }

}
