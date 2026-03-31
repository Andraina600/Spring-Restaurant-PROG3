package school.hei.spring_restaurant.DTO;

import school.hei.spring_restaurant.type.MouvementType;
import school.hei.spring_restaurant.type.UnitType;

import java.time.Instant;

public class StockMouvementCreateDTO {
    private double quantity;
    private UnitType unit;
    private MouvementType type;
    private Instant creationDatetime;

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public UnitType getUnit() { return unit; }
    public void setUnit(UnitType unit) { this.unit = unit; }

    public MouvementType getType() { return type; }
    public void setType(MouvementType type) { this.type = type; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }
}
