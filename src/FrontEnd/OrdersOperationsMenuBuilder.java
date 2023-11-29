package FrontEnd;

import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import BackEnd.Restaurant.Order;
import BackEnd.Restaurant.OrderStatus;
import BackEnd.Restaurant.Restaurant;
import BackEnd.Restaurant.Table;
import BackEnd.Users.User;

import java.util.List;

import static FrontEnd.MenuBuilder.*;
import static FrontEnd.MenuBuilder.getDishNameFromIndex;
import static FrontEnd.RestaurantMenuBuilder.getAllThreeDishesMerged;
import static FrontEnd.RestaurantMenuBuilder.printAllRestaurantDishesWithNumbers;

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
            case 1 -> createNewOrder(new Restaurant(10));
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

    public static Order createNewOrder(Restaurant restaurant) {

        int selectedTable = tableSelection(restaurant);

        Table table = new Table(selectedTable);
        table.occupy();

        Order order = new Order();

        // print all dishes + prompt select dish to add to order + quantity ? (Enter for 1)
        // if quantity > 1 = for loop add to order
        // while loop - until 0 is pressed - add to order
        // Once order is

        while (true) {
            printAllRestaurantDishesWithNumbers("Done");

            List<String> allDishesCommaSeparated = getAllThreeDishesMerged();

            ConsolePrinter.printQuestion("Enter the index of the item that you wish to add: ");

            int selection = getUserInputFrom0toNumber(allDishesCommaSeparated.size());

            if (selection == 0) {
                break;
            }
            String dishName = getDishNameFromIndex(selection, allDishesCommaSeparated);
            Dish dish = RestaurantMenu.getDishFromDishName(dishName);
            order.addDish(dish);

            printCurrentOrder(order);
        }
        return order;
    }

    public static void printCurrentOrder(Order order) {
        List<Dish> orderedDishes = order.getOrderedDishes();
        for (Dish d : orderedDishes) {
            System.out.println("Ordered: " + d.getName() + " - " + d.getPrice());
        }
        System.out.println("Total: " + order.getTotalPrice());
    }

    public static void addDishToOrder() {

    }

    public static void printAllDishes() {
        RestaurantMenuBuilder.deleteItemFromRestaurantMenu();
    }

    public static int tableSelection(Restaurant restaurant) {
        int[] freeTables = Restaurant.getFreeTablesArr();
        String frameLabel = "Free tables"; // No frame label on the Login Menu page.
        String topMenuLabel = "Please enter a table number:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Free Table";

        // buildMenu(freeTables, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> tableSelection(option), null);
        // todo - create new printMenuAndGetUsersChoice kind of method that will only show available Table <Number>,
        //  no indexes and available options when selecting a table will be only the free tables

        int selectedTable = buildMenuOrder(freeTables, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
        return selectedTable;
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
