package FrontEnd;

import BackEnd.Restaurant.OrderStatus;
import BackEnd.Users.User;

import static FrontEnd.MenuBuilder.buildMenu;

public class OrdersOperationsMenuBuilder {
    // Всяка поръчка си има дата и час на създаване и номер на маса. Не може да се създаде повече от една поръчка за маса.
    //Към всяка поръчка може да се добавят или премахват ястия. Всяко ястие може да се добави веднъж или много пъти. Общата цена на поръчката се показва в реално време.
    // Сервитьорът може да смени статуса на поръчка на “платена”. Тогава му се показва обобщение на поръчката, тя изчезва от списъка с активни поръчки и на тази маса вече може да се прави нова поръчка.
    // Drink/Desert - cannot be cooked but still need to be ready for delivery? If new item is Food - wait for cook status before able to deliver?
    // todo - ready should only show orders with status "Cooking"
    public static void ordersMenu(User user) {
        String[] menuOptions = new String[]{"New order", "Show open orders", "Show completed orders",};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Restaurant Menu Options";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> ordersMenuOptions(option), user);  // lambda function to ignore the user.
    }

    public static void ordersMenuOptions(int option) {
        switch (option) {
            case 1 -> createNewOrder();
            case 2 -> showOpenOrders();
            case 3 -> showClosedOrders();
        }
    }

    public static void kitchenOrdersMenu(User user) {
        String[] menuOptions = new String[]{"Show open orders", "Set status to \"Cooking\"", "Set status to \"Ready\""};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Restaurant Menu Options";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> kitchenOrdersMenuOptions(option), user);  // lambda function to ignore the user.
    }

    public static void kitchenOrdersMenuOptions(int option) {
        // todo
//        switch (option) {
//            case 1 -> createNewOrder();
//            case 2 -> showOpenOrders();
//            case 3 -> showClosedOrders();
//        }
    }

    public static void createNewOrder() {
        // todo
    }

    public static void showOpenOrders() {
        // print all open orders - table/order number ? Add numbers infront - ask for order selection
        // go to viewSingleOpenOrder() - decide what to use - int number in menu/table number/option number ?
        // todo
    }

    public static void viewSingleOpenOrder() {
        // View occupied tables/orders - add option to view order
        // "Add item to order", "Remove item from order", "Set status: served"
        // todo
    }

    public static void showClosedOrders() {
        // todo
    }

    public static void changeOrderStatus(OrderStatus orderStatus) {
        // todo
    }
}
