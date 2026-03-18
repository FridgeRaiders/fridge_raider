package com.example.foodproject.controller;

import com.example.foodproject.dto.IngredientDTO;
import com.example.foodproject.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;


@Controller
public class IndexController{

    private final IngredientService ingredientService;

    public IndexController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
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
}