package BackEnd.Restaurant;

import BackEnd.Users.User;
import FrontEnd.ConsolePrinter;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private static String restaurantName;
    private static List<Table> tables;
    private List<User> users;  // todo ?

    public Restaurant(int numberOfTables) {
        tables = new ArrayList<>();
        for (int i = 1; i <= numberOfTables; i++) {
            tables.add(new Table(i));
        }
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

    public static int[] getFreeTablesArr(){
        return getTables(getFreeTables());
    }

    public static int[] getOccupiedTablesArr(){
        return getTables(getOccupiedTables());
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
