package school.hei.spring_restaurant.DTO;

import school.hei.spring_restaurant.type.MouvementType;
import school.hei.spring_restaurant.type.UnitType;

import java.time.Instant;

public class StockMouvementDTO {
    private int id;
    private Instant creationDatetime;
    private UnitType unit;
    private double quantity;
    private MouvementType type;

    public StockMouvementDTO(int id, Instant creationDatetime, UnitType unit, double quantity, MouvementType type) {
        this.id = id;
        this.creationDatetime = creationDatetime;
        this.unit = unit;
        this.quantity = quantity;
        this.type = type;
    }

    public int getId() { return id; }
    public Instant getCreationDatetime() { return creationDatetime; }
    public UnitType getUnit() { return unit; }
    public double getQuantity() { return quantity; }
    public MouvementType getType() { return type; }
}