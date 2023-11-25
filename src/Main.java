import BackEnd.Users.UserManager;
import FrontEnd.UserInterface;

public class Main {
    // todo create abstract class for DB
    public static void main(String[] args) {
        runApp();
    }

    public static void testTable() {
        List<String> userDataToPrint = new ArrayList<>();
        String description = "Col1, Col2, Col3, Col4";
        userDataToPrint.add("Hello, this is, row1 - title, another cola");
        userDataToPrint.add("Hello, this is, , ");
        userDataToPrint.add("Hello2, , row2, ");

        MenuBuilder.printMenuOptionsInFrameNewTable(userDataToPrint, "Users", description);
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