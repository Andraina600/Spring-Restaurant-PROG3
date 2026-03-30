package school.hei.spring_restaurant.exception;

public class IngredientNotFoundException extends RuntimeException {
    public IngredientNotFoundException(Integer id) {
        super("Could not find ingredient with id " + id);
    }
}
