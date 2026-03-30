package school.hei.spring_restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.spring_restaurant.DTO.DishDTO;
import school.hei.spring_restaurant.service.DishService;

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
            e.printStackTrace(); // ← ajoute cette ligne
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage()); // ← retourne le message d'erreur
        }
    }
}