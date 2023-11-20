public class Waiter extends User {
    public static final UserType userType = UserType.WAITER;

    public Waiter(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password, userType);
    }

    public Waiter() {
        this.setUserType(userType);
    }
}
