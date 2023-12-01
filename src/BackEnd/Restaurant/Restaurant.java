package BackEnd.Restaurant;

import BackEnd.Users.User;
import BackEnd.Users.UserManager;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private RestaurantInfo restaurantInfo; //= getRestaurantInfoTest();
    private static List<Table> tables = new ArrayList<>();
//    private static List<User> users = new ArrayList<>();

    public Restaurant() {
        initRestaurantInfo();
        initTables();
    }

    public void initRestaurantInfo() {
        // Load restaurantInfo from DB. If not in DB - Ask User + write to DB.
        restaurantInfo = RestaurantInfo.getRestaurantInfoFromDB();
        if (restaurantInfo == null) {
            ConsolePrinter.printWarning("RestaurantInfo not found in the DB.");
            RestaurantInfo newRestaurantInfo = new RestaurantInfo();
            setRestaurantInfo(newRestaurantInfo.getNewRestaurantInfoFromUser());
        } else {
            ConsolePrinter.printInfo("Restaurant info loaded from DB.");
        }
    }

    public void initTables() {
        // todo
        // Load tables from DB. If not in DB - Create new tables + write to DB.
//        tables = Table.getTablesFromDB();
//        if (tables.isEmpty()) {
//            ConsolePrinter.printWarning("Tables not found in the DB.");
//            initNewTablesFromUser();
//            Table.saveTablesInDB(tables);
//        } else {
//            ConsolePrinter.printInfo("Tables loaded from DB.");
//        }

    }

//
//    public List<Table> getTables() {
//        if (restaurantInfo == null || )
//        return tables;
//    }

    public static void setTables(List<Table> tables) {
        Restaurant.tables = tables;
    }

    public static List<Table> generateNewTables(int numberOfTables) {
        List<Table> newTables = new ArrayList<>();
        for (int i = 1; i <= numberOfTables; i++) {
            newTables.add(new Table(i));
        }
        return newTables;
    }

    public static Table getTable(int tableNumber) {
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

    public static List<Table> getFreeTables() {
        List<Table> freeTables = new ArrayList<>();
        for (Table table : tables) {
            if (!table.isOccupied()) {
                freeTables.add(table);
            }
        }
        return freeTables;
    }

    public static List<Table> getOccupiedTables() {
        List<Table> occupiedTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.isOccupied()) {
                occupiedTables.add(table);
            }
        }
        return occupiedTables;
    }

    private static int[] getTables(List<Table> tablesList) {
        int[] tables = new int[tablesList.size()];
        for (int i = 0; i < tablesList.size(); i++) {
            tables[i] = tablesList.get(i).getTableNumber();
        }
        return tables;
    }

    public static int[] getFreeTablesArr() {
        return getTables(getFreeTables());
    }

    public static int[] getOccupiedTablesArr() {
        return getTables(getOccupiedTables());
    }

    public List<User> getUsers() {
        return UserManager.getActiveUsers();
    }

    public int getNumberOfTables() {
        return restaurantInfo.getNumberOfTables();
    }

    public String getRestaurantName() {
        return restaurantInfo.getRestaurantName();
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }

    public void setRestaurantInfo(RestaurantInfo restaurantInfo) {
        this.restaurantInfo = restaurantInfo;
        RestaurantInfo.saveRestaurantInfoInDB(restaurantInfo);
    }

//    private static String[] getTablesString(List<Table> tablesList) {
//        String[] tables = new String [tablesList.size()];
//        for (int i = 0; i < tablesList.size(); i++) {
//            int tableNumber = tablesList.get(i).getTableNumber();
//            String tableNumberStr = "Table " + tableNumber;
//            tables[i] = tableNumberStr;
//        }
//        return tables;
//    }
//
//    public static String[] getFreeTablesArr(){
//        return getTablesString(getFreeTables());
//    }
//
//    public static String[] getOccupiedTablesArr(){
//        return getTablesString(getOccupiedTables());
//    }

}
