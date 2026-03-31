package school.hei.spring_restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import school.hei.spring_restaurant.DTO.StockMouvementCreateDTO;
import school.hei.spring_restaurant.entity.Ingredient;
import school.hei.spring_restaurant.entity.StockValue;
import school.hei.spring_restaurant.exception.IngredientNotFoundException;
import school.hei.spring_restaurant.exception.InvalidStockQueryException;
import school.hei.spring_restaurant.service.IngredientService;
import school.hei.spring_restaurant.type.UnitType;


import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@RestController
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredients")
    public ResponseEntity<?> getAllIngredients(){
        try{
            List<Ingredient> ingredients = ingredientService.findAllIngredient();
            if (ingredients.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body(ingredients);
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ingredients);
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur au niveau du serveur");
        }
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<Object> findIngredientById(@PathVariable int id){
        try {

            Ingredient ingredient = ingredientService.findIngredientById(id);
            if(ingredient == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Ingredient id not found");
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ingredient);

        }catch(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur au niveau du serveur");
        }
    }

    @GetMapping("/ingredients/{id}/stock")
    public ResponseEntity<?> getStockValue(
            @PathVariable int id,
            @RequestParam(required = false) Instant at,
            @RequestParam(required = false) UnitType unit
    ) {
        try {
            StockValue stockValue = ingredientService.getStockValueAT(id, at, unit);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(stockValue);

        } catch (InvalidStockQueryException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (IngredientNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur au niveau du serveur");
        }
    }
}
