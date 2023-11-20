package BackEnd.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenu {
    private List<Dish> dishes = new ArrayList<>();

    private void loadDishesFromDB() {
        //todo - open file/DB and load dishes
    }

    private void saveDishesToDB() {
        // todo
    }

    public void addDish(Dish dish) {
        // todo - check if already in?
        dishes.add(dish);
    }

    public void removeDish(Dish dish) {
        // todo: removing dishes will be by selecting the dish number in the menu (should have all data about the dish)
        // todo: mark as not-available? - re-add later on/tomorrow?
        dishes.remove(dish);
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}
