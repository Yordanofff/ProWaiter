package FrontEnd;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import BackEnd.Users.UserType;

import java.util.*;

public class MenuBuilder {
    private static final String sep = ",";  // separator for the strings when printing menus
    static final int MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU = 5;
    static final String MENU_SEPARATOR = " - ";  // With spaces if required.

    static final String midCross = "┼";
    static final String topCross = "┬";
    static final String bottomCross = "┴";
    static final String topLeftCorner = "┌";
    static final String bottomLeftCorner = "└";
    static final String midLeft = "├";
    static final String topRightCorner = "┐";
    static final String bottomRightCorner = "┘";
    static final String midRight = "┤";
    static final String topBottom = "─";
    static final String SideWall = "│";
    static Scanner scanner = new Scanner(System.in);

    public static void LoginMenu() {
        // todo - if enough time - add another option to change the color of the menu.
        String[] menuOptions = new String[]{"Login", "About"};
        String frameLabel = "";  // No frame label on the Login Menu page.
        String topMenuLabel = "Please Login with your credentials:";
        String optionZeroText = "Exit";
        String optionZeroMsg = "GoodBye!";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> LoginMenuAction(option), null);
    }

    public static void LoginMenuAction(int option) {
        // Call methods to run
        switch (option) {
            case 1 -> UserLoginMenuAction();
            case 2 -> aboutPage();
        }
    }
    private static void aboutPage() {
        ConsolePrinter.printQuestion("\nHello, we are [Ivaylo Yordanov] and [Ivaylo Staykov] and we love coding.\n\nWe really hope that you'll like our project.\n");
    }

    public static void UserLoginMenuAction() {
        User user = UserManager.getTheLoginUserIfUsernameAndPasswordAreCorrect();
        if (user != null) {
            UserType userType = user.getUserType();
            System.out.println("Opening [" + userType + "] panel...");
            if (userType == UserType.ADMIN) {
                AdminMenu(user);
            } else if (userType == UserType.COOK) {
                KitchenMenu(user);
            } else if (userType == UserType.WAITER) {
                WaiterMenu(user);
            }
        }
    }

    public static void AdminMenu(User user) {
        String[] menuOptions = new String[]{"User Management", "Menu Management", "Order management"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, MenuBuilder::AdminMenuAction, user);
    }

    public static void AdminMenuAction(int option, User user) {
        // Call methods to run
        switch (option) {
            case 1 -> UserManagementMenu(user);
            case 2 -> System.out.println("Menu Management"); // todo
            case 3 -> System.out.println("Order management");  // todo
        }
    }

    public static void UserManagementMenu(User user) {
        String[] menuOptions = new String[]{"View all users", "Add user", "Edit user", "Delete user"};
        String frameLabel = "[" + user.getUserType().toString() + "]";  // Allowing MANAGER class to be added later on and not having to change this.
        String topMenuLabel = "User Management";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back to main [" + user.getUserType() + "] menu...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, MenuBuilder::UserManagementMenuAction);
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

    public static void editUserMenu() {
        // todo - print all users - select user.. then
        //  print user details - print another menu 1.2.3.4 - options what to change + UserInput prompt for that.
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

    public static void deleteUserMenuSelectUserType() {
        String[] userTypeNames = getUserTypeNames();

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

    private static String getStringForInteger(HashMap<Integer, String> map, int key, int columnToReturn) {
        String data = map.get(key);
        String[] columns = data.split(sep);
        return columns[columnToReturn];
    }

    private static List<String> createListWithCommaSeparatedValues(Map<Integer, String> map) {
        List<String> resultList = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            String commaSeparatedValue = entry.getKey() + sep + entry.getValue();
            resultList.add(commaSeparatedValue);
        }

        return resultList;
    }

    private static HashMap<Integer, String> mapNumbersToItems(String[] items, int startingNumber) {
        // Using LinkedHashMap to keep the order as they were added.
        HashMap<Integer, String> itemsWithNumbers = new LinkedHashMap<>();

        for (int i = startingNumber; i < items.length + startingNumber; i++) {
            itemsWithNumbers.put(i, items[i - 1]);
        }

        return itemsWithNumbers;
    }

    public static void addUserMenu() {
        String[] userTypeNames = getUserTypeNames();

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

    private static String[] getUserTypeNames() {
        UserType[] userTypes = UserType.values();
        String[] userTypeNames = new String[userTypes.length];

        // Populate the String array with enum names
        for (int i = 0; i < userTypes.length; i++) {
            userTypeNames[i] = userTypes[i].name();
        }
        return userTypeNames;
    }

    public static void KitchenMenu(User user) {
        // todo - ready should only show orders with status "Cooking"
        String[] menuOptions = new String[]{"Show orders", "Set status: cooking", "Set status: ready"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, MenuBuilder::KitchenMenuAction);
    }

    public static void KitchenMenuAction(int option, User user) {
        // todo - use user somewhere?
        switch (option) {
            case 1 -> System.out.println("Showing orders..");
            case 2 -> System.out.println("Showing orders.. + user input - select order - status cooking");
            case 3 -> System.out.println("Showing orders.. + user input - select order - status ready");
        }
    }

    public static void WaiterMenu(User user) {
        // todo - ready should only show orders with status "Cooking"
        String[] menuOptions = new String[]{"Show orders", "Show ready orders", "Add order", "Add to order", "Remove from order", "Set status: served"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Hello, " + user.getFullName() + "!";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
//        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, MenuBuilder::WaiterMenuAction); // use this if user data is needed in WaiterMenuAction
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> WaiterMenuAction(option), user);  // lambda function to ignore the user.

    }

    public static void WaiterMenuAction(int option) {
        switch (option) {
            case 1 -> System.out.println(".. waiter option 1");
            case 2 -> System.out.println(".. waiter option 2");
        }
    }

    public static void buildMenu(String[] menuOptions, String topMenuLabel, String optionZeroText, String optionZeroMsg, String frameLabel, MenuAction menuAction, User user) {
        while (true) {
            int selectedOption = printMenuAndGetUsersChoice(menuOptions, topMenuLabel, optionZeroText, frameLabel);

            // Exit if 0
            if (selectedOption == 0) {
                System.out.println(optionZeroMsg);
                break;
            }

            // Executing the selected option
            menuAction.execute(selectedOption, user);

            // pause
            System.out.print("Press any key to continue..: ");
            scanner.nextLine();

            System.out.println();
        }
    }

    public static void buildMenu(String[] menuOptions, String topMenuLabel, String optionZeroText, String optionZeroMsg, String frameLabel, MenuAction menuAction) {
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, menuAction, null);
    }

    private static int printMenuAndGetUsersChoice(String[] menuOptions, String topMenuQuestion, String optionZeroText, String frameLabel) {
        // Creating HashMap with numbers and options. + Adding Exit/Logout/Go Back
        HashMap<Integer, String> menuOptionsWithNumbers = generateHashMapMenuOptionsWithNumbers(menuOptions, optionZeroText);

        // Fixing spaces before the numbers, adding separator and storing all in a list.
        List<String> dataToPrint = getMenuOptionsWithSameLengthOfMaxDigitLength(menuOptionsWithNumbers);

        // Printing the options in a frame
        printMenuOptionsInFrame(topMenuQuestion, dataToPrint, frameLabel);

        // Returning the user selection
        return getUserInputFrom0toNumber(menuOptions.length);
    }

    private static int getUserInputFrom0toNumber(int numOptions) {
        int choice;

        while (true) {
            String ans = scanner.nextLine().strip();

            try {
                choice = Integer.parseInt(ans);

                if (choice >= 0 && choice <= numOptions) {
                    break;
                } else {
                    ConsolePrinter.printError("Please enter a number between [0 - " + numOptions + "]");
                }
            } catch (NumberFormatException e) {
                ConsolePrinter.printError("Invalid input [" + ans + "]! " +
                        "Please enter an integer in the range [0 - " + numOptions + "]");
            }
        }

        return choice;
    }

    private static HashMap<Integer, String> generateHashMapMenuOptionsWithNumbers(String[] menuOptionsWithoutExit, String optionZeroText) {
        // Will add Exit as position 0.

        // Using LinkedHashMap to keep the order as they were added.
        HashMap<Integer, String> menuOptionsWithNumbers = new LinkedHashMap<>();
        menuOptionsWithNumbers.put(0, optionZeroText);  // Exit/LogOut/Go Back

        for (int i = 1; i <= menuOptionsWithoutExit.length; i++) {
            menuOptionsWithNumbers.put(i, menuOptionsWithoutExit[i - 1]);
        }

        return menuOptionsWithNumbers;
    }

    /**
     * This method will add trailing spaces in front of the numbers so that they will be printed one below another.
     * Also adds separator between the number and the option name.
     * " 7 - Option ..."
     * " 8 - Option ..."
     * " 9 - Option ..."
     * "10 - Option ..."
     * "11 - Option ..."
     *
     * @param hashMapMenuOptionsWithNumbers - HashMap of (number, option name)
     * @return List<String> with all options
     */
    private static List<String> getMenuOptionsWithSameLengthOfMaxDigitLength(HashMap<Integer, String> hashMapMenuOptionsWithNumbers) {
        List<String> dataToPrint = new ArrayList<>();

        int longestKey = getNumberOfSymbolsInSetOfIntegers(hashMapMenuOptionsWithNumbers.keySet());
        String keyToString;
        for (int key : hashMapMenuOptionsWithNumbers.keySet()) {
            String value = String.valueOf(hashMapMenuOptionsWithNumbers.get(key));

            // Convert int to String, to be able to add trailing spaces.
            keyToString = String.valueOf(key);
            // Format string doesn't work because will be printed normally not pritf...
            keyToString = addTrailingSpacesBeginningOfString(keyToString, longestKey);

            dataToPrint.add(keyToString + ConsolePrinter.getGreenMsg(MENU_SEPARATOR) + value);
        }

        return dataToPrint;
    }

    /**
     * |      0 - Exit                                   |
     * |      1 - Login                                  |
     *
     * @param frameLength - longest row data + min spaces on each side + 2
     * @param rowData     - the actual data that needs to be printed "0 - Exit"
     */
    private static void printMiddleMenuLine(int frameLength, String rowData, String sideSymbol, int numberOfSymbolsFromLeftWall) {
        String coloredFrameAndSpacesBeginningOfRow = ConsolePrinter.getGreenMsg(sideSymbol + " ".repeat(numberOfSymbolsFromLeftWall));
        System.out.print(coloredFrameAndSpacesBeginningOfRow + rowData);
        if (numberOfSymbolsFromLeftWall == MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU) {
            System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + sideSymbol));
        } else {
            int diff = MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU - numberOfSymbolsFromLeftWall;
            frameLength = frameLength + diff;  // add more spaces when printing the frame so that the right wall is in the correct place.
            System.out.println(ConsolePrinter.getGreenMsg(" ".repeat(getNumberOfRemainingSpacesToTheEndOfTheFrame(frameLength, rowData)) + sideSymbol));
        }
    }

    private static void printMiddleMenuLine(int frameLength, String rowData, String sideSymbol) {
        printMiddleMenuLine(frameLength, rowData, sideSymbol, MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU);
    }

    private static int getNumberOfRemainingSpacesToTheEndOfTheFrame(int frameLength, String rowData) {
        return (frameLength - (getStringLengthWithoutANSI(rowData) + MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU + 2));
    }

    private static int getTheNumberOfSymbolsInTheLongestString(String str, List<String> listOfStrings) {
        // Create a new list that combines the current list and the string and get the length of the longest one.
        List<String> menuQuestionAndOptions = new ArrayList<>(listOfStrings);
        menuQuestionAndOptions.add(str);
        return getLengthOfTheLongestStringInList(menuQuestionAndOptions);
    }

    private static String addTrailingSpacesBeginningOfString(String str, int desiredLength) {
        return " ".repeat(desiredLength - str.length()) + str;
    }

    private static int getNumberOfSymbolsInSetOfIntegers(Set<Integer> set) {
        int longest = 0;
        for (int num : set) {
            String word = String.valueOf(num);
            if (word.length() > longest) {
                longest = word.length();
            }
        }
        return longest;
    }

    private static int getLengthOfTheLongestStringInList(List<String> list) {
        int longest = 0;
        for (String row : list) {
            int currentLength = getStringLengthWithoutANSI(row);
            if (currentLength > longest) {
                longest = currentLength;
            }
        }
        return longest;
    }

    private static int getStringLengthWithoutANSI(String str) {
        return str.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    /**
     * ┌────────── [ADMIN] ───────────┐
     * │     Hello, Ivo Yordanov!     │
     * ├──────────────────────────────┤
     * │     1 - User Management      │
     * │     2 - Menu Management      │
     * │     3 - Order management     │
     * ├──────────────────────────────┤
     * │     0 - Log out              │
     * └──────────────────────────────┘
     *
     * @param menuTopQuestion "Hello, Ivo Yordanov!"
     * @param menuOptions     - List of Strings with all Options: ["0 - Log out", "1 - User Management", ..]
     * @param frameLabel      - [ADMIN]
     */
    public static void printMenuOptionsInFrame(String menuTopQuestion, List<String> menuOptions, String frameLabel) {

        int longestRowWithData = getTheNumberOfSymbolsInTheLongestString(menuTopQuestion, menuOptions);

        // Add the minimum spaces on both sides of the string + 2 symbols for the frame (left + right side)
        int frameLength = longestRowWithData + (MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU * 2) + 2;

        System.out.println(getTopLineOfMenu(frameLength, frameLabel));
        printMiddleMenuLine(frameLength, menuTopQuestion, SideWall);
        System.out.println(getMidLine(frameLength));

        // The first element will be [0 - Exit] or [0 - Log Out] etc.. Don't print it in the top part.
        boolean isZeroElement = true;
        for (String menuOptionRow : menuOptions) {
            if (isZeroElement) {
                isZeroElement = false;
                continue;
            }
            printMiddleMenuLine(frameLength, menuOptionRow, SideWall);
        }

        System.out.println(getMidLine(frameLength));

        // Print the [0 - Exit] or [0 - Log Out] - at the bottom of the list below another separator
        printMiddleMenuLine(frameLength, menuOptions.get(0), SideWall);

        System.out.println(getBottomLine(frameLength));
    }

    public static void printMenuOptionsInFrameTable(List<String> rowsWithCommaSeparatedColumns, String frameLabel, String columnNames, String zeroOptionText) {

        int numSpacesAroundEachColumnWord = 2;
        int[] maxColumnLengths = getBiggestColumnNames(rowsWithCommaSeparatedColumns, columnNames);
        int numberOfColumns = getMaxNumberOfColumns(maxColumnLengths, columnNames);

        // This sums up the longest word in each column
        int maxNumberOfSymbolsAllRows = getMaxNumberOfSymbolsAllRows(maxColumnLengths);

        int numAddedSpaces = numberOfColumns * 2 * numSpacesAroundEachColumnWord;

        // top frame len = maxColumnLength + (numberOfColumns +1) (1 symbol each column + sides) + numAddedSpaces
        int frameLength = maxNumberOfSymbolsAllRows + numAddedSpaces + numberOfColumns + 1;

        System.out.println(getTopLineOfMenu(frameLength, frameLabel));
        System.out.println(getTopLineTableEndingUpDown(frameLength, maxColumnLengths));
        columnNames = addExtraSeparatorsToLength(columnNames, numberOfColumns);
        printMiddleMenuLineTable(columnNames, maxColumnLengths, numSpacesAroundEachColumnWord);
        System.out.println(getMidLineTable(frameLength, maxColumnLengths));

        for (String row : rowsWithCommaSeparatedColumns) {
            row = addExtraSeparatorsToLength(row, numberOfColumns);
            printMiddleMenuLineTable(row, maxColumnLengths, numSpacesAroundEachColumnWord);
        }
        if (zeroOptionText.isEmpty()) {
            System.out.println(getBottomLineTable(frameLength, maxColumnLengths));
        } else {
            System.out.println(getBottomLineTableContinuingDownCorners(frameLength, maxColumnLengths));
            zeroOptionText = "0 - " + zeroOptionText;
            printMiddleMenuLine(frameLength, zeroOptionText, SideWall, numSpacesAroundEachColumnWord);
            System.out.println(getBottomLine(frameLength));
        }
    }

    public static void printMenuOptionsInFrameTable(List<String> rowsWithCommaSeparatedColumns, String frameLabel, String columnNames) {
        printMenuOptionsInFrameTable(rowsWithCommaSeparatedColumns, frameLabel, columnNames, "");
    }

    private static String addExtraSeparatorsToLength(String columnNames, int numberOfColumns) {
        // Add extra , to the columns so that it prints walls on right side if columns are more than column names.
        if (numberOfColumns > columnNames.split(sep).length) {
            int diff = numberOfColumns - columnNames.split(sep).length;
            columnNames += ", ".repeat(diff);
        }
        return columnNames;
    }

    private static int getMaxNumberOfColumns(int[] maxColumnLengths, String columnNames) {
        int numberOfColumns = maxColumnLengths.length;
        if (getNumberOfColumns(columnNames) > numberOfColumns) {
            numberOfColumns = getNumberOfColumns(columnNames);
        }
        return numberOfColumns;
    }

    private static int[] getBiggestColumnNames(List<String> rowsWithCommaSeparatedColumns, String columnNames) {
        int[] maxColumnLengths = getMaxColumnLengths(rowsWithCommaSeparatedColumns);
        // Get the length of the elements in the columnNames
        int[] maxColumnLengthDescription = getMaxColumnLength(columnNames);
        maxColumnLengths = getBiggest(maxColumnLengths, maxColumnLengthDescription);
        return maxColumnLengths;
    }

    private static int[] getBiggest(int[] array1, int[] array2) {
        int maxLength = Math.max(array1.length, array2.length);
        int[] result = new int[maxLength];

        for (int i = 0; i < maxLength; i++) {
            int value1 = (i < array1.length) ? array1[i] : 0; // Use 0 if array1 is shorter
            int value2 = (i < array2.length) ? array2[i] : 0; // Use 0 if array2 is shorter

            result[i] = Math.max(value1, value2);
        }

        return result;
    }

    private static int[] getLastXElements(int[] sourceArray, int x) {
        // Ensure x is not greater than the length of the source array
        x = Math.min(x, sourceArray.length);

        int[] resultArray = new int[x];

        // Copy the last x elements from the source array to the result array
        for (int i = 0; i < x; i++) {
            resultArray[i] = sourceArray[sourceArray.length - x + i];
        }

        return resultArray;
    }

    // │  column 1  │  column 2  │  column 3  │  column 4  │
    private static void printMiddleMenuLineTable(String row, int[] maxColumnLengths, int numSpacesAroundEachColumnWord) {

        // Green frame, white letters like everywhere.
        String[] elements = getRowElementsTrimmed(row);
        int elementsLength = elements.length;

        StringBuilder toPrint = new StringBuilder("");

        for (int i = 0; i < elementsLength; i++) {
            String currentElement = elements[i];

            int maxLengthCurrentPosition = maxColumnLengths[i];
            int count = maxLengthCurrentPosition - currentElement.length() + numSpacesAroundEachColumnWord;
            toPrint.append(ConsolePrinter.getGreenMsg(SideWall) + " ".repeat(numSpacesAroundEachColumnWord) +
                    currentElement + " ".repeat(count));

            // Add end of frame symbol
            if (i == elementsLength - 1) {
                toPrint.append(ConsolePrinter.getGreenMsg(SideWall));
            }
        }
        System.out.println(toPrint);
    }

    private static void printElementsTable(List<String> myList, int[] maxColumnLengths) {
        int numSpacesAroundEachColumnWord = 2;
        int maxNumberOfSymbolsAllRows = getMaxNumberOfSymbolsAllRows(maxColumnLengths);
        int numberOfColumns = maxColumnLengths.length; // todo
        int numAddedSpaces = numberOfColumns * 2 * numSpacesAroundEachColumnWord;
        // top frame len = maxColumnLength + (numberOfColumns +1) (1 symbol each column + sides) + numAddedSpaces
        int frameLength = maxNumberOfSymbolsAllRows + numAddedSpaces + numberOfColumns + 1;
        System.out.println("_".repeat(frameLength));
        for (String row : myList) {
            String[] elements = getRowElementsTrimmed(row);

            for (int i = 0; i < elements.length; i++) {
                String currentElement = elements[i];
                int maxLengthCurrentPosition = maxColumnLengths[i];

                System.out.print("|" + " ".repeat(numSpacesAroundEachColumnWord) + currentElement +
                        " ".repeat(maxLengthCurrentPosition - currentElement.length() + numSpacesAroundEachColumnWord));
                if (i == elements.length - 1) {
                    System.out.println("|");
                }
            }
        }
        System.out.println("_".repeat(frameLength));
    }

    private static String getTopLineOfMenuTable(int length, int[] maxColumnLengths) {
        return getGreenLineTable(length, topLeftCorner, topRightCorner, topCross, maxColumnLengths);
    }

    // ┌────────── [ADMIN] ───────────┐
    private static String getTopLineOfMenu(int length, String label) {
        int numDashesEachSide = length - label.length() - 2 - 2; // 2 for the spaces, 2 for the corners
        if (label.isEmpty()) {
            return getGreenLine(length, topLeftCorner, topRightCorner);
        }
        String dashesBefore = topBottom.repeat(numDashesEachSide / 2);
        String dashesAfter = topBottom.repeat(numDashesEachSide / 2);
        if ((label.length() % 2 == 1 && length % 2 == 0) || (label.length() % 2 == 0 && length % 2 == 1)) {
            dashesAfter = topBottom.repeat((numDashesEachSide / 2) + 1);
        }
        return ConsolePrinter.getGreenMsg(topLeftCorner + dashesBefore + " ")
                + label + ConsolePrinter.getGreenMsg(" " + dashesAfter + topRightCorner);
    }

    // ┌──────────────────────────────┐
    private static String getTopLineOfMenu(int length) {
        return getTopLineOfMenu(length, "");
    }

    private static String getGreenLine(int length, String mostLeftSymbol, String mostRightSymbol) {
        return ConsolePrinter.getGreenMsg(mostLeftSymbol + topBottom.repeat(length - 2) + mostRightSymbol);
    }

    // ├────────────────────────┬───────────┬────────────────┬────────────────┤
    private static String getTopLineTableEndingUpDown(int length, int[] maxColumnLengths) {
        return getGreenLineTable(length, midLeft, midRight, topCross, maxColumnLengths);
    }

    private static String getGreenLineTable(int length, String mostLeftSymbol, String mostRightSymbol, String crossSymbol, int[] maxColumnLengths) {
        String toReturn = "";
        toReturn += mostLeftSymbol;
        for (int i = 0; i < maxColumnLengths.length; i++) {
            int numBottomSymbols = maxColumnLengths[i] + MIN_NUMBER_OF_SPACES_ON_EACH_SIDE_OF_MENU - 1;
            toReturn += topBottom.repeat(numBottomSymbols);
            if (i != maxColumnLengths.length - 1) {
                toReturn += crossSymbol;
            }
        }
        toReturn += mostRightSymbol;
        return ConsolePrinter.getGreenMsg(toReturn);
    }

    // └──────────┴───────────┴────────────────┴────────────────┘
    private static String getBottomLineTable(int length, int[] maxColumnLengths) {
        return getGreenLineTable(length, bottomLeftCorner, bottomRightCorner, bottomCross, maxColumnLengths);
    }

    // ├──────────┴───────────┴────────────────┴────────────────┤
    private static String getBottomLineTableContinuingDownCorners(int length, int[] maxColumnLengths) {
        return getGreenLineTable(length, midLeft, midRight, bottomCross, maxColumnLengths);
    }

    // ├──────────┼───────────┼────────────────┼────────────────┤
    private static String getMidLineTable(int length, int[] maxColumnLengths) {
        return getGreenLineTable(length, midLeft, midRight, midCross, maxColumnLengths);
    }

    // ├──────────────────────────────┤
    private static String getMidLine(int length) {
        return getGreenLine(length, midLeft, midRight);
    }

    // └──────────────────────────────┘
    private static String getBottomLine(int length) {
        return getGreenLine(length, bottomLeftCorner, bottomRightCorner);
    }

    private static int getMaxNumberOfSymbolsAllRows(int[] maxColumnLengths) {
        int n = 0;
        for (int el : maxColumnLengths) {
            n += el;
        }
        return n;
    }

    private static int[] getMaxColumnLengths(List<String> myList) {
        int numberOfColumns = getNumberOfColumns(myList);
        int[] elementsLength = new int[numberOfColumns];
        for (String row : myList) {

            String[] elements = getRowElementsTrimmed(row);

            for (int i = 0; i < elements.length; i++) {
                int currentElementLength = elements[i].length();
                if (elementsLength[i] < currentElementLength) {
                    elementsLength[i] = currentElementLength;
                }
            }
        }
        return elementsLength;
    }

    private static int[] getMaxColumnLength(String row) {
        int numberOfColumns = getNumberOfColumns(row);
        int[] elementsLength = new int[numberOfColumns];

        String[] elements = getRowElementsTrimmed(row);

        for (int i = 0; i < elements.length; i++) {
            int currentElementLength = elements[i].length();
            if (elementsLength[i] < currentElementLength) {
                elementsLength[i] = currentElementLength;
            }
        }
        return elementsLength;
    }

    private static String[] getRowElementsTrimmed(String row) {
        // Create elements - trimmed - single row
        String[] elements = row.split(sep);
        for (int i = 0; i < elements.length; i++) {
            elements[i] = elements[i].trim();
        }
        return elements;
    }

    private static int getNumberOfColumns(List<String> myList) {
        if (myList.isEmpty()) {
            return 0;
        }

        String[] firstRow = myList.get(0).split(sep);
        return firstRow.length;
    }

    private static int getNumberOfColumns(String myString) {
        String[] row = myString.split(sep);
        return row.length;
    }
}