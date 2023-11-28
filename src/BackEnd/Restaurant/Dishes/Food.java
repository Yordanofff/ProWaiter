package BackEnd.Restaurant.Dishes;

public class Food extends Dish {
    public static final DishType dishType = DishType.FOOD;

    public Food() {
        super(dishType);
    }

    public Food(String name, double price) {
        super(name, price, dishType);
    }
}
