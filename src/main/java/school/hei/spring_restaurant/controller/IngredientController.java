package school.hei.spring_restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.spring_restaurant.entity.Ingredient;
import school.hei.spring_restaurant.service.IngredientService;


import java.util.List;

@RestController
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredient")
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

    @GetMapping("/ingredient/{id}")
    public ResponseEntity<Object> findIngredientById(@PathVariable int id){
        try {

            Ingredient ingredient = ingredientService.findIngredientById(id);
            if(ingredient == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("L'ingredient avec l'id " + id + " n'est pas préésent");
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

}
