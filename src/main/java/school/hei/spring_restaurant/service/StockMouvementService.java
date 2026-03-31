package school.hei.spring_restaurant.service;

import org.springframework.stereotype.Service;
import school.hei.spring_restaurant.entity.StockMouvement;
import school.hei.spring_restaurant.exception.IngredientNotFoundException;
import school.hei.spring_restaurant.repository.StockMouvementRepository;

import java.time.Instant;
import java.util.List;
@Service
public class StockMouvementService {
    private final StockMouvementRepository repository;

    public StockMouvementService(StockMouvementRepository repository) {
        this.repository = repository;
    }

    public List<StockMouvement> getByIngredientAndDateRange(
            int ingredientId, Instant from, Instant to) {

        if (!repository.existsIngredient(ingredientId)) {
            throw new IngredientNotFoundException(ingredientId);
        }

        return repository.findByIngredientIdAndDateRange(ingredientId, from, to);
    }
}
