package school.hei.spring_restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.hei.spring_restaurant.DTO.StockMouvementCreateDTO;
import school.hei.spring_restaurant.DTO.StockMouvementDTO;
import school.hei.spring_restaurant.entity.StockMouvement;
import school.hei.spring_restaurant.service.StockMouvementService;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ingredients")
public class StockMouvementController {
    private final StockMouvementService service;

    public StockMouvementController(StockMouvementService service) {
        this.service = service;
    }

    @GetMapping("/{id}/stockMovements")
    public ResponseEntity<List<StockMouvementDTO>> getStockMovements(
            @PathVariable int id,
            @RequestParam Instant from,
            @RequestParam Instant to) {

        try {
            List<StockMouvement> mouvements = service.getByIngredientAndDateRange(id, from, to);

            List<StockMouvementDTO> dtos = mouvements.stream()
                    .map(sm -> new StockMouvementDTO(
                            sm.getId(),
                            sm.getCreationDatetime(),
                            sm.getValue().getUnit(),
                            sm.getValue().getQuantity(),
                            sm.getType()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/stockMovements")
    public ResponseEntity<?> addStockMovements(
            @PathVariable int id,
            @RequestBody List<StockMouvementCreateDTO> movements) {

        try{
            List<StockMouvementDTO> created = service.addStockMovements(id, movements);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur au niveau server");
        }
    }
}
