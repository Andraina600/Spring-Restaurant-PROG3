package school.hei.spring_restaurant.service;

import org.springframework.stereotype.Service;
import school.hei.spring_restaurant.DTO.DishDTO;
import school.hei.spring_restaurant.DTO.IngredientDTO;
import school.hei.spring_restaurant.repository.DishRepository;

import java.sql.SQLException;
import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<DishDTO> getAllDishes() throws SQLException {
        return dishRepository.findAllWithIngredients()
                .stream()
                .map(dish -> {
                    List<IngredientDTO> ingredients = dish.getCompositions()
                            .stream()
                            .map(comp -> new IngredientDTO(
                                    comp.getIngredient().getId(),
                                    comp.getIngredient().getName(),
                                    comp.getIngredient().getCategory().name(), // CategoryEnum → String
                                    comp.getIngredient().getPrice()
                            ))
                            .toList();

                    return new DishDTO(
                            dish.getId(),
                            dish.getName(),
                            dish.getPrice(),
                            ingredients
                    );
                })
                .toList();
    }
}