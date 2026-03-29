package school.hei.spring_restaurant.service;

import org.springframework.stereotype.Service;
import school.hei.spring_restaurant.entity.Ingredient;
import school.hei.spring_restaurant.respository.IngredientRespository;

import java.sql.SQLException;
import java.util.List;

@Service
public class IngredientService {
    private final IngredientRespository ingredientRespository;

    public IngredientService(IngredientRespository ingredientRespository) {
        this.ingredientRespository = ingredientRespository;
    }

    public List<Ingredient> findAllIngredient () throws SQLException {
        return ingredientRespository.findIngredient();
    }

    public Ingredient findIngredientById(int id) throws SQLException {
        return ingredientRespository.findIngredientById(id);
    }


}
