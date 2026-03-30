package school.hei.spring_restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.spring_restaurant.DTO.DishDTO;
import school.hei.spring_restaurant.exception.DishNotFoundException;
import school.hei.spring_restaurant.service.DishService;
import school.hei.spring_restaurant.DTO.DishIngredientRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping("/dishes")
    public ResponseEntity<?> getAllDishes() {
        try {
            List<DishDTO> dishes = dishService.getAllDishes();
            return ResponseEntity.ok(dishes);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur au niveau du server");
        }
    }

    @PutMapping("/dishes/{id}/ingredients")
    public ResponseEntity<?> updateDishIngredients(
            @PathVariable int id,
            @RequestBody(required = false) List<DishIngredientRequest> ingredients
    ) {
        if (ingredients == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Le corps de la requête est obligatoire.");
        }

        try {
            List<DishDTO> updatedDishes = dishService.updateDishIngredients(id, ingredients);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updatedDishes);

        } catch (DishNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}