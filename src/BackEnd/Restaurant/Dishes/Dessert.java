package BackEnd.Restaurant.Dishes;

public class Dessert extends Dish{
    public static final DishType dishType = DishType.DESSERT;
    public Dessert() {
        this.setDishType(dishType);
    }
}
