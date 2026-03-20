package com.example.foodproject.service;


import com.example.foodproject.model.Recipe;
import com.example.foodproject.model.Saved;
import com.example.foodproject.model.User;
import com.example.foodproject.repository.SavedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavedService {

    private final SavedRepository savedRepository;

    public SavedService(SavedRepository savedRepository) {
        this.savedRepository = savedRepository;
    }

    // save a recipe:
    public void saveRecipe(User user, Recipe recipe) {
        // check if already saved
        boolean alreadySaved = savedRepository.existsByUserAndRecipe(user, recipe);
        // if already saved, do nothing
        if (alreadySaved) {
            return;
        }
        // create new Saved entity
        Saved saved = new Saved(user, recipe);
        // save to database
        savedRepository.save(saved);
    }

    // remove a saved recipe:
    public void removeSavedRecipe(User user, Recipe recipe) {
        // find saved record
        Optional<Saved> savedOptional = savedRepository.findByUserAndRecipe(user, recipe);
        // if found, delete it
        savedOptional.ifPresent(savedRepository::delete);
    }

    // get all saved recipes
    public List<Saved> getSavedRecipes(User user) {
        return savedRepository.findByUser(user);
    }

    // check if a recipe is already saved (could use for UI, e.g. a save/saved button)
    public boolean isRecipeSaved(User user, Recipe recipe) {
        return savedRepository.existsByUserAndRecipe(user, recipe);
    }

}
