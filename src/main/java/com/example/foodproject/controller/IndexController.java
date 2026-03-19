package com.example.foodproject.controller;

import com.example.foodproject.dto.IngredientDTO;
import com.example.foodproject.dto.RecipeDTO;
import com.example.foodproject.service.IngredientService;
import com.example.foodproject.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class IndexController{

    private final IngredientService ingredientService;
    private final RecipeService recipeService;

    public IndexController(IngredientService ingredientService, RecipeService recipeService) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
    }

    @GetMapping("/")
    public ModelAndView indexPage() {
        return new ModelAndView("/Index");
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<IngredientDTO>> search(@RequestParam String query) {
        return ResponseEntity.ok(ingredientService.searchIngredients(query));
    }

    @GetMapping("/recipes/search")
    @ResponseBody
    public ResponseEntity<List<RecipeDTO>> searchRecipes(@RequestParam String ingredients) {

        // Split the comma-separated string into a list
        // e.g. "chicken,garlic,lemon" → ["chicken", "garlic", "lemon"]
        List<String> ingredientList = Arrays.stream(ingredients.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        return ResponseEntity.ok(recipeService.searchByIngredients(ingredientList));
    }
}