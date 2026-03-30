package school.hei.spring_restaurant.validator;

import org.springframework.stereotype.Component;
import school.hei.spring_restaurant.entity.StockValue;
import school.hei.spring_restaurant.exception.InvalidStockQueryException;
import school.hei.spring_restaurant.type.UnitType;

import java.time.Instant;

@Component
public class StockQueryValidator {
    public void validateStockQuery(Integer ingredientId, Instant at, UnitType unit) {
        if (ingredientId == null) {
            throw new InvalidStockQueryException("Parameter 'id' is mandatory");
        }
        if (at == null) {
            throw new InvalidStockQueryException("Parameter 'at' is mandatory");
        }
        if (unit == null) {
            throw new InvalidStockQueryException("Parameter 'unit' is mandatory");
        }
    }
}
