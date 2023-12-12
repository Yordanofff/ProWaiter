package FrontEnd;

import BackEnd.DB.DBOperations;
import BackEnd.DB.TableOccupationException;
import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.DishType;
import BackEnd.Restaurant.Dishes.OrderedDish;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import BackEnd.Restaurant.Order;
import BackEnd.Restaurant.OrderStatus;
import BackEnd.Restaurant.Restaurant;
import BackEnd.Restaurant.Table;
import BackEnd.Users.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static BackEnd.Restaurant.Restaurant.getAllClosedOrdersInformation;
import static FrontEnd.MenuBuilder.*;
import static FrontEnd.MenuBuilder.printMenuOptionsInFrameTableRestaurantMenu;
import static FrontEnd.RestaurantMenuBuilder.*;
import static FrontEnd.UserInput.*;
import static FrontEnd.Validators.formatDecimalNumber;

public class OrdersOperationsMenuBuilder {
    static void ordersMenu(User user) {
        String[] menuOptions = new String[]{"New order", "Show open orders", "Deliver order", "Close order", "Show completed orders"};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Order Management";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel,
                (option, nouser) -> ordersMenuOptions(option), user);  // lambda function to ignore the user.
    }

    static void kitchenOrdersMenu(User user) {
        String[] menuOptions = new String[]{"Show open orders", "Set status to \"Cooking\"", "Set status to \"Cooked\""};
        String frameLabel = "[" + user.getUserType() + "]";
        String topMenuLabel = "Restaurant Menu Options";
        String optionZeroText = "Log out";
        String optionZeroMsg = "Logging out...";
        buildMenu(menuOptions, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, (option, nouser) -> kitchenOrdersMenuOptions(option), user);  // lambda function to ignore the user.
    }

    private static Table getFreeTableFromUserPrompt() {
        int selectedTableNumber = getFreeTableNumberFromUserPrompt(Restaurant.GET_INSTANCE());

        if (selectedTableNumber == 0) {
            return null;
        }

        return new Table(selectedTableNumber);
    }

    private static void ordersMenuOptions(int option) {
        switch (option) {
            case 1 -> {
                Table table = getFreeTableFromUserPrompt();
                if (table == null) {
                    break;
                }

                try {
                    // Update the occupancy of the table in the DB, so no one else can create orders on it.
                    table.occupy();
                } catch (TableOccupationException e) {
                    ConsolePrinter.printError("The table is already occupied. Please choose another table.");
                    ordersMenuOptions(1);
                }

                Order order = createNewOrder(table);
                if (order != null) {
                    table.assignOrder(order);
                }
            }
            case 2 -> printTablesGetAndEditOrder(Restaurant.GET_INSTANCE());
            case 3 -> printTablesReadyToBeDelivered(Restaurant.GET_INSTANCE());
            case 4 -> printTablesReadyToBeClosed(Restaurant.GET_INSTANCE());
            case 5 -> printClosedOrder();
        }
    }

    private static void kitchenOrdersMenuOptions(int option) {
        switch (option) {
            case 1 -> printTablesForKitchen(Restaurant.GET_INSTANCE());
            case 2 -> printTablesReadyToBeCooking(Restaurant.GET_INSTANCE());
            case 3 -> printTablesReadyToBeCooked(Restaurant.GET_INSTANCE());
        }
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

    private static Order createNewOrder(Table table) {
        Order order = new Order(table);

        // Create an entry in DB: this.isPaid = false, OrderStatus.CREATED, tableNumber
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
        DBOperations.addOrderToOrdersTable(order);
        DBOperations.updateOrderDishesToDB(order);  // Write Ordered Dishes to DB
    }

    private static int getFreeTableNumberFromUserPrompt(Restaurant restaurant) {
        int[] freeTables = restaurant.getFreeTablesArr();
        String frameLabel = "Free tables"; // No frame label on the Login Menu page.
        String topMenuLabel = "Please enter a table number:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Free Table";

        return buildMenuOrder(freeTables, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
    }

    private static void printTablesGetAndEditOrder(Restaurant restaurant) {
        int tableNumber = getOccupiedTableNumberFromUserPrompt(restaurant);
        if (tableNumber == 0) {
            return;
        }

        Table selectedTable = restaurant.getTable(tableNumber);
        Order selectedOrder = selectedTable.getCurrentOrder();

        viewSingleOpenOrderMenu(selectedOrder);
    }

    private static void printTablesForKitchen(Restaurant restaurant) {
        int tableNumber = getOccupiedTableNumberFromUserPrompt(restaurant);
        if (tableNumber == 0) {
            return;
        }

        Table selectedTable = restaurant.getTable(tableNumber);

        Order selectedOrder = selectedTable.getCurrentOrder();

        viewSingleOpenOrderMenuAction(1, selectedOrder);
    }

    private static int getOccupiedTableNumberFromUserPrompt(Restaurant restaurant) {
        int[] occupiedTablesArr = restaurant.getOccupiedTablesArr();
        String frameLabel = "Open Orders";
        String topMenuLabel = "Select Table Number To View Order:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Table /Open Order/";

        return buildMenuOrder(occupiedTablesArr, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
    }

    private static void printTablesReadyToBeDelivered(Restaurant restaurant) {
        // get tables with status "COOKED". Then set status to "SERVED".
        int tableNumber = getTableNumberFromUserPromptWithStatusCooked(restaurant);
        setOrderStatusForTableInDB(tableNumber, OrderStatus.SERVED, restaurant);
    }

    private static int getTableNumberFromUserPromptWithStatusCooked(Restaurant restaurant) {
        int[] occupiedTablesArr = restaurant.getCookedTablesArr();
        String frameLabel = "Cooked Orders";
        String topMenuLabel = "Select Table Number To View Order:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Table /Cooked Order/";

        return buildMenuOrder(occupiedTablesArr, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
    }

    private static void printTablesReadyToBeClosed(Restaurant restaurant) {
        // get tables with status "DELIVERED". Then set status to "PAID".
        int tableNumber = getTableNumberFromUserPromptWithStatusDelivered(restaurant);
        setOrderStatusForTableInDB(tableNumber, OrderStatus.PAID, restaurant);
    }

    private static int getTableNumberFromUserPromptWithStatusDelivered(Restaurant restaurant) {
        int[] occupiedTablesArr = restaurant.getDeliveredTablesArr();
        String frameLabel = "Delivered Orders";
        String topMenuLabel = "Select Table Number To View Order:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Table /Delivered Order/";

        return buildMenuOrder(occupiedTablesArr, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
    }

    private static void printTablesReadyToBeCooking(Restaurant restaurant) {
        // get tables with status "CREATED" or "UPDATED" - ready to be "COOKED". Then set status to COOKING.
        int tableNumber = getTableNumberFromUserPromptWithStatusCreatedOrUpdated(restaurant);
        setOrderStatusForTableInDB(tableNumber, OrderStatus.COOKING, restaurant);
    }

    private static int getTableNumberFromUserPromptWithStatusCreatedOrUpdated(Restaurant restaurant) {
        int[] occupiedTablesArr = restaurant.getReadyForKitchenCookingTablesArr();
        String frameLabel = "Kitchen Orders";
        String topMenuLabel = "Select Table Number To View Order:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Table (Open/Updated Order)";

        return buildMenuOrder(occupiedTablesArr, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
    }

    private static void printTablesReadyToBeCooked(Restaurant restaurant) {
        // get tables with status "COOKING". Then set status to "COOKED".
        int tableNumber = getTableNumberFromUserPromptWithStatusCooking(restaurant);
        setOrderStatusForTableInDB(tableNumber, OrderStatus.COOKED, restaurant);
    }

    private static int getTableNumberFromUserPromptWithStatusCooking(Restaurant restaurant) {
        int[] occupiedTablesArr = restaurant.getReadyForKitchenCookedTablesArr();
        String frameLabel = "Cooking Orders";
        String topMenuLabel = "Select Table Number To View Order:";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back!";
        String tableText = "Table (Cooking Order)";

        return buildMenuOrder(occupiedTablesArr, topMenuLabel, optionZeroText, optionZeroMsg, frameLabel, tableText);
    }

    private static void setOrderStatusForTableInDB(int tableNumber, OrderStatus orderStatus, Restaurant restaurant) {
        if (tableNumber == 0) {
            return;
        }

        Table selectedTable = restaurant.getTable(tableNumber);
        Order selectedOrder = selectedTable.getCurrentOrder();
        selectedOrder.setOrderStatusAndSaveToDB(orderStatus);
        ConsolePrinter.printInfo("Order on table [" + tableNumber + "] is now " + orderStatus.toString().toLowerCase() + ".");

        // Set table back to Free if order is paid.
        if (orderStatus == OrderStatus.PAID) {
            ConsolePrinter.printInfo("Total stay time: " + getStayTimeInMinutes(selectedOrder.getCreationDateTime()) + " minutes.");
            selectedTable.unOccupy();
            boolean isOrderSummary = getConfirmation("Do you want to see order summary?");
            if (isOrderSummary) {
                printReceipt(selectedOrder);
            }
        }
    }

    private static long getStayTimeInMinutes(LocalDateTime start) {
        return ChronoUnit.MINUTES.between(start, LocalDateTime.now());
    }

    private static void printClosedOrder() {
        String columnNames = "Index, Table Number, Order Number";
        List<String> allClosedOrdersInformation = getAllClosedOrdersInformation();

        printMenuOptionsInFrameTableRestaurantMenu(allClosedOrdersInformation, "CLOSED ORDERS", columnNames, "Go Back");

        ConsolePrinter.printQuestion("Enter an [index] number to view order.");

        int selectedIndex = getUserInputFrom0toNumber(allClosedOrdersInformation.size());

        if (selectedIndex == 0) {
            System.out.println("Going back..");
            return;
        }

        long id = getOrderID(allClosedOrdersInformation, selectedIndex);
        printAllDishesInOrder(id);
    }

    private static void printAllDishesInOrder(long id) {
        List<OrderedDish> orderedDishes = Restaurant.getAllOrderedDishesFromDB(id);

        List<List<String>> allDishes = getAllThreeOrderedDishesForMenu(orderedDishes);

        printAllOrderedDishesWithNumbers(allDishes, orderedDishes);
        pressAnyKeyToContinue();
        printClosedOrder();
    }

    private static long getOrderID(List<String> allClosedOrdersInformation, int selectedIndexByUser) {
        int orderNumberPositionInString = 2;
        String idString = getElementPositionFromIndex(selectedIndexByUser, allClosedOrdersInformation, orderNumberPositionInString);

        if (idString == null) {
            throw new RuntimeException("The ID is null/not found/ at position [" + orderNumberPositionInString + "].");
        }

        return Long.parseLong(idString);
    }

    private static void viewSingleOpenOrderMenu(Order order) {
        String[] menuOptions = new String[]{"Show order", "Add dish", "Remove dish", "Print Receipt"};
        String frameLabel = "[Table " + order.getTableNumber() + "]";
        String topMenuLabel = "Select option: ";
        String optionZeroText = "Go back";
        String optionZeroMsg = "Going back...";

        while (true) {
            int selectedOption = printMenuAndGetUsersChoice(menuOptions, topMenuLabel, optionZeroText, frameLabel);

            if (selectedOption == 0) {
                System.out.println(optionZeroMsg);
                break;
            }

            viewSingleOpenOrderMenuAction(selectedOption, order);
        }
    }

    private static void viewSingleOpenOrderMenuAction(int option, Order order) {
        switch (option) {
            case 1 -> {
                ConsolePrinter.printInfo("Order created at: " + convertDateTimeToHumanReadable(order.getCreationDateTime()));
                List<List<String>> allThreeOrderedDishesForMenu = getAllThreeOrderedDishesForMenu(order);
                printAllOrderedDishesWithNumbers(allThreeOrderedDishesForMenu, order);

                pressAnyKeyToContinue();
            }
            case 2 -> {
                addDishToOrder(order);
                order.setOrderStatusAndSaveToDB(OrderStatus.UPDATED);
            }
            case 3 -> removeDishFromOrder(order);  // Keep order status.
            case 4 -> printReceipt(order);
        }
    }

    private static String convertDateTimeToHumanReadable(LocalDateTime dt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return dt.format(formatter);
    }

    private static void printReceipt(Order order) {
        List<OrderedDish> orderedDishes = order.getOrderedDishesFromDB();
        List<OrderedDish> summarizedOrderedDishes = getSummarizeOrderedDishes(orderedDishes);
        List<List<String>> summarizedAsStrings = getAllThreeOrderedDishesForMenu(summarizedOrderedDishes);
        printAllOrderedDishesWithNumbers(summarizedAsStrings, order);
        pressAnyKeyToContinue();
    }

    private static List<OrderedDish> getSummarizeOrderedDishes(List<OrderedDish> orderedDishes) {
        List<OrderedDish> summarizedList = new ArrayList<>();

        for (OrderedDish orderedDish : orderedDishes) {
            OrderedDish existingOrderedDish = getOrderedDishFromList(summarizedList, orderedDish.getDish());
            if (existingOrderedDish == null) {
                summarizedList.add(orderedDish);
            } else {
                // Dish already in summarizedList
                existingOrderedDish.increaseQuantity(orderedDish.getQuantity());
            }
        }
        return summarizedList;
    }

    private static OrderedDish getOrderedDishFromList(List<OrderedDish> orderedDishes, Dish dish) {
        for (OrderedDish orderedDish : orderedDishes) {
            if (orderedDish.getDish().getName().equals(dish.getName())) {
                return orderedDish;
            }
        }
        return null;
    }

    private static void printOrderInMenu(Order order, String optionZeroText) {
        // Print the current order
        List<List<String>> allThreeOrderedDishesForMenu = getAllThreeOrderedDishesForMenu(order);
        printAllOrderedDishesWithNumbers(allThreeOrderedDishesForMenu, optionZeroText);
    }

    private static void removeDishFromOrder(Order order) {

        printOrderInMenu(order, "Go Back");

        List<OrderedDish> orderedDishes = order.getOrderedDishesFromDB();
        ConsolePrinter.printQuestion("Enter the [index] <space> [quantity] of the item that you wish to remove: ");
        int[] dishIndexAndQuantity = getUserInputMenuNumberAndQuantity(orderedDishes.size());
        int dishIndex = dishIndexAndQuantity[0];
        int dishQuantity = dishIndexAndQuantity[1];

        if (dishIndex == 0) {
            System.out.println("Cancelling.");
            return;
        }

        String selectedDishName = getDishNameFromSelection(getAllThreeOrderedDishesForMenu(order), dishIndex);
        if (selectedDishName == null) {
            ConsolePrinter.printError("This should never happen. Please try again..");
            return;
        }

        int totalNumberOfOrderedDishName = order.getTotalNumberOfOrderedDishName(selectedDishName);

        order.removeOrderedDish(selectedDishName, dishQuantity);
        int maximumPossibleDishesWithThatNameToRemove = Math.min(totalNumberOfOrderedDishName, dishQuantity);
        ConsolePrinter.printInfo("Successfully removed [" + maximumPossibleDishesWithThatNameToRemove + " x " + selectedDishName + "].");
    }

    private static String getDishNameFromSelection(List<List<String>> allThreeOrderedDishesForMenu, int selectedIndex) {
        String dishname;
        for (List<String> sectionWithTypeInMenu : allThreeOrderedDishesForMenu) {
            dishname = MenuBuilder.getFirstElementFromIndex(selectedIndex, sectionWithTypeInMenu);
            if (dishname != null) {
                return dishname;
            }
        }
        return null;
    }

    private static void addDishToOrder(Order order) {
        while (true) {
            OrderedDish orderedDish = getDishFromUserInput();

            if (orderedDish == null) {
                break;
            }

            order.addOrderedDish(orderedDish);
            ConsolePrinter.printInfo("Added [" + orderedDish.getQuantity() + " x " + orderedDish.getDish().getName() + "] to the order.\n");
            order.printCurrentOrder();
        }

    }

    private static List<List<String>> getAllThreeOrderedDishesForMenu(Order order) {
        return getAllThreeOrderedDishesForMenu(order.getOrderedDishesFromDB());
    }

    private static List<List<String>> getAllThreeOrderedDishesForMenu(List<OrderedDish> orderedDishList) {
        List<OrderedDish> allFood = new ArrayList<>();
        List<OrderedDish> allDrinks = new ArrayList<>();
        List<OrderedDish> allDeserts = new ArrayList<>();

        for (OrderedDish orderedDish : orderedDishList) {
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

        return MenuBuilder.combineLists(allFoodCommaSeparated, allDrinkCommaSeparated, allDessertCommaSeparated);
    }

    private static List<String> joinOrderedDishToString(List<OrderedDish> orderedDishes, boolean addDishType, boolean addNumbers, int startNumber) {
        List<String> result = new ArrayList<>();
        String dataToAdd = "";

        for (OrderedDish orderedDish : orderedDishes) {
            dataToAdd = "";
            if (addNumbers) {
                dataToAdd += startNumber + ", ";
                startNumber++;
            }
            Dish dish = orderedDish.getDish();
            dataToAdd += dish.getName() + ", " + formatDecimalNumber(dish.getPrice()) + ", " + orderedDish.getQuantity() + ", " + formatDecimalNumber(dish.getPrice() * orderedDish.getQuantity());
            if (addDishType) {
                dataToAdd += ", " + dish.getDishType();

            }
            result.add(dataToAdd);
        }
        return result;
    }

    private static void printAllOrderedDishesWithNumbers(List<List<String>> allThreeOrderedDishesForMenu, String optionZeroText, boolean printTotal, String totalPrice) {
        List<String> food = allThreeOrderedDishesForMenu.get(0);
        List<String> drink = allThreeOrderedDishesForMenu.get(1);
        List<String> dessert = allThreeOrderedDishesForMenu.get(2);

        String columnNames = "Index, Name, Price, Quantity, Total Price";

        int[] maxColumnLengths = getBiggest(food, drink, dessert, columnNames);

        MenuBuilder.printColumnNames(maxColumnLengths, columnNames);

        if (!food.isEmpty()) {
            if (drink.isEmpty() && dessert.isEmpty()) {
                printMenuOptionsInFrameTableRestaurantMenu(food, "Food", "", optionZeroText, maxColumnLengths, printTotal, totalPrice);
            } else {
                printMenuOptionsInFrameTableRestaurantMenu(food, "Food", "", "", maxColumnLengths);
            }
        }
        if (!drink.isEmpty()) {
            if (dessert.isEmpty()) {
                printMenuOptionsInFrameTableRestaurantMenu(drink, "Drinks", "", optionZeroText, maxColumnLengths, printTotal, totalPrice);
            } else {
                printMenuOptionsInFrameTableRestaurantMenu(drink, "Drinks", "", "", maxColumnLengths);
            }
        }
        if (!dessert.isEmpty()) {
            printMenuOptionsInFrameTableRestaurantMenu(dessert, "Deserts", "", optionZeroText, maxColumnLengths, printTotal, totalPrice);
        }

    }

    private static void printAllOrderedDishesWithNumbers(List<List<String>> allThreeOrderedDishesForMenu, String optionZeroText) {
        printAllOrderedDishesWithNumbers(allThreeOrderedDishesForMenu, optionZeroText, false, "NA");
    }

    private static void printAllOrderedDishesWithNumbers(List<List<String>> allThreeOrderedDishesForMenu, Order order) {
        String totalPrice = Validators.formatDecimalNumber(order.getCalculatedTotalPrice());
        printAllOrderedDishesWithNumbers(allThreeOrderedDishesForMenu, "", true, totalPrice);
    }

    private static void printAllOrderedDishesWithNumbers(List<List<String>> allThreeOrderedDishesForMenu, List<OrderedDish> orderedDishes) {
        String totalPrice = Validators.formatDecimalNumber(Order.getCalculatedTotalPrice(orderedDishes));
        printAllOrderedDishesWithNumbers(allThreeOrderedDishesForMenu, "", true, totalPrice);
    }

}
