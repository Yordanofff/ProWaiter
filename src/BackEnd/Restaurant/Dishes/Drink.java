package BackEnd.Restaurant.Dishes;

public class Drink extends Dish {
    public static final DishType dishType = DishType.DRINK;

    public Drink(String name, double price) {
        super(name, price, dishType);
    }
}
