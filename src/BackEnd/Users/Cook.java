package BackEnd.Users;

public class Cook extends User {
    public static final UserType userType = UserType.COOK;

    public Cook(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password, userType);
    }

    public Cook() {
        this.setUserType(userType);
    }
}
