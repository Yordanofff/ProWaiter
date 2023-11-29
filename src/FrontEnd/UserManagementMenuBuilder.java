package FrontEnd;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import BackEnd.Users.UserType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static FrontEnd.MenuBuilder.*;

public class UserManagementMenuBuilder {
    public static void UserManagementMenu(User user) {
        String[] menuOptions = new String[]{"View all users", "Add user", "Edit user", "Delete user"};
        String frameLabel = "[" + user.getUserType().toString() + "]";  // Allowing MANAGER class to be added later on and not having to change this.
        String topMenuLabel = "User Management";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back to main [" + user.getUserType() + "] menu...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, UserManagementMenuBuilder::UserManagementMenuAction);
    }

    public static void UserManagementMenuAction(int option, User user) {
        // todo - use user somewhere?
        switch (option) {
            case 1 -> printAllUsers();
            case 2 -> addUserMenu();
            case 3 -> editUserMenu();
            case 4 -> deleteUserMenuSelectUserType();
        }
    }

    public static void printAllUsers() {
        List<String> userDataToPrint;
        List<User> activeUsers = UserManager.getActiveUsers();

        String columnNames = "Username, Full Name, Id";
        for (UserType userType : UserType.values()) {

            // Empty the users list for each new type
            userDataToPrint = new ArrayList<>();

            for (User user : activeUsers) {
                if (user.getUserType() == userType) {
                    String userDataSingleString = (user.getUsername() + sep + user.getFullName() + sep + user.getId().toString());
                    userDataToPrint.add(userDataSingleString);
                }
            }
            MenuBuilder.printMenuOptionsInFrameTable(userDataToPrint, userType.toString(), columnNames);
            System.out.println();  // Space below
        }
    }

    public static void addUserMenu() {
        String[] userTypeNames = UserManager.getUserTypeNames();

        String frameLabel = "Select User Type";  // No frame label on the Login Menu page.
        String topMenuLabel = "Please choose the type of User that you want to create:";
        String optionZeroText = "Cancel";
        String optionZeroMsg = "Canceled.";
        buildMenu(userTypeNames, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> addUserMenuAction(option), null);
    }

    public static void addUserMenuAction(int option) {
        // todo - any better/clever way to do this?
        // Will need to add new UserTypes to this list if new types
        switch (option) {
            case 1 -> UserManager.addAdmin();
            case 2 -> UserManager.addWaiter();
            case 3 -> UserManager.addCook();
            default ->
                    ConsolePrinter.printError("UserType not implemented. Add UserType in MenuBuilder/addUserMenuAction");
        }
    }

    public static void editUserMenu() {
        System.out.println("Not implemented");
        // todo - print all users - select user.. then
        //  print user details - print another menu 1.2.3.4 - options what to change + UserInput prompt for that.
    }

    public static void deleteUserMenuSelectUserType() {
        String[] userTypeNames = UserManager.getUserTypeNames();

        String frameLabel = "Select User Type";  // No frame label on the Login Menu page.
        String topMenuLabel = "Please choose the type of User that you want to delete:";
        String optionZeroText = "Cancel";
        String optionZeroMsg = "Canceled.";
        buildMenu(userTypeNames, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> deleteUserMenuAction(option), null);
    }

    public static void deleteUserMenuAction(int option) {
        // todo - any better/clever way to do this?
        // Will need to add new UserTypes to this list if new types
        switch (option) {
            case 1 -> deleteUserMenuPrintUsers(UserType.ADMIN);
            case 2 -> deleteUserMenuPrintUsers(UserType.WAITER);
            case 3 -> deleteUserMenuPrintUsers(UserType.COOK);
            default ->
                    ConsolePrinter.printError("UserType not implemented. Add UserType in MenuBuilder/deleteUserMenuAction");
        }
    }

    private static void deleteUserMenuPrintUsers(UserType userType) {
        String[] usersArrayByType = UserManager.getUsersArrayByType(userType);
        int numberOfUsers = usersArrayByType.length;
        HashMap<Integer, String> usersArrayByTypeNumbered = mapNumbersToItems(usersArrayByType, 1);

        List<String> userDataToPrint = createListWithCommaSeparatedValues(usersArrayByTypeNumbered);

        String columnNames = "Index, Username, Full Name, Id";
        MenuBuilder.printMenuOptionsInFrameTable(userDataToPrint, userType.toString(), columnNames, "Cancel");

        ConsolePrinter.printQuestion("Enter the [index] of the user you want to delete: ");
        int selection = getUserInputFrom0toNumber(numberOfUsers);

        // Exit if 0
        if (selection == 0) {
            System.out.println("Canceled.");
            return;
        }

        int columnNumberWithUsername = 0;  // the username is the first column in the String part of the hashmap.
        String selectedUserName = getStringForInteger(usersArrayByTypeNumbered, selection, columnNumberWithUsername); // username is unique

        boolean confirmed = UserInput.getConfirmation("Are you sure you want to delete user [" + selectedUserName + "]");
        if (confirmed) {
            if (UserManager.deleteUserName(selectedUserName)) {
                System.out.println("Username deleted: " + selectedUserName);
            }
        } else {
            System.out.println("Cancelling..");
        }
    }
}
