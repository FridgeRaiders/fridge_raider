package com.example.foodproject.controller;

import com.example.foodproject.model.Recipe;
import com.example.foodproject.model.User;
import com.example.foodproject.repository.UserRepository;
import com.example.foodproject.service.RecipeService;
import com.example.foodproject.service.SavedService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SavedController {

    private final SavedService savedService;
    private final RecipeService recipeService;
    private final UserRepository userRepository;

    public SavedController(SavedService savedService,
                           RecipeService recipeService,
                           UserRepository userRepository) {
        this.savedService = savedService;
        this.recipeService = recipeService;
        this.userRepository = userRepository;
    }

    // save recipe
    @PostMapping("/recipes/{id}/save")
    public String saveRecipe(@PathVariable Long id,
                             @AuthenticationPrincipal OidcUser oidcUser) {

        // find recipe that user is trying to save
        Recipe recipe = recipeService.getRecipeById(id);
        // find or create corresponding user
        User user = getCurrentUser(oidcUser);
        // save recipe for this user
        savedService.saveRecipe(user, recipe);
        // redirect user back to the page for that recipe
        return "redirect:/recipes/" + id;
    }

    // remove saved recipe
    @PostMapping("/recipes/{id}/unsave")
    public String unsaveRecipe(@PathVariable Long id,
                               @AuthenticationPrincipal OidcUser oidcUser) {

        // find recipe that user is trying to remove from saved
        Recipe recipe = recipeService.getRecipeById(id);
        // find or create corresponding user
        User user = getCurrentUser(oidcUser);
        // remove saved recipe
        savedService.removeSavedRecipe(user, recipe);
        // redirect user back to the page for that recipe
        return "redirect:/recipes/" + id;
    }

    // show all saved recipes
    @GetMapping("/saved")
    public String viewSavedRecipes(@AuthenticationPrincipal OidcUser oidcUser,
                                   Model model) {

        // find user
        User user = getCurrentUser(oidcUser);
        // add list of saved recipes to the model (for Thymeleaf)
        model.addAttribute("savedRecipes", savedService.getSavedRecipes((user)));
        // return Thymeleaf template name
        return "saved-recipes";
    }

    // return IDs of all saved recipes
    @GetMapping("/saved/ids")
    @ResponseBody
    public List<Long> getSavedRecipeIds(@AuthenticationPrincipal OidcUser oidcUser) {

        // Find the logged-in application user
        User user = getCurrentUser(oidcUser);

        // Return the IDs of recipes this user has saved
        return savedService.getSavedRecipeIds(user);
    }

    // Toggle save/unsave — returns JSON for JS fetch calls
    @PostMapping("/recipes/{id}/toggle-save")
    @ResponseBody
    public java.util.Map<String, Object> toggleSave(@PathVariable Long id,
                                                    @AuthenticationPrincipal OidcUser oidcUser) {
        Recipe recipe = recipeService.getRecipeById(id);
        User user = getCurrentUser(oidcUser);
        boolean isSaved = savedService.isRecipeSaved(user, recipe);
        if (isSaved) {
            savedService.removeSavedRecipe(user, recipe);
        } else {
            savedService.saveRecipe(user, recipe);
        }
        return java.util.Map.of("saved", !isSaved);
    }

    // Check if a recipe is saved — returns JSON for JS fetch calls
    @GetMapping("/recipes/{id}/saved")
    @ResponseBody
    public java.util.Map<String, Object> isSaved(@PathVariable Long id,
                                                 @AuthenticationPrincipal OidcUser oidcUser) {
        Recipe recipe = recipeService.getRecipeById(id);
        User user = getCurrentUser(oidcUser);
        return java.util.Map.of("saved", savedService.isRecipeSaved(user, recipe));
    }

    // method to find current user
    private User getCurrentUser(OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        return userRepository.findUserByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setDisplayName(email);
                    return userRepository.save(newUser);
                });
    }

}
