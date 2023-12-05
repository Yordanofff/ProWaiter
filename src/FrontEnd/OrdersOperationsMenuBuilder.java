package FrontEnd;

import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import BackEnd.Restaurant.Order;
import BackEnd.Restaurant.OrderStatus;
import BackEnd.Restaurant.Restaurant;
import BackEnd.Restaurant.Table;
import BackEnd.Users.User;

import java.util.List;

import static FrontEnd.MenuBuilder.*;
import static FrontEnd.RestaurantMenuBuilder.*;

public class OrdersOperationsMenuBuilder {
    // Всяка поръчка си има дата и час на създаване и номер на маса. Не може да се създаде повече от една поръчка за маса.
    //Към всяка поръчка може да се добавят или премахват ястия. Всяко ястие може да се добави веднъж или много пъти. Общата цена на поръчката се показва в реално време.
    // Сервитьорът може да смени статуса на поръчка на “платена”. Тогава му се показва обобщение на поръчката, тя изчезва от списъка с активни поръчки и на тази маса вече може да се прави нова поръчка.
    // Drink/Desert - cannot be cooked but still need to be ready for delivery? If new item is Food - wait for cook status before able to deliver?
    // todo - ready should only show orders with status "Cooking"
    public static void ordersMenu(User user) {
        String[] menuOptions = new String[]{"New order", "Show open orders", "Show completed orders",};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Order Management";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel,
                (option, nouser) -> {
                    ordersMenuOptions(option);
                }, user);  // lambda function to ignore the user.
    }

    private static Table getFreeTable() {
        int selectedTable = tableSelection(Restaurant.GET_INSTANCE());

        if (selectedTable == 0) {
            return null;
        }

        Table table = new Table(selectedTable);

        // TODO: think about the table occupation. If something gets the table and then breaks then the table will be occupied forever.
        //  try {} catch === exception if table alread occupied ?

        table.occupy();

        return table;
    }

    public static void ordersMenuOptions(int option) {
        switch (option) {
            case 1 -> {
                Table table = getFreeTable();
                if (table == null) {
                    break;
                }
                Order order = createNewOrder(table);
                table.assignOrder(order);
            }
            case 2 -> showOpenOrders(Restaurant.GET_INSTANCE());
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

    private static OrderedDish getDishFromUserInput() {
        printAllRestaurantDishesWithNumbers("Done");

        List<String> allDishesCommaSeparated = getMergedListOfNestedStringLists(getAllThreeDishesForMenu());

        ConsolePrinter.printQuestion("Enter the [index] <space> [quantity] of the item that you wish to add: ");

        int[] dishIndexAndQuantity = getUserInputMenuNumberAndQuantity(allDishesCommaSeparated.size());

        int dishIndex = dishIndexAndQuantity[0];
        int dishQuantity = dishIndexAndQuantity[1];

        if (dishIndex == 0) {
            return null;
        }
        String dishName = MenuBuilder.getFirstElementFromIndex(dishIndexAndQuantity[0], allDishesCommaSeparated);
        Dish dish = RestaurantMenu.getDishFromDishName(dishName);

        return new OrderedDish(dish, dishQuantity);
    }

    public static Order createNewOrder(Table table) {

        Order order = new Order(table);

        // print all dishes + prompt select dish to add to order + quantity ? (Enter for 1)
        // if quantity > 1 = for loop add to order
        // while loop - until 0 is pressed - add to order
        // Once order is

        while (true) {
            OrderedDish orderedDish = getDishFromUserInput();

            if (orderedDish == null) {
                break;
            }

            order.addDish(orderedDish);
            printCurrentOrder(order);
        }

        saveOrderToDB(order);

        return order;
    }

    private static void saveOrderToDB(Order order) {

        // Write to DB
        DBOperations.addOrderToOrdersTable(order);
        DBOperations.updateOrderDishesToDB(order);  // Write Ordered Dishes to DB
    }


    public static void printCurrentOrder(Order order) {
        List<OrderedDish> orderedDishes = order.getOrderedDishes();
        for (OrderedDish d : orderedDishes) {
            System.out.println("Ordered " + d.getQuantity() + " x " + d.getDish().getName() + " - " +
                    d.getDish().getPrice() + " Total: " + d.getDish().getPrice() * d.getQuantity());
        }
        System.out.println("Total: " + order.getTotalPrice());
    }

    public static void addDishToOrder() {

    }

    public static void printAllDishes() {
        RestaurantMenuBuilder.deleteItemFromRestaurantMenu();
    }

    public static int tableSelection(Restaurant restaurant) {
        int[] freeTables = restaurant.getFreeTablesArr();
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


    public static void showOpenOrders(Restaurant restaurant) {
        // print all open orders - table/order number ? Add numbers infront - ask for order selection
        // go to viewSingleOpenOrder() - decide what to use - int number in menu/table number/option number ?
        // TODO: Edit Tables/Orders - to load the order for that table from DB.

        System.out.println(restaurant.getTables());
        //Table{tableNumber=1, currentOrder=null, isOccupied=false}
        //, Table{tableNumber=2, currentOrder=null, isOccupied=  true }
        //, Table{tableNumber=3, currentOrder=null, isOccupied=false}
        //, Table{tableNumber=4, currentOrder=null, isOccupied=false}


        int[] occupiedTablesArr = restaurant.getOccupiedTablesArr();
        String frameLabel = "Open Orders"; // No frame label on the Login Menu page.
        String topMenuLabel = "Select Table Number To View Order:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Table /Open Order/";

        int selectedTableNumber = buildMenuOrder(occupiedTablesArr, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);

        Table selectedTable = restaurant.getTable(selectedTableNumber);

        Order selectedOrder = selectedTable.getCurrentOrder();

        System.out.println(selectedOrder);

        for (OrderedDish orderedDish : selectedOrder.getOrderedDishes()) {
            System.out.println(orderedDish.getDish().getName() + " - " + orderedDish.getQuantity());
        }

        // todo - print the above nicely + add menu Options - Add Dish to order/ remove Dish from order/ Print receip.. etc.
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
