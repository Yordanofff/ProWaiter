public class UserInterface {
    public static void printAppLogo() {
        System.out.println(
                "\n" +
                "\n" +
                " _____       __          __   _ _            \n" +
                "|  __ \\      \\ \\        / /  (_) |           \n" +
                "| |__) | __ __\\ \\  /\\  / /_ _ _| |_ ___ _ __ \n" +
                "|  ___/ '__/ _ \\ \\/  \\/ / _` | | __/ _ \\ '__|\n" +
                "| |   | | | (_) \\  /\\  / (_| | | ||  __/ |   \n" +
                "|_|   |_|  \\___/ \\/  \\/ \\__,_|_|\\__\\___|_|   \n" +
                "                                             ");
    }

    public static void startApp() {
        UserInterface.printAppLogo();

        int activeUsers = UserManager.getActiveUserCount();

        if (activeUsers != 0) {
            ConsolePrinter.printWarning("No users found. Creating the initial Admin account.");
            UserManager.addAdmin();
        } else {
            // print login menu
            printLoginMenu();
        }
    }

    public static void printMainMenu() {
        // todo
        System.out.println("...");
    }

    public static void printLoginMenu() {
        Menu.LoginMenu();
    }

}
