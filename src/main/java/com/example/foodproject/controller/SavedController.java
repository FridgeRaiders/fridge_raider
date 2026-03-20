//package com.example.foodproject.controller;
//
//
//import com.example.foodproject.model.Recipe;
//import com.example.foodproject.model.User;
//import com.example.foodproject.service.RecipeService;
//import com.example.foodproject.service.SavedService;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//
//@Controller
//public class SavedController {
//
//    private final SavedService savedService;
//    private final RecipeService recipeService;
//    private final UserRepository userRepository;
//
//    public SavedController(SavedService savedService,
//                           RecipeService recipeService,
//                           UserRepository userRepository) {
//        this.savedService = savedService;
//        this.recipeService = recipeService;
//        this.userRepository = userRepository;
//    }
//
//    // save recipe
//    @PostMapping("/recipes/{id}/save")
//    public String saveRecipe(@PathVariable Long id,
//                             @AuthenticationPrincipal OidcUser oidcUser) {
//
//        // find recipe that user is trying to save
//
//        // find or create corresponding user
//
//        // save recipe for this user
//
//        // redirect user back to the page for that recipe
//
//    }
//
//    // remove saved recipe
//    @PostMapping("/recipes/{id}/unsave")
//    public String unsaveRecipe(@PathVariable Long id,
//                               @AuthenticationPrincipal OidcUser oidcUser) {
//
//        // find recipe that user is trying to remove from saved
//
//        // find or create corresponding user
//
//        // remove saved recipe
//
//        // redirect user back to the page for that recipe
//    }
//
//    // show all saved recipes
//    @GetMapping("/saved")
//    public String viewSavedRecipes(@AuthenticationPrincipal OidcUser oidcUser,
//                                   Model model) {
//
//        // find or create corresponding user
//
//
//        // add list of saved recipes to the model (for Thymeleaf)
//
//        // return Thymeleaf template name
//    }
//
//
//}
