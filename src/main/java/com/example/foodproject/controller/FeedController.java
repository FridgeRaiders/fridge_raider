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
public class FeedController {

    @GetMapping("/feed")
    public ModelAndView feedPage() {
        return new ModelAndView("/feed");
    }
}
