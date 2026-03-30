package school.hei.spring_restaurant.DTO;

import java.util.List;

public class DishDTO {
    private int id;
    private String name;
    private Double sellingPrice;
    private List<IngredientDTO> ingredients;

    public DishDTO(int id, String name, Double sellingPrice, List<IngredientDTO> ingredients) {
        this.id = id;
        this.name = name;
        this.sellingPrice = sellingPrice;
        this.ingredients= ingredients;
    }

    public int getId(){ return id; }
    public String getName(){ return name; }
    public Double getSellingPrice(){ return sellingPrice; }
    public List<IngredientDTO> getIngredients() { return ingredients; }
}