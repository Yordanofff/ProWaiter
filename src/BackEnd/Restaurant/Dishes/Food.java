package BackEnd.Restaurant.Dishes;

public class Food extends Dish{
    public static final DishType dishType = DishType.FOOD;
    public Food() {
        this.setDishType(dishType);
    }
}
