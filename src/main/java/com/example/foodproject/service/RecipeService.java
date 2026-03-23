package com.example.foodproject.service;

import com.example.foodproject.dto.RecipeDTO;
import com.example.foodproject.model.Recipe;
import com.example.foodproject.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<RecipeDTO> searchByIngredients(List<String> ingredients) {

        // Guard clause — return empty if nothing was passed
        if (ingredients == null || ingredients.isEmpty()) {
            return Collections.emptyList();
        }

        return ingredients.stream()
                .flatMap(ingredient ->
                        recipeRepository.findByIngredientsContaining(ingredient).stream()
                )
                .distinct()                    // remove duplicate recipes
                .map(recipe -> new RecipeDTO(
                        recipe.getId(),
                        recipe.getDescription(),
                        recipe.getIngredients(),
                        recipe.getNutrients(),
                        recipe.getServings(),
                        recipe.getPrepTime(),
                        recipe.getCookTime(),
                        recipe.getIsBudget()
                ))
                .collect(Collectors.toList());
    }
    // needed for SavedController.java
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Recipe not found with ID: " + id));
    }


}