package school.hei.spring_restaurant.exception;

public class InvalidStockQueryException extends RuntimeException {
    public InvalidStockQueryException(String message) {
        super(message);
    }
}
