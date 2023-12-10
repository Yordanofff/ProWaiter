package BackEnd.Restaurant.Dishes;

public class Food extends Dish {
    private static final DishType dishType = DishType.FOOD;

    public Food(String name, double price) {
        super(name, price, dishType);
    }
}
