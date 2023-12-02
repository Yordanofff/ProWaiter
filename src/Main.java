import BackEnd.DB.DBOperations;
import BackEnd.Restaurant.Dishes.Dessert;
import BackEnd.Restaurant.Dishes.Dish;
import BackEnd.Restaurant.Dishes.Drink;
import BackEnd.Restaurant.Dishes.Food;
import BackEnd.Restaurant.Menu.RestaurantMenu;
import BackEnd.Users.*;
import FrontEnd.*;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {

//        runApp();

        testRunAppAsADMIN();
//        testRunAppAsCOOK();
//        testRunAppAsWaiter();

    }

    public static void populateMenu() {
        // Create a HashMap for Food
        HashMap<String, Double> foodMenu = new HashMap<>();
        foodMenu.put("Pizza", 12.99);
        foodMenu.put("Burger", 8.49);
        foodMenu.put("Pasta", 10.95);
        foodMenu.put("Salad", 6.99);

        // Create a HashMap for drinks
        HashMap<String, Double> drinkMenu = new HashMap<>();
        drinkMenu.put("Soda", 2.49);
        drinkMenu.put("Iced Tea", 1.99);
        drinkMenu.put("Smoothie", 4.95);

        // Create a HashMap for desserts
        HashMap<String, Double> dessertMenu = new HashMap<>();
        dessertMenu.put("Chocolate Cake", 5.99);
        dessertMenu.put("Ice Cream", 3.50);
        dessertMenu.put("Cheesecake", 6.75);

        for (HashMap.Entry<String, Double> entry : foodMenu.entrySet()) {
            Dish food = new Food(entry.getKey(), entry.getValue());
            RestaurantMenu.addDish(food);
        }
        for (HashMap.Entry<String, Double> entry : drinkMenu.entrySet()) {
            Dish drink = new Drink(entry.getKey(), entry.getValue());
            RestaurantMenu.addDish(drink);
        }
        for (HashMap.Entry<String, Double> entry : dessertMenu.entrySet()) {
            Dish desert = new Dessert(entry.getKey(), entry.getValue());
            RestaurantMenu.addDish(desert);
        }

        List<Dish> x = RestaurantMenu.getDishes();

        for (Dish d : x) {
            DBOperations.addDishToRestaurantMenuItems(d);
        }
    }

    public static void runApp() {
        UserInterface.startApp();
    }

    public static void testRunAppAsADMIN(){
        User user = new Administrator("Ivo", "Yordanov", "admin", "123");
        MenuBuilder.AdminMenu(user);
    }

    public static void testRunAppAsCOOK(){
        User user = new Cook("Ivo", "Yordanov", "cook1", "123");
        MenuBuilder.KitchenMenu(user);
    }

    public static void testRunAppAsWaiter(){
        User user = new Waiter("Ivo", "Yordanov", "waiter1", "123");
        MenuBuilder.WaiterMenu(user);
    }

}

