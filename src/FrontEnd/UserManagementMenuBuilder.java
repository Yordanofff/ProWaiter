package FrontEnd;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import BackEnd.Users.UserType;

import java.util.HashMap;
import java.util.List;

import static FrontEnd.MenuBuilder.*;
import static FrontEnd.UserInput.getUserInputFrom0toNumber;

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
        // TODO: use user somewhere or remove it.
        switch (option) {
            case 1 -> printAllUsersAlignedAndGetUserInput(true);
            case 2 -> addUserMenu();
            case 3 -> editUserMenu(true);
            case 4 -> deleteUserMenuSelectUserType();
        }
    }

    public static void printAllUsersAlignedWithNumbers(String columnNames, List<List<String>> allUsers) {
        List<String> allAdmins = allUsers.get(0);
        List<String> allWaiters = allUsers.get(1);
        List<String> allCooks = allUsers.get(2);

        int[] maxColumnLengths = getBiggest(allAdmins, allWaiters, allCooks, columnNames);

        printMenuOptionsInFrameTableRestaurantMenu(allAdmins, "ADMINS", columnNames, "", maxColumnLengths);
        printMenuOptionsInFrameTableRestaurantMenu(allWaiters, "WAITERS", "", "", maxColumnLengths);
        printMenuOptionsInFrameTableRestaurantMenu(allCooks, "COOKS", "", "Go Back", maxColumnLengths);
    }

    public static List<List<String>> getAllUsersNestedList(boolean printPassword) {
        List<String> allAdmins = UserManager.getAllUsersInformationByUserType(UserType.ADMIN, printPassword, true, 1);
        List<String> allWaiters = UserManager.getAllUsersInformationByUserType(UserType.WAITER, printPassword, true, allAdmins.size() + 1);
        List<String> allCooks = UserManager.getAllUsersInformationByUserType(UserType.COOK, printPassword, true, allAdmins.size() + allWaiters.size() + 1);

        return MenuBuilder.combineLists(allAdmins, allWaiters, allCooks);
    }

    public static void printAllUsersAlignedAndGetUserInput(List<List<String>> allUsers, boolean printPassword) {
        String columnNames = "Index, Username, Full Name";
        if (printPassword) {
            columnNames = "Index, Username, Full Name, Password";
        }

        printAllUsersAlignedWithNumbers(columnNames, allUsers);
    }

    public static void printAllUsersAlignedAndGetUserInput(boolean printPassword) {
        List<List<String>> allUsers = getAllUsersNestedList(printPassword);
        printAllUsersAlignedAndGetUserInput(allUsers, printPassword);
    }

    public static String getUsernameFromUserSelection(List<List<String>> allUsers) {
        List<String> allUsersString = getMergedListOfNestedStringLists(allUsers);
        ConsolePrinter.printQuestion("Select the index of the user that you wish to modify: ");
        int selection = getUserInputFrom0toNumber(allUsersString.size());

        if (selection == 0) {
            return null;
        }

        return getFirstElementFromIndex(selection, allUsersString);
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
        switch (option) {
            case 1 -> UserManager.addAdmin();
            case 2 -> UserManager.addWaiter();
            case 3 -> UserManager.addCook();
            default -> throw new RuntimeException("UserType not implemented.");
        }
    }

    public static void editUserMenu(boolean printPassword) {
        printAllUsersAlignedAndGetUserInput(printPassword);

        List<List<String>> allUsers = getAllUsersNestedList(printPassword);

        String selectedUsername = getUsernameFromUserSelection(allUsers);
        if (selectedUsername == null) {
            System.out.println("Going back..");
        } else {
            System.out.println("You selected: " + selectedUsername);
        }

        //  TODO: print another menu 1.2.3.4 - options what to change + UserInput prompt for that.
    }

    public static void deleteUserMenuSelectUserType() {
        String[] userTypeNames = UserManager.getUserTypeNames();

        String frameLabel = "Select User Type";
        String topMenuLabel = "Please choose the type of User that you want to delete:";
        String optionZeroText = "Cancel";
        String optionZeroMsg = "Canceled.";
        buildMenu(userTypeNames, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> deleteUserMenuAction(option), null);
    }

    public static void deleteUserMenuAction(int option) {
        switch (option) {
            case 1 -> deleteUserMenuPrintUsers(UserType.ADMIN);
            case 2 -> deleteUserMenuPrintUsers(UserType.WAITER);
            case 3 -> deleteUserMenuPrintUsers(UserType.COOK);
            default -> throw new RuntimeException("UserType not implemented.");
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
