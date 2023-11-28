package BackEnd.Restaurant;

public class Table {
    private int tableNumber;
    private Order currentOrder;
    private boolean isOccupied;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.currentOrder = null;
        this.isOccupied = false;
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableNumber=" + tableNumber +
                ", currentOrder=" + currentOrder +
                ", isOccupied=" + isOccupied +
                "} \n";
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

    private void occupy() {
        isOccupied = true;
    }

    private void unOccupy() {
        isOccupied = false;
    }
}
