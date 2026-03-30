package school.hei.spring_restaurant.service;

import org.springframework.stereotype.Service;
import school.hei.spring_restaurant.entity.Dish;
import school.hei.spring_restaurant.repository.DishRepository;

import java.sql.SQLException;
import java.util.List;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> getAllDishes() throws SQLException {
        return dishRepository.findAllWithIngredients();
    }
}
