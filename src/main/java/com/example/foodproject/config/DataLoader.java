package com.example.foodproject.config;

import com.example.foodproject.repository.RecipeRepository;
import com.example.foodproject.repository.UserRepository;
import com.example.foodproject.utils.DataLoaderUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public DataLoader(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (recipeRepository.count() == 0) {
            DataLoaderUtils seeder = new DataLoaderUtils(userRepository, recipeRepository);
            seeder.seedRecipes();
            System.out.println("Recipes table seeded.");
        }
        else {
            System.out.println("Recipes table already seeded.");
        }
    }
}
