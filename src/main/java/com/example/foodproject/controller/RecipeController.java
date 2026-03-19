package com.example.foodproject.controller;

import com.example.foodproject.model.Recipe;
import com.example.foodproject.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller //temporary, change to rest controller once returning views
public class RecipeController {
    @Autowired
    RecipeRepository recipeRepository;


    @GetMapping("/recipes")
    @ResponseBody
    public Iterable getRecipes(){
    Iterable recipes = recipeRepository.findAll();
    return recipes;
    }

    @PostMapping("/recipes") // creating a new recipe
    @ResponseBody
    public Recipe createNewRecipe(@RequestBody Recipe newRecipe){
        return recipeRepository.save(newRecipe);
    }

    @GetMapping("/recipes/{displayName}")
    @ResponseBody
    public Iterable<Recipe> getRecipesByDisplayName(@PathVariable String displayName) {
        System.out.println("DISPLAY NAMEEEEEEEEE");
        System.out.println(displayName);
        //System.out.println(recipeRepository.getRecipesByUser_Id(1L));
        return recipeRepository.getRecipesByUser_Id(1L);
    }






}
