package com.example.foodproject.repository;

import com.example.foodproject.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.foodproject.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query(value = "SELECT * FROM recipes WHERE " +
            "LOWER(ingredients) ~ LOWER(CONCAT('(^|[^a-z])', :ingredient, '([^a-z]|$)'))",
            nativeQuery = true)
    List<Recipe> findByIngredientsContaining(@Param("ingredient") String ingredient);
    List<Recipe> getRecipesByUser_DisplayName(String displayName);
    List<Recipe> getRecipesByUser_Id(Long userId);

    @Query(value = "SELECT * FROM recipes ORDER BY RANDOM() LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Recipe> findRandom(@Param("limit") int limit, @Param("offset") int offset);
}