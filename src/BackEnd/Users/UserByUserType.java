package BackEnd.Users;
import java.util.UUID;

public class UserByUserType {
    public static User createUser(UserType userType, String firstName, String lastName, String username, String password) {
        User user = switch (userType) {
            case ADMIN -> new Administrator(firstName, lastName, username, password);
            case WAITER -> new Waiter(firstName, lastName, username, password);
            case KITCHEN -> new Cook(firstName, lastName, username, password);
        };

        // Assign a unique ID
        user.setId(UUID.randomUUID());

        return user;
    }
}
