package com.example.foodproject.dto;

public record RecipeDTO(
        Long id,
        String description,
        String ingredients,
        String nutrients,
        Short servings,
        Short prepTime,
        Short cookTime,
        Boolean isBudget
) {}