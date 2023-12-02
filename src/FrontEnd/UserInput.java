package FrontEnd;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;

import java.util.List;
import java.util.Scanner;

public class UserInput {
    static Scanner scanner = new Scanner(System.in);

    public static String[] getLoginUserAndPassword() {
        String[] userAndPassword = new String[2];
        String userName = getUsername(false);
        String password = getPassword(userName, false);
        userAndPassword[0] = userName;
        userAndPassword[1] = password;
        return userAndPassword;
    }

    public static String getUserInput(String question) {
        ConsolePrinter.printQuestion(question);
        return scanner.nextLine();
    }

    public static double getDoubleInput(String question) {
        double result = 0;
        boolean validInput = false;
        String input = null;

        while (!validInput) {
            try {
                ConsolePrinter.printQuestion(question);
                input = scanner.nextLine();

                // Attempt to parse the input as a double
                result = Double.parseDouble(input);
                validInput = true;
            } catch (NumberFormatException e) {
                ConsolePrinter.printError("Invalid input [" + input + "]. Please enter a valid double or integer.");
            }
        }

        return result;
    }

    public static int getIntInput(String question) {
        int result = 0;
        boolean validInput = false;
        String input = null;

        while (!validInput) {
            try {
                ConsolePrinter.printQuestion(question);
                input = scanner.nextLine();

                // Attempt to parse the input as an integer
                result = Integer.parseInt(input);
                validInput = true;
            } catch (NumberFormatException e) {
                ConsolePrinter.printError("Invalid input [" + input + "]. Please enter a valid integer.");
            }
        }

        return result;
    }

    public static String getUsername(boolean isCreatingNewUser) {
        String username;
        while (true) {
            String question = "Please enter username: ";
            String userInput = getUserInput(question);

            if (!isCreatingNewUser) {
                // No checks when user is trying to log in.
                username = userInput;
                break;
            }

            // Re-run the loop if the username already exists.
            if (isUserNameAlreadyInDB(userInput)) {
                ConsolePrinter.printError("Username [" + userInput + "] already exist!");
                continue;
            }

            if (Validators.isValidUsername(userInput, User.MINIMUM_USERNAME_LENGTH)) {
                username = userInput;
                if (Validators.isCapitalLetterInString(username)) {
                    ConsolePrinter.printWarning("The username [" + username + "] will be converted to lowercase letters only");
                }
                break;
            } else {
                System.out.println("Try again.");
            }
        }
        return username.toLowerCase();
    }

    public static String getUsername() {
        return getUsername(true);
    }

    private static boolean isUserNameAlreadyInDB(String userName) {
        List<User> activeUsers = UserManager.getActiveUsers();
        for (User user : activeUsers) {
            if (user.getUsername().equalsIgnoreCase(userName)) {
                return true;
            }
        }
        return false;
    }

    public static String getPassword(String username, boolean validatePassword) {
        String password;
        while (true) {
            String question = "Please enter password for user [" + username + "]: ";
            String userInput = getUserInput(question);

            // Password validation not needed when the user is trying to log in.
            if (!validatePassword) {
                password = userInput;
                break;
            }

            if (Validators.isPasswordValid(userInput, User.MINIMUM_USERNAME_PASSWORD_LENGTH)) {
                password = userInput;
                break;
            } else {
                System.out.println("Try again.");
            }
        }
        return password;
    }

    public static String getPassword(String username) {
        return getPassword(username, true);
    }

    public static String getFirstName() {
        return getName("first");
    }

    public static String getLastName() {
        return getName("last");
    }

    private static String getName(String firstOrLastName) {
        String nameToReturn;
        while (true) {
            String question = "Please enter your " + firstOrLastName + " name: ";
            String userInput = getUserInput(question);

            if (Validators.isValidName(userInput)) {
                nameToReturn = userInput;
                if (!Validators.isNameWrittenCorrectly(nameToReturn)) {
                    String fixedCapitalizationName = UserInput.capitalize(nameToReturn);
                    ConsolePrinter.printWarning("Incorrect capitalization of " + firstOrLastName +
                            " name [" + nameToReturn + "]. Will be renamed to [" + fixedCapitalizationName + "].");
                    nameToReturn = fixedCapitalizationName;
                }
                break;
            } else {
                System.out.println("Try again.");
            }
        }
        return nameToReturn;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static boolean getConfirmation(String message) {
        ConsolePrinter.printQuestion(message + " (yes/no): ");

        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("yes");
    }
}
