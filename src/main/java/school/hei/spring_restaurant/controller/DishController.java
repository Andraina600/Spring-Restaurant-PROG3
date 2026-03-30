package school.hei.spring_restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import school.hei.spring_restaurant.entity.Dish;
import school.hei.spring_restaurant.repository.DishRepository;
import school.hei.spring_restaurant.service.DishService;

import java.sql.SQLException;
import java.util.List;

@RestController
public class DishController {
    private DishService  dishService;
    public DishController (DishService dishService){
        this.dishService = dishService;
    }

    @GetMapping("/dishes")
    public ResponseEntity<?> getAllDishes(){
            try{
                List<Dish> dishes = dishService.getAllDishes();
                if(!dishes.isEmpty()){
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(dishes);
                }
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }catch(Exception e){
                throw  new RuntimeException(e);
            }
    }
}
