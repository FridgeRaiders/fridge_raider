package com.example.foodproject.service;

import com.example.foodproject.dto.RecipeDTO;
import com.example.foodproject.model.Recipe;
import com.example.foodproject.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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

        Set<String> seenNames = new java.util.HashSet<>();

        return ingredients.stream()
                .flatMap(ingredient ->
                        recipeRepository.findByIngredientsContaining(ingredient).stream()
                )
                .distinct()
                .filter(recipe -> seenNames.add(recipe.getName()))
                .map(recipe -> {
                    int score = calculateMatchScore(recipe.getIngredients(), ingredients);
                    return new RecipeDTO(
                            recipe.getId(),
                            recipe.getName(),
                            recipe.getDescription(),
                            recipe.getImageLink(),
                            recipe.getIngredients(),
                            recipe.getNutrients(),
                            recipe.getSteps(),
                            recipe.getServings(),
                            recipe.getPrepTime(),
                            recipe.getCookTime(),
                            recipe.getIsBudget(),
                            recipe.getDifficulty(),
                            score
                    );
                })
                .sorted((a, b) -> Integer.compare(b.matchScore(), a.matchScore()))
                .collect(Collectors.toList());
    }
    // Count how many selected ingredients appear in the recipe text
    private int calculateMatchScore(String recipeIngredients, List<String> selectedIngredients) {
        if (recipeIngredients == null || recipeIngredients.isBlank()) return 0;

        List<String> ingredientList = Arrays.stream(recipeIngredients.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        long matches = ingredientList.stream()
                .filter(recipeIngredient ->
                        selectedIngredients.stream()
                                .anyMatch(selected -> recipeIngredient.toLowerCase().contains(selected.toLowerCase()))
                )
                .count();

        if (ingredientList.isEmpty()) return 0;
        return (int) Math.round((matches * 100.0) / ingredientList.size());
    }

    // needed for SavedController.java
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Recipe not found with ID: " + id));
    }

    public List<RecipeDTO> getFeedRecipes(int offset, int limit) {
        return recipeRepository.findRandom(limit, offset)
                .stream()
                .map(recipe -> new RecipeDTO(
                        recipe.getId(),
                        recipe.getName(),
                        recipe.getDescription(),
                        recipe.getImageLink(),
                        recipe.getIngredients(),
                        recipe.getNutrients(),
                        recipe.getSteps(),
                        recipe.getServings(),
                        recipe.getPrepTime(),
                        recipe.getCookTime(),
                        recipe.getIsBudget(),
                        recipe.getDifficulty(),
                        0
                ))
                .collect(Collectors.toList());
    }
}