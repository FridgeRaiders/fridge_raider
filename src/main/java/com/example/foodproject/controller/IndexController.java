package com.example.foodproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@RestController
public class IndexController{

    @GetMapping("/")
    public ModelAndView indexPage() {
        return new ModelAndView("/Index");
    }

}