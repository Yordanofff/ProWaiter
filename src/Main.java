import BackEnd.Users.UserManager;
import FrontEnd.UserInterface;

public class Main {
    public static void main(String[] args) {
        runApp();
    }

    public static void runApp() {
        System.out.println(UserManager.getActiveUsers());
        int activeUsers = UserManager.getActiveUserCount();  // todo - move getUsers in UserManager
//        System.out.println("Active now: " + activeUsers);
//
        UserInterface.startApp();
//
//        activeUsers = UserManager.getActiveUserCount();  // todo - move getUsers in UserManager
//        System.out.println("Active now: " + activeUsers);

    }
}