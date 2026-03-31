package school.hei.spring_restaurant.repository;

import org.springframework.stereotype.Repository;
import school.hei.spring_restaurant.DTO.StockMouvementCreateDTO;
import school.hei.spring_restaurant.DTO.StockMouvementDTO;
import school.hei.spring_restaurant.config.DataSource;
import school.hei.spring_restaurant.entity.StockMouvement;
import school.hei.spring_restaurant.entity.StockValue;
import school.hei.spring_restaurant.type.MouvementType;
import school.hei.spring_restaurant.type.UnitType;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Repository

public class StockMouvementRepository {
    private final DataSource dataSource;

    public StockMouvementRepository(DataSource dataSource) {
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

    public boolean existsIngredient(int ingredientId) {
        String sql = "SELECT COUNT(*) FROM ingredient WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur DB lors de la vérification de l'ingrédient", e);
        }
        return false;
    }

    public List<StockMouvement> findByIngredientIdAndDateRange(
            int ingredientId, Instant from, Instant to) {

        String sql = """
        SELECT id,
               creation_datetime,
               quantity,
               unit,
               type_mouvement
        FROM stock_mouvement
        WHERE id_ingredient = ?    
          AND creation_datetime >= ?
          AND creation_datetime <= ?
        ORDER BY creation_datetime
        """;
        List<StockMouvement> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.from(from));
            ps.setTimestamp(3, Timestamp.from(to));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockMouvement sm = new StockMouvement();
                sm.setId(rs.getInt("id"));
                sm.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

                StockValue sv = new StockValue();
                sv.setQuantity(rs.getDouble("quantity"));
                sv.setUnit(UnitType.valueOf(rs.getString("unit")));
                sm.setValue(sv);

                sm.setType(MouvementType.valueOf(rs.getString("type_mouvement")));
                result.add(sm);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur DB lors de la récupération des mouvements", e);
        }

        return result;
    }


    public List<StockMouvement> addStockMovements(Integer ingredientId, List<StockMouvementCreateDTO> movements) throws SQLException {

        String sql = """
            INSERT INTO stock_mouvement
            (id_ingredient, quantity, unit, type_mouvement, creation_datetime)
            VALUES (?, ?, ?::unit_type, ?::type_mvt, ?)
            RETURNING id, creation_datetime, quantity, unit, type_mouvement
            """;

        List<StockMouvement> created = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (StockMouvementCreateDTO dto : movements) {
                Instant now = Instant.now();

                ps.setInt(1, ingredientId);
                ps.setDouble(2, dto.getQuantity());
                ps.setString(3, dto.getUnit().name());
                ps.setString(4, dto.getType().name());
                ps.setObject(5, LocalDateTime.ofInstant(now, ZoneOffset.UTC));

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    StockMouvement sm = new StockMouvement();
                    sm.setId(rs.getInt("id"));
                    sm.setCreationDatetime(
                            rs.getObject("creation_datetime", LocalDateTime.class)
                                    .toInstant(ZoneOffset.UTC)
                    );

                    StockValue sv = new StockValue();
                    sv.setQuantity(rs.getDouble("quantity"));
                    sv.setUnit(UnitType.valueOf(rs.getString("unit")));
                    sm.setValue(sv);

                    sm.setType(MouvementType.valueOf(rs.getString("type_mouvement")));
                    created.add(sm);
                }
            }
        }

        return created;
    }
}
