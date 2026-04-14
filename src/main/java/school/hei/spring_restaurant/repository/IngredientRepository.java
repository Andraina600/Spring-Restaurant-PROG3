package school.hei.spring_restaurant.repository;

import org.springframework.stereotype.Repository;
import school.hei.spring_restaurant.config.DataSource;
import school.hei.spring_restaurant.entity.Ingredient;
import school.hei.spring_restaurant.entity.StockMouvement;
import school.hei.spring_restaurant.entity.StockValue;
import school.hei.spring_restaurant.type.CategoryEnum;
import school.hei.spring_restaurant.type.MouvementType;
import school.hei.spring_restaurant.type.UnitType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngredientRepository {
    private final DataSource dataSource;

    public IngredientRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> findIngredient () throws SQLException {
        String sql = "SELECT i.id, i.name, i.price, i.category FROM ingredient i ";

        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredients.add(ingredient);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return ingredients;
    }


    public Ingredient findIngredientById(int id) {
        String sqlIngredient = """
            SELECT id, name, price, category
            FROM ingredient
            WHERE id = ?
            """;

        String sqlMovements = """
            SELECT id, quantity, unit, type_mouvement, creation_datetime
            FROM stock_mouvement
            WHERE id_ingredient = ?
            ORDER BY creation_datetime ASC
            """;

        try (Connection conn = dataSource.getConnection()) {
            Ingredient ingredient = null;

            try (PreparedStatement psIng = conn.prepareStatement(sqlIngredient)) {
                psIng.setInt(1, id);
                try (ResultSet rs = psIng.executeQuery()) {
                    if (rs.next()) {
                        ingredient = new Ingredient();
                        ingredient.setId(rs.getInt("id"));
                        ingredient.setName(rs.getString("name"));
                        ingredient.setPrice(rs.getDouble("price"));
                        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    } else {
                        return null;
                    }
                }
            }catch (SQLException e){
                throw new RuntimeException(e);
            }

            try (PreparedStatement psMvt = conn.prepareStatement(sqlMovements)) {
                psMvt.setInt(1, id);
                try (ResultSet rs = psMvt.executeQuery()) {
                    while (rs.next()) {
                            StockMouvement mvt = new StockMouvement();
                        mvt.setId(rs.getInt("id"));

                        StockValue value = new StockValue();
                        value.setQuantity(rs.getDouble("quantity"));
                        value.setUnit(UnitType.valueOf(rs.getString("unit")));
                        mvt.setValue(value);

                        mvt.setType(MouvementType.valueOf(rs.getString("type_mouvement")));
                        mvt.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());

                    }
                }
            }catch (SQLException e){
                throw new RuntimeException(e);
            }

            return ingredient;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


}
