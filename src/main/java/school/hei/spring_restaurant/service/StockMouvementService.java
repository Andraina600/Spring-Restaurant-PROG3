package school.hei.spring_restaurant.service;

import org.springframework.stereotype.Service;
import school.hei.spring_restaurant.DTO.StockMouvementCreateDTO;
import school.hei.spring_restaurant.DTO.StockMouvementDTO;
import school.hei.spring_restaurant.entity.StockMouvement;
import school.hei.spring_restaurant.exception.IngredientNotFoundException;
import school.hei.spring_restaurant.repository.StockMouvementRepository;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<StockMouvementDTO> addStockMovements(Integer ingredientId, List<StockMouvementCreateDTO> movements) {

        if (!repository.existsIngredient(ingredientId)) {
            throw new IngredientNotFoundException(ingredientId);
        }

        try {
            List<StockMouvement> created = repository.addStockMovements(ingredientId, movements);
            return created.stream()
                    .map(sm -> new StockMouvementDTO(
                            sm.getId(),
                            sm.getCreationDatetime(),
                            sm.getValue().getUnit(),
                            sm.getValue().getQuantity(),
                            sm.getType()
                    ))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout des mouvements", e);
        }
    }
}
