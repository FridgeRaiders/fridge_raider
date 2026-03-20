package com.example.foodproject.repository;

import org.springframework.stereotype.Repository;
import com.example.foodproject.model.Ingredient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByNameContainingIgnoreCase(String query);
}