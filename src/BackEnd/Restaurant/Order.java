package BackEnd.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private List<Dish> order = new ArrayList<>();
    private double totalPrice = 0;

    // Keep incrementing the number on each new order, so we have total number of orders.
    private static int orderNumber = 0;

    public Order() {
        orderNumber += 1;
    }

    public Map<Integer, String> getCombinedItems(){
        // todo - to be tested.... should not have the same item name in the menu! Kameniza 250ml/ Kameniza 500ml. Or include bottle size?
        // Map with number of items in the order (before printing the receipt)
        Map<Integer, String> combinedItems = new HashMap<>();
        for (Dish dish : order) {
            int quantity = 1; // Since quantity will always be 1
            String itemName = dish.getName();

            // Check if the item is already in the map
            if (combinedItems.containsValue(itemName)) {
                // If yes, find the entry with the same item name and increment the quantity
                for (Map.Entry<Integer, String> entry : combinedItems.entrySet()) {
                    if (entry.getValue().equals(itemName)) {
                        quantity = entry.getKey() + 1;
                        break;
                    }
                }
            }

            // Update or add the entry in the map
            combinedItems.put(quantity, itemName);

        }

        return combinedItems;
    }

    public void printReceipt() {
        // todo create MenuBuilder for receipts. Use getCombinedItems ?

        System.out.println("Order number: " + orderNumber);

        for (Dish dish : order) {
            System.out.println("Dish | Amount | Single Price | Total Price");

        }

        System.out.println("Total: " + getTotalPrice());
    }

    public void addDishToOrder(Dish dish) {
        order.add(dish);
        setTotalPrice(getTotalPrice() + dish.getPrice());
    }

    public void removeDishFromOrder(Dish dish) {
        order.remove(dish);
        setTotalPrice(getTotalPrice() - dish.getPrice());
    }

    public List<Dish> getOrder() {
        return order;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
