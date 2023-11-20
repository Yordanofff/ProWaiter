package BackEnd.Restaurant;

public class Food extends Dish{
    public static final DishType dishType = DishType.FOOD;
    public Food() {
        this.setDishType(dishType);
    }
}
