package school.hei.spring_restaurant.repository;

import school.hei.spring_restaurant.DTO.DishIngredientRequest;
import school.hei.spring_restaurant.entity.Dish;
import school.hei.spring_restaurant.entity.DishIngredient;
import school.hei.spring_restaurant.entity.Ingredient;
import school.hei.spring_restaurant.type.CategoryEnum;
import school.hei.spring_restaurant.type.DishTypeEnum;
import org.springframework.stereotype.Repository;
import school.hei.spring_restaurant.type.UnitType;
import school.hei.spring_restaurant.config.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DishRepository {
    private final DataSource dataSource;

    public DishRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Dish> findAllWithIngredients() throws SQLException {
        String sql = """
            SELECT 
                d.id AS dish_id,
                d.name AS dish_name,
                d.dish_type,
                d.selling_price,
                i.id AS ing_id,
                i.name AS ing_name,
                i.price AS ing_price,
                i.category,
                di.quantity_required,
                di.unit
            FROM dish d
            LEFT JOIN dishingredient di ON d.id = di.id_dish
            LEFT JOIN ingredient i ON di.id_ingredient = i.id
            ORDER BY d.id, i.id
            """;

        List<Dish> dishes = new ArrayList<>();
        Dish currentDish = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int dishId = rs.getInt("dish_id");

                if (currentDish == null || currentDish.getId() != dishId) {
                    currentDish = new Dish();
                    currentDish.setId(dishId);
                    currentDish.setName(rs.getString("dish_name"));
                    currentDish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    currentDish.setSellingPrice(
                            rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null
                    );
                    currentDish.setCompositions(new ArrayList<>());
                    dishes.add(currentDish);
                }

                if (rs.getObject("ing_id") != null) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("ing_id"));
                    ingredient.setName(rs.getString("ing_name"));
                    ingredient.setPrice(rs.getDouble("ing_price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setIngredient(ingredient);
                    dishIngredient.setQuantityRequired(rs.getDouble("quantity_required"));
                    dishIngredient.setUnit(UnitType.valueOf(rs.getString("unit")));


                    currentDish.getCompositions().add(dishIngredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }

    public Dish findById(Integer id) {
        String sql = """
            SELECT 
                d.id AS dish_id,
                d.name AS dish_name,
                d.dish_type,
                d.selling_price,
                i.id AS ing_id,
                i.name AS ing_name,
                i.price AS ing_price,
                i.category,
                di.quantity_required,
                di.unit
            FROM dish d
            LEFT JOIN dishingredient di ON d.id = di.id_dish
            LEFT JOIN ingredient i ON di.id_ingredient = i.id
            WHERE d.id = ?
            ORDER BY i.id
            """;

        Dish dish = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (dish == null) {
                        dish = new Dish();
                        dish.setId(rs.getInt("dish_id"));
                        dish.setName(rs.getString("dish_name"));
                        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                        dish.setSellingPrice(
                                rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null
                        );
                        dish.setCompositions(new ArrayList<>());
                    }

                    if (rs.getObject("ing_id") != null) {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setId(rs.getInt("ing_id"));
                        ingredient.setName(rs.getString("ing_name"));
                        ingredient.setPrice(rs.getDouble("ing_price"));
                        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                        DishIngredient dishIngredient = new DishIngredient();
                        dishIngredient.setIngredient(ingredient);
                        dishIngredient.setQuantityRequired(rs.getDouble("quantity_required"));
                        dishIngredient.setUnit(UnitType.valueOf(rs.getString("unit")));

                        dish.getCompositions().add(dishIngredient);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dish;
    }

    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM dish WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> findExistingIngredientIds(List<Integer> ids) {
        if (ids.isEmpty()) return new ArrayList<>();
        String placeholders = ids.stream().map(i -> "?").collect(Collectors.joining(", "));
        String sql = "SELECT id FROM ingredient WHERE id IN (" + placeholders + ")";
        List<Integer> existingIds = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                existingIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return existingIds;
    }

    public void deleteAllIngredientsByDishId(Integer dishId) {
        String sql = "DELETE FROM dishingredient WHERE id_dish = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertDishIngredients(Integer dishId, List<DishIngredientRequest> ingredients) {
        String sql = "INSERT INTO dishingredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredientRequest ing : ingredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, ing.getId());
                ps.setDouble(3, ing.getQuantityRequired());
                ps.setString(4, ing.getUnit());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}