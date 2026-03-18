package com.example.foodproject.service;

import com.example.foodproject.dto.IngredientDTO;
import com.example.foodproject.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<IngredientDTO> searchIngredients(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }

        return ingredientRepository
                .findByNameContainingIgnoreCase(query.trim())
                .stream()
                .limit(10)
                .map(ingredient -> new IngredientDTO(ingredient.getId(), ingredient.getName()))
                .toList();
    }
}