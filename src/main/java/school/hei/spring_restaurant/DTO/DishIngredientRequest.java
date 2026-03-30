package school.hei.spring_restaurant.DTO;

public class DishIngredientRequest {
    private int id;
    private double quantityRequired;
    private String unit;

    public int getId(){ return id; }
    public double getQuantityRequired(){ return quantityRequired; }
    public String getUnit(){ return unit; }

    public void setId(int id){ this.id = id; }
    public void setQuantityRequired(double q){ this.quantityRequired = q; }
    public void setUnit(String unit){ this.unit = unit; }
}
