package BackEnd.Users;

import BackEnd.DB.DBOperations;
import FrontEnd.ConsolePrinter;
import FrontEnd.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static FrontEnd.MenuBuilder.sep;

public class UserManager {
    // todo - part of BackEnd.Users.Administrator?
    // todo - load/update users from DB before login
    // todo - change user type /promotion from-to/
    // TODO - MINIMIZE CALLS TO THE DB. Caching? If user is created from one computer but needs to be logged in from
    //  another? Or user is deleted..? Every machine will need to connect to the DB to make sure it has the latest information.
    private static List<User> activeUsers = new ArrayList<>();
    private static Map<UserType, List<User>> usersByType = new HashMap<>();

    public static User getTheLoginUserIfUsernameAndPasswordAreCorrect() {
        String[] creds = UserInput.getLoginUserAndPassword();

        boolean usernameAndPasswordCorrect = false;
        User currentUser = null;
        for (User user : getActiveUsers()) {
            if (user.getUsername().equalsIgnoreCase(creds[0]) && user.getPassword().equals(creds[1])) {
                usernameAndPasswordCorrect = true;
                currentUser = user;
                break;
            }
        }

        if (usernameAndPasswordCorrect) {
            // This will return the user - never null
            return currentUser;
        }

        printErrorMsgIfUserOrPasswordIsWrong(creds[0]);
        return null;
    }

    private static void printErrorMsgIfUserOrPasswordIsWrong(String username) {
        boolean usernameFound = false;
        for (User user : getActiveUsers()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                usernameFound = true;
                ConsolePrinter.printError("Wrong password for user [" + username + "]");
                break;
            }
        }
        if (!usernameFound) {
            ConsolePrinter.printError("User [" + username + "] doesn't exist.");
        }
    }

    public static int getActiveUserCount() {
        return getActiveUsers().size();
    }

    public static List<User> getActiveUsers() {
        return DBOperations.getUsers(10);
    }  // todo - remove limit ?

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

        DBOperations.addUser(user);

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

    public static boolean deleteUserName(String userName) {
        if (userName.equalsIgnoreCase("admin")) {
            // Don't allow deletion of the main admin account.
            ConsolePrinter.printError("The account cannot be deleted: [admin]");
            return false;
        } else {
            DBOperations.deleteUserByUsername(userName);
            return true;
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

    public static String[] getUsersArrayByType(UserType userType) {
        List<User> activeUsers = UserManager.getActiveUsers();
        String[] usersOfType = new String[getNumberOfUsersByType(userType)];
        int counter = 0;
        for (User user : activeUsers) {
            if (user.getUserType() == userType) {
                String userDataSingleString = (user.getUsername() + ", " + user.getFullName() + ", " + user.getId().toString());
                usersOfType[counter] = userDataSingleString;
                counter++;
            }
        }
        return usersOfType;
    }

    private static int getNumberOfUsersByType(UserType userType) {
        List<User> activeUsers = UserManager.getActiveUsers();
        int numberOfUsers = 0;
        for (User user : activeUsers) {
            if (user.getUserType() == userType) {
                numberOfUsers += 1;
            }
        }
        return numberOfUsers;
    }

    public static String[] getUserTypeNames() {
        UserType[] userTypes = UserType.values();
        String[] userTypeNames = new String[userTypes.length];

        // Populate the String array with enum names
        for (int i = 0; i < userTypes.length; i++) {
            userTypeNames[i] = userTypes[i].name();
        }
        return userTypeNames;
    }

    public static List<String> getAllUsersInformationByUserType(UserType userType, boolean withPassword, boolean addNumbers, int startingNumber) {
        List<String> usersInformation = new ArrayList<>();
        String userInfoToAdd;
        for (User user : getActiveUsers()) {
            if (user.getUserType() == userType) {
                if (addNumbers) {
                    userInfoToAdd = startingNumber + sep + getUserInformationString(withPassword, user);
                    startingNumber ++;
                } else {
                    userInfoToAdd = getUserInformationString(withPassword, user);
                }
                usersInformation.add(userInfoToAdd);
            }
        }
        return usersInformation;
    }

    public static String getUserInformationString(boolean withPassword, User user) {
        if (withPassword) {
            return user.getUsername() + sep + user.getFullName() + sep + user.getPassword();
        }
        return user.getUsername() + sep + user.getFullName();
    }

    public static List<String> getAllUsersInformationByUserType(UserType userType, boolean withPassword) {
        return getAllUsersInformationByUserType(userType, withPassword, false, 0);
    }

}

