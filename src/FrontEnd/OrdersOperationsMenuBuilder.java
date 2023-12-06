package FrontEnd;

import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.DishType;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import BackEnd.Restaurant.Order;
import BackEnd.Restaurant.OrderStatus;
import BackEnd.Restaurant.Restaurant;
import BackEnd.Restaurant.Table;
import BackEnd.Users.User;

import java.util.ArrayList;
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
        String[] menuOptions = new String[]{"New order", "Show open orders", "Show completed orders"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Order Management";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel,
                (option, nouser) -> ordersMenuOptions(option), user);  // lambda function to ignore the user.
    }

    private static Table getFreeTable() {
        int selectedTable = tableSelection(Restaurant.GET_INSTANCE());

        if (selectedTable == 0) {
            return null;
        }

        Table table = new Table(selectedTable);

        // TODO: think about the table occupation. If something gets the table and then breaks then the table will be occupied forever.
        //  try {} catch === exception if table alread occupied ?

        return table;
    }

    public static void ordersMenuOptions(int option) {
        switch (option) {
            case 1 -> {
                Table table = getFreeTable();
                if (table == null) {
                    break;
                }
                table.occupy();
                Order order = createNewOrder(table);
                if (order != null) {
                    table.assignOrder(order); // TODO: is it needed?
                }
            }
            case 2 -> printTablesGetAndEditOrder(Restaurant.GET_INSTANCE());
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
//            case 2 -> getOccupiedTableNumberFromUserPrompt();
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
        // this.isPaid = false;
        // this.orderedDishes = new ArrayList<>();
        // this.orderStatus = OrderStatus.CREATED;
        // tableNumber(set);

        saveOrderToDB(order);

        boolean noDishesAddedToOrder = false;

        while (true) {
            OrderedDish orderedDish = getDishFromUserInput();

            if (orderedDish == null) {
                List<OrderedDish> alreadyOrderedDishes = order.getOrderedDishesFromDB();

                // Check if at least one dish was added to the order. If not - then set table as unoccupied + delete order from DB.
                if (alreadyOrderedDishes.isEmpty()) {
                    noDishesAddedToOrder = true;
                }
                break;
            }

            order.addOrderedDish(orderedDish);

            order.printCurrentOrder();
        }

        if (noDishesAddedToOrder) {
            ConsolePrinter.printInfo("No dishes selected for table [" + order.getTableNumber() + "].");
            table.unOccupy();
            DBOperations.deleteOrderByID(order);
            return null;
        }
        return order;
    }

    private static void saveOrderToDB(Order order) {
        // Write to DB
        DBOperations.addOrderToOrdersTable(order);
        DBOperations.updateOrderDishesToDB(order);  // Write Ordered Dishes to DB
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

        int selectedTable = buildMenuOrder(freeTables, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
        return selectedTable;
    }

    public static void printTablesGetAndEditOrder(Restaurant restaurant) {

        int tableNumber = getOccupiedTableNumberFromUserPrompt(restaurant);

        Table selectedTable = restaurant.getTable(tableNumber);

        Order selectedOrder = selectedTable.getCurrentOrder();

        viewSingleOpenOrder(selectedOrder);
    }

    public static int getOccupiedTableNumberFromUserPrompt(Restaurant restaurant) {
        int[] occupiedTablesArr = restaurant.getOccupiedTablesArr();
        String frameLabel = "Open Orders"; // No frame label on the Login Menu page.
        String topMenuLabel = "Select Table Number To View Order:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Table /Open Order/";

        return buildMenuOrder(occupiedTablesArr, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
    }

    public static void viewSingleOpenOrder(Order order) {
        List<List<String>> allThreeOrderedDishesForMenu = getAllThreeOrderedDishesForMenu(order);

        // Print the current order
        printAllOrderedDishesWithNumbers(allThreeOrderedDishesForMenu);

        // Print additional small menu with options
        viewSingleOpenOrderMenu(order);
    }

    public static void viewSingleOpenOrderMenu(Order order) {
        String[] menuOptions = new String[]{"Add dish", "Remove dish", "Close order"};  // serve order ?
        String frameLabel = "[Table " + order.getTableNumber() + "]";
        String topMenuLabel = "Select option: ";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back...";

        // buildMenu
        while (true) {
            int selectedOption = printMenuAndGetUsersChoice(menuOptions, topMenuLabel, optionZeroText, frameLabel);

            // Exit if 0
            if (selectedOption == 0) {
                System.out.println(optionZeroMsg);
                break;
            }

            viewSingleOpenOrderMenuAction(selectedOption, order);
        }

    }

    public static void viewSingleOpenOrderMenuAction(int option, Order order) {
        switch (option) {
            case 1 -> addDishToOrder(order);
//            case 2 -> ;
//            case 3 -> ;
        }
    }

    public static void addDishToOrder(Order order) {
        while (true) {
            OrderedDish orderedDish = getDishFromUserInput();

            System.out.println(orderedDish);

            if (orderedDish == null) {
                break;
            }

            order.addOrderedDish(orderedDish);
            order.printCurrentOrder();
        }

        saveOrderToDB(order);
    }

    public static void showClosedOrders() {
        // todo
    }

    public static void changeOrderStatus(OrderStatus orderStatus) {
        // todo
    }

    public static List<List<String>> getAllThreeOrderedDishesForMenu(Order order) {
        List<OrderedDish> allFood = new ArrayList<>();
        List<OrderedDish> allDrinks = new ArrayList<>();
        List<OrderedDish> allDeserts = new ArrayList<>();

        for (OrderedDish orderedDish : order.getOrderedDishesFromDB()) {
            Dish dish = orderedDish.getDish();
            DishType type = dish.getDishType();
            if (type == DishType.FOOD) {
                allFood.add(orderedDish);
            } else if (type == DishType.DRINK) {
                allDrinks.add(orderedDish);
            } else {
                allDeserts.add(orderedDish);
            }
        }

        List<String> allFoodCommaSeparated = joinOrderedDishToString(allFood, false, true, 1);
        List<String> allDrinkCommaSeparated = joinOrderedDishToString(allDrinks, false, true, allFoodCommaSeparated.size() + 1);
        List<String> allDessertCommaSeparated = joinOrderedDishToString(allDeserts, false, true, allFoodCommaSeparated.size() + allDrinkCommaSeparated.size() + 1);

        List<List<String>> result = new ArrayList<>();
        result.add(allFoodCommaSeparated);
        result.add(allDrinkCommaSeparated);
        result.add(allDessertCommaSeparated);

        return result;
    }

    public static List<String> joinOrderedDishToString(List<OrderedDish> orderedDishes, boolean addDishType, boolean addNumbers, int startNumber) {
        List<String> result = new ArrayList<>();
        String dataToAdd = "";

        for (OrderedDish orderedDish : orderedDishes) {
            dataToAdd = "";
            if (addNumbers) {
                dataToAdd += startNumber + ", ";
                startNumber++;
            }
            Dish dish = orderedDish.getDish();
            dataToAdd += dish.getName() + ", " + dish.getPrice() + ", " + orderedDish.getQuantity() + ", " + dish.getPrice() * orderedDish.getQuantity();
            if (addDishType) {
                dataToAdd += ", " + dish.getDishType();

            }
            result.add(dataToAdd);
        }
        return result;
    }

    public static void printAllOrderedDishesWithNumbers(List<List<String>> allThreeOrderedDishesForMenu) {
        // TODO: combine with RestaurantMenuBuilder - printAllRestaurantDishesWithNumbers in RestaurantMenu ?
        List<String> food = allThreeOrderedDishesForMenu.get(0);
        List<String> drink = allThreeOrderedDishesForMenu.get(1);
        List<String> dessert = allThreeOrderedDishesForMenu.get(2);

        String columnNames = "Index, Name, Price, Quantity, Total Price";

        int[] maxColumnLengths = getBiggest(food, drink, dessert, columnNames);

        // Print only the categories that have items. Add column names to the first one that has data.
        if (!food.isEmpty()) {
            printMenuOptionsInFrameTableRestaurantMenu(food, "Food", columnNames, "", maxColumnLengths);
            if (!drink.isEmpty()) {
                printMenuOptionsInFrameTableRestaurantMenu(drink, "Drinks", "", "", maxColumnLengths);
            }
            if (!dessert.isEmpty()) {
                printMenuOptionsInFrameTableRestaurantMenu(dessert, "Deserts", "", "", maxColumnLengths);
            }
        } else if (!drink.isEmpty()) {
            printMenuOptionsInFrameTableRestaurantMenu(drink, "Drinks", columnNames, "", maxColumnLengths);
            if (!dessert.isEmpty()) {
                printMenuOptionsInFrameTableRestaurantMenu(dessert, "Deserts", "", "", maxColumnLengths);
            }
        } else {
            printMenuOptionsInFrameTableRestaurantMenu(dessert, "Deserts", columnNames, "", maxColumnLengths);
        }

    }

}
