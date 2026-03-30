package school.hei.spring_restaurant.exception;

public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException(Integer id) {
        super("Dish.id=" + id + " is not found");
    }
}
