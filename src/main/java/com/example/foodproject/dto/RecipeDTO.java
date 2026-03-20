package com.example.foodproject.dto;

public record RecipeDTO(
        Long id,
        String name,
        String description,
        String image,
        String ingredients,
        String nutrients,
        String steps,
        Short servings,
        Short prepTime,
        Short cookTime,
        Boolean isBudget,
        String difficulty,
        int matchScore
) {}