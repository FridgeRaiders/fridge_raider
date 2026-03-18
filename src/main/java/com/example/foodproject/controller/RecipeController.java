package com.example.foodproject.controller;

import com.example.foodproject.model.Recipe;
import com.example.foodproject.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller //temporary, change to rest controller once returning views
public class RecipeController {
    @Autowired
    RecipeRepository recipeRepository;


    @GetMapping("/recipes")
    Iterable getRecipes(){
    Iterable recipes = recipeRepository.findAll();
    return recipes;
    }
}
