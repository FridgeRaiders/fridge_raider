package com.example.foodproject.repository;

import com.example.foodproject.model.Recipe;
import com.example.foodproject.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    List<Recipe> getRecipesByUser_DisplayName(String displayName);
    List<Recipe> getRecipesByUser_Id(Long userId);
}
