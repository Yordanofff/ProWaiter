package BackEnd.Restaurant;

import BackEnd.DB.DBOperations;

import java.util.List;

public class Table {
    private int tableNumber;
    private Order currentOrder;
    private boolean isOccupied;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.currentOrder = null;
        this.isOccupied = false;
    }

    public Table(int tableNumber, boolean isOccupied){
        this.tableNumber = tableNumber;
        this.isOccupied = isOccupied;
        this.currentOrder = null;
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableNumber=" + tableNumber +
                ", currentOrder=" + currentOrder +
                ", isOccupied=" + isOccupied +
                "} \n";
    }

    public static List<Table> getTablesFromDB(){
        return DBOperations.getAllTablesFromDB();
    }

    public void freeUpTable() {
        unOccupy();
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void assignOrder(Order order) {
        this.currentOrder = order;
    }

    private void clearOrder() {
        this.currentOrder = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void occupy() {
        isOccupied = true;
    }

    public void unOccupy() {
        isOccupied = false;
    }
}
