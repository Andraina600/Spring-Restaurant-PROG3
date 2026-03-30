package school.hei.spring_restaurant.repository;

import org.springframework.stereotype.Repository;
import school.hei.spring_restaurant.config.DataSource;
import school.hei.spring_restaurant.entity.StockValue;
import school.hei.spring_restaurant.type.UnitType;

import java.sql.*;
import java.time.Instant;
@Repository

public class StockRepository {
    private final DataSource dataSource;

    public StockRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public StockValue getStockValueAt(Integer ingredientID, Instant t){
        String sql = """
                    SELECT SUM(
                    CASE 
                        WHEN type_mouvement = 'IN' THEN quantity
                        ELSE quantity * (-1)
                    END) as actual_quantity,
                    unit as unit_type
                    FROM stock_mouvement
                    WHERE id_ingredient = ?
                    AND creation_datetime <= ?
                    GROUP BY  unit_type""";
        try(Connection conn = dataSource.getConnection();){
            PreparedStatement ps = conn.prepareStatement(sql);{
                ps.setInt(1, ingredientID);
                ps.setTimestamp(2, Timestamp.from(t));
            }
            try (ResultSet rs = ps.executeQuery()) {
                double total = 0.0;
                UnitType unit = UnitType.KG;
                while(rs.next()) {
                    total += rs.getDouble("actual_quantity");
                    unit = UnitType.valueOf(rs.getString("unit_type"));
                }
                return new StockValue(Math.max(0, total), unit);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
