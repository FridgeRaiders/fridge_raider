package com.example.foodproject.controller;

import com.example.foodproject.dto.RecipeDTO;
import com.example.foodproject.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class FeedController {

    private final RecipeService recipeService;

    public FeedController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/feed")
    public ModelAndView feedPage() {
        return new ModelAndView("feed");
    }

    @GetMapping("/feed/recipes")
    @ResponseBody
    public ResponseEntity<List<RecipeDTO>> getFeedRecipes(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(recipeService.getFeedRecipes(offset, limit));
    }
}