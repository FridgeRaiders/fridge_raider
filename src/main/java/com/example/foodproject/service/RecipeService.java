package com.example.foodproject.service;

import com.example.foodproject.dto.RecipeDTO;
import com.example.foodproject.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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

        if (ingredients == null || ingredients.isEmpty()) {
            return Collections.emptyList();
        }

        return ingredients.stream()
                .flatMap(ingredient ->
                        recipeRepository.findByIngredientsContaining(ingredient).stream()
                )
                .distinct()
                .map(recipe -> {
                    int score = calculateMatchScore(recipe.getIngredients(), ingredients);
                    return new RecipeDTO(
                            recipe.getId(),
                            recipe.getDescription(),
                            recipe.getIngredients(),
                            recipe.getNutrients(),
                            recipe.getServings(),
                            recipe.getPrepTime(),
                            recipe.getCookTime(),
                            recipe.getIsBudget(),
                            score
                    );
                })
                // Sort by match score descending — highest match appears first
                .sorted((a, b) -> Integer.compare(b.matchScore(), a.matchScore()))
                .collect(Collectors.toList());
    }

    // Count how many selected ingredients appear in the recipe text
    private int calculateMatchScore(String recipeIngredients, List<String> selectedIngredients) {
        if (recipeIngredients == null || recipeIngredients.isBlank()) return 0;

        String lowerRecipe = recipeIngredients.toLowerCase();

        // Split the recipe ingredients text into individual ingredients
        String[] recipeIngredientList = lowerRecipe.split(",");

        // Count how many recipe ingredients the user has
        long matches = Arrays.stream(recipeIngredientList)
                .filter(recipeIngredient ->
                        selectedIngredients.stream()
                                .anyMatch(selected -> recipeIngredient.contains(selected.toLowerCase()))
                )
                .count();

        // Percentage of the recipe's ingredients that the user has
        return (int) Math.round((matches * 100.0) / recipeIngredientList.length);
    }
}