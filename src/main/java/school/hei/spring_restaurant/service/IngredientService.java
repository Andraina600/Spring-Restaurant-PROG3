package school.hei.spring_restaurant.service;

import org.springframework.stereotype.Service;
import school.hei.spring_restaurant.DTO.StockMouvementCreateDTO;
import school.hei.spring_restaurant.entity.Ingredient;
import school.hei.spring_restaurant.entity.StockValue;
import school.hei.spring_restaurant.entity.UnitConversion;
import school.hei.spring_restaurant.exception.IngredientNotFoundException;
import school.hei.spring_restaurant.repository.IngredientRepository;
import school.hei.spring_restaurant.repository.StockMouvementRepository;
import school.hei.spring_restaurant.type.UnitType;
import school.hei.spring_restaurant.validator.StockQueryValidator;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final StockMouvementRepository stockMouvementRepository;
    private final StockQueryValidator stockQueryValidator;

    public IngredientService(IngredientRepository ingredientRepository, StockMouvementRepository stockMouvementRepository, StockQueryValidator stockQueryValidator) {
        this.ingredientRepository = ingredientRepository;
        this.stockMouvementRepository = stockMouvementRepository;
        this.stockQueryValidator = stockQueryValidator;
    }

    public List<Ingredient> findAllIngredient () throws SQLException {
        return ingredientRepository.findIngredient();
    }

    public Ingredient findIngredientById(int id) throws SQLException {
        Ingredient ingredient = ingredientRepository.findIngredientById(id);
        if(ingredient == null){
            throw new IngredientNotFoundException(id);
        }
        return ingredient;
    }

    public StockValue getStockValueAT(Integer ingredientID, Instant at, UnitType unit) throws SQLException {
        stockQueryValidator.validateStockQuery(ingredientID, at, unit);

        if(ingredientRepository.findIngredientById(ingredientID) == null){
            throw new IngredientNotFoundException(ingredientID);
        }

        StockValue stockValue = stockMouvementRepository.getStockValueAt(ingredientID, at);

        if(unit != UnitType.KG){
            double converted = UnitConversion.fromKG(
                    ingredientRepository.findIngredientById(ingredientID).getName(),
                    stockValue.getQuantity(),
                    unit
            );
            return new  StockValue(converted, unit);
        }
        return new  StockValue(stockValue.getQuantity(), stockValue.getUnit());
    }

    public void addStockMovements(Integer ingredientId, List<StockMouvementCreateDTO> movements) throws SQLException {
        if (movements == null || movements.isEmpty()) {
            throw new IllegalArgumentException("La liste des mouvements ne peut pas être vide");
        }

        if (ingredientRepository.findIngredientById(ingredientId) == null) {
            throw new IngredientNotFoundException(ingredientId);
        }

        stockMouvementRepository.addStockMovements(ingredientId, movements);
    }
}
