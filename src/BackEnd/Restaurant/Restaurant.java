package BackEnd.Restaurant;

import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.OrderedDish;
import FrontEnd.ConsolePrinter;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

import static FrontEnd.MenuBuilder.sep;

public class Restaurant {

    // Singleton pattern - we'll be working with single Restaurant.
    // When needed use: Restaurant.GET_INSTANCE()
    private static final Restaurant INSTANCE = new Restaurant();

    public static Restaurant GET_INSTANCE() {
        return Restaurant.INSTANCE;
    }

    private RestaurantInfo restaurantInfo = loadRestaurantInfo();
    private List<Table> tables = getTablesFromDBorPopulateTheTablesIfNone();//= new ArrayList<>();

    public RestaurantInfo loadRestaurantInfo() {
        // Load restaurantInfo from DB. If not in DB - Ask User + write to DB.
        RestaurantInfo restaurantInfoDB = RestaurantInfo.getRestaurantInfoFromDB();
        if (restaurantInfoDB == null) {
            ConsolePrinter.printWarning("RestaurantInfo not found in the DB.");
            RestaurantInfo newRestaurantInfo = new RestaurantInfo();
            setRestaurantInfo(newRestaurantInfo.getNewRestaurantInfoFromUser());
            return newRestaurantInfo;
        }

        ConsolePrinter.printInfo("Restaurant info loaded from DB.");
        return restaurantInfoDB;
    }

    public List<Table> getTablesFromDBorPopulateTheTablesIfNone() {
        // TODO: Populate the tables just once when the RestaurantInfo is initialized. Then just load from DB.
        // Load tables from DB. If not in DB - Create new tables + write to DB.
        List<Table> tablesFromDB = Table.getTablesFromDB();
        if (tablesFromDB.isEmpty()) {
            ConsolePrinter.printWarning("Tables not found in the DB.");
            List<Table> newTableList = generateNewTables(restaurantInfo.getNumberOfTables());
            if (DBOperations.writeTablesToDB(newTableList)) {
                ConsolePrinter.printInfo("New tables successfully written to the database.");
            }
            return newTableList;
        }
        ConsolePrinter.printInfo("Tables loaded from DB.");
        return tablesFromDB;
    }

    public static List<Table> generateNewTables(int numberOfTables) {
        List<Table> newTables = new ArrayList<>();
        for (int i = 1; i <= numberOfTables; i++) {
            newTables.add(new Table(i));
        }
        return newTables;
    }

    public Table getTable(int tableNumber) {
        if (tableNumber > tables.size()) {
            ConsolePrinter.printError("Table [" + tableNumber + "] doesn't exist. Max table number [" + tables.size() + "]");
            return null;
        }
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }
        return null; // Table not found
    }

    public List<Table> getFreeTables() {
        List<Table> tablesFromDB = Table.getTablesFromDB();
        List<Table> freeTables = new ArrayList<>();
        for (Table table : tablesFromDB) {
            if (!table.isOccupied()) {
                freeTables.add(table);
            }
        }
        return freeTables;
    }

    public List<Table> getOccupiedTables() {
        List<Table> tablesFromDB = Table.getTablesFromDB();
        List<Table> occupiedTables = new ArrayList<>();
        for (Table table : tablesFromDB) {
            if (table.isOccupied()) {
                occupiedTables.add(table);
            }
        }
        return occupiedTables;
    }

    public List<Table> getCookedTables() {
        List<Order> cookedOrders = DBOperations.getAllOrdersFromDBWithStatus(OrderStatus.COOKED);

        return getTablesFromOrders(cookedOrders);
    }

    public List<Table> getDeliveredTables() {
        List<Order> servedOrders = DBOperations.getAllOrdersFromDBWithStatus(OrderStatus.SERVED);

        return getTablesFromOrders(servedOrders);
    }

    public List<Table> getReadyForKitchenCookingTables() {
        List<Order> allOrdersWithCreatedAndUpdatedStatus = getAllOrdersWithCreatedAndUpdatedStatus();

        // Sort the array - it will not be sorted because it's created from 2 different lists.
        allOrdersWithCreatedAndUpdatedStatus.sort(Comparator.comparingInt(Order::getTableNumber));

        return getTablesFromOrders(allOrdersWithCreatedAndUpdatedStatus);
    }

    public List<Order> getAllOrdersWithCreatedAndUpdatedStatus(){
        List<Order> createdOrders = DBOperations.getAllOrdersFromDBWithStatus(OrderStatus.CREATED);
        List<Order> updatedOrders = DBOperations.getAllOrdersFromDBWithStatus(OrderStatus.UPDATED);

        List<Order> combinedOrders = new ArrayList<>();
        combinedOrders.addAll(createdOrders);
        combinedOrders.addAll(updatedOrders);

        return combinedOrders;
    }

    public List<Table> getReadyForKitchenCookedTables() {
        List<Order> createdOrders = DBOperations.getAllOrdersFromDBWithStatus(OrderStatus.COOKING);

        return getTablesFromOrders(createdOrders);
    }

    public static List<Order> getAllClosedOrders() {
        return DBOperations.getAllOrdersFromDBWithStatus(OrderStatus.PAID);
    }

    public static List<String> getAllClosedOrdersInformation() {
        List<Order> closedOrders = getAllClosedOrders();
        List<String> ordersInformation = new ArrayList<>();

        for (int i = 0; i < closedOrders.size(); i++) {
            int tableNumber = closedOrders.get(i).getTableNumber();
            long id = closedOrders.get(i).getOrderNumber();
            ordersInformation.add((i + 1) + sep + tableNumber + sep + id);
        }

        return ordersInformation;
    }

    public static List<OrderedDish> getAllOrderedDishesFromDB(long id) {
        return DBOperations.getOrdersDishesForID(id);
    }

    public static List<Table> getTablesFromOrders(List<Order> orders) {
        List<Table> tables = new ArrayList<>();
        for (Order order : orders) {
            Table table = new Table(order.getTableNumber());
            tables.add(table);
        }
        return tables;
    }

    private static int[] getTables(List<Table> tablesList) {
        int[] tables = new int[tablesList.size()];
        for (int i = 0; i < tablesList.size(); i++) {
            tables[i] = tablesList.get(i).getTableNumber();
        }
        return tables;
    }

    public int[] getFreeTablesArr() {
        return getTables(getFreeTables());
    }

    public int[] getOccupiedTablesArr() {
        return getTables(getOccupiedTables());
    }

    public int[] getCookedTablesArr() {
        return getTables(getCookedTables());
    }

    public int[] getDeliveredTablesArr() {
        return getTables(getDeliveredTables());
    }

    public int[] getReadyForKitchenCookingTablesArr() {
        return getTables(getReadyForKitchenCookingTables());
    }

    public int[] getReadyForKitchenCookedTablesArr() {
        return getTables(getReadyForKitchenCookedTables());
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
        RestaurantInfo.saveRestaurantInfoInDB(restaurantInfo);
    }

//    public int getNumberOfTables() {
//        return restaurantInfo.getNumberOfTables();
//    }
//
//    public String getRestaurantName() {
//        return restaurantInfo.getRestaurantName();
//    }
//
//    public RestaurantInfo getRestaurantInfo() {
//        return restaurantInfo;
//    }
//
//    public List<Table> getTables() {
//        return tables;
//    }
//
//    public void setTables(List<Table> tables) {
//        this.tables = tables;
//        if (DBOperations.writeTablesToDB(tables)) {
//            ConsolePrinter.printInfo("Successfully wrote tables to DB.");
//        }
//    }

}
