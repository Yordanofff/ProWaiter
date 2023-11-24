package BackEnd.Users;

import BackEnd.DB.PosgtgeSQL;
import FrontEnd.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager {
    // todo - part of BackEnd.Users.Administrator?
    // todo - load/update users from DB before login
    // todo - change user type /promotion from-to/
    private static List<User> activeUsers = new ArrayList<>();
    private static Map<UserType, List<User>> usersByType = new HashMap<>();
    private static final PosgtgeSQL db = new PosgtgeSQL();

    public static void printAllUsers() {
        // todo - delete
        for (User user : getActiveUsers()) {
            System.out.println(user);
        }
    }

    public static int getActiveUserCount() {
        return getActiveUsers().size();
    }

    public static List<User> getActiveUsers() {
        return db.getUsers(10);
    }

    public static Map<UserType, List<User>> getUsersByType() {
        return usersByType;
    }

    public static void addUser(User user) {
        boolean userNameAlreadySet = false;
        String userName = "";
        if (getActiveUserCount() == 0) {
            if (user.getUserType() != UserType.ADMIN) {
                throw new RuntimeException("The first user should always be the \"admin\" user.");
            }
            userName = "admin";
            userNameAlreadySet = true;
        }

        String firstName = UserInput.getFirstName();
        String lastName = UserInput.getLastName();

        if (!userNameAlreadySet) {
            userName = UserInput.getUsername();
        }

        String password = UserInput.getPassword(userName);
        // Set common fields
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(userName);
        user.setPassword(password);

        db.addUser(user);

        // add to all ActiveUsers list
//        activeUsers.add(user);

        // Initialize the list if it doesn't exist for the given BackEnd.Users.UserType
//        usersByType.putIfAbsent(user.getUserType(), new ArrayList<>());

        // Add to usersByType list
//        usersByType.get(user.getUserType()).add(user);

        // todo - need to write users in DB/File and get all users from there on startup.
    }

    public static boolean isInitialAdminAccountCreated() {
        // todo - delete? not needed
        for (User user : activeUsers) {
            if (user.getUsername().equalsIgnoreCase("admin")) {
                return true;
            }
        }
        return false;
    }

    public static void addAdmin() {
        User admin = new Administrator();
        addUser(admin);
    }

    public static void addCook() {
        Cook cook = new Cook();
        addUser(cook);
    }

    public static void addWaiter() {
        Waiter waiter = new Waiter();
        addUser(waiter);
    }

    public void removeUser(User user) {
        // todo - delete user from DB/File --> Don't delete - set user as "non active"
        if (user.getUsername().equalsIgnoreCase("admin")) {
            throw new RuntimeException("The admin account cannot be removed.");
        } else {
            activeUsers.remove(user);
            usersByType.get(user.getUserType()).remove(user);
            System.out.println(user.getUsername() + " has logged out.");
        }
    }

    public static void displayActiveUsers() {
        System.out.println("Active BackEnd.Users:");
        for (User user : activeUsers) {
            System.out.println(user.getUserType() + " - " + user.getUsername() + " - " + user.getFullName());
        }
    }

    public static void displayUsersByType(UserType userTypeToCheck) {
        if (usersByType.containsKey(userTypeToCheck) && usersByType.get(userTypeToCheck) != null) {

            System.out.println("BackEnd.Users with BackEnd.Users.UserType " + userTypeToCheck + ":");
            List<User> userList = usersByType.get(userTypeToCheck);
            for (User user : userList) {
                System.out.println("Name: " + user.getFullName() + ", Username: " + user.getUsername());
            }
        } else {
            System.out.println("No users found for BackEnd.Users.UserType: " + userTypeToCheck);
        }
    }

    public static void displayAllUsersByType() {
        for (UserType type : UserType.values()) {
            displayUsersByType(type);
        }
    }

}

