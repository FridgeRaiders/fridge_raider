package com.example.foodproject.service;

import com.example.foodproject.dto.RecipeDTO;
import com.example.foodproject.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                // Sort by match score descending — highest match appears first
                .sorted((a, b) -> Integer.compare(b.matchScore(), a.matchScore()))
                .collect(Collectors.toList());
    }

    // Count how many selected ingredients appear in the recipe text
    private int calculateMatchScore(String recipeIngredients, List<String> selectedIngredients) {
        if (recipeIngredients == null || recipeIngredients.isBlank()) return 0;

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> ingredientList = mapper.readValue(
                    recipeIngredients,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            long matches = ingredientList.stream()
                    .filter(recipeIngredient ->
                            selectedIngredients.stream()
                                    .anyMatch(selected -> recipeIngredient.toLowerCase().contains(selected.toLowerCase()))
                    )
                    .count();

            return (int) Math.round((matches * 100.0) / ingredientList.size());

        } catch (Exception e) {
            return 0;
        }
    }
}