package com.example.foodproject.utils;

import com.example.foodproject.model.Recipe;
import com.example.foodproject.model.User;
import com.example.foodproject.repository.RecipeRepository;
import com.example.foodproject.repository.UserRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataLoaderUtils {
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public DataLoaderUtils(UserRepository userRepository, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }


    public short parseTimeToMinutes(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return 0;
        }

        timeStr = timeStr.toLowerCase().trim();

        // Handle special cases
        if (timeStr.contains("no time") || timeStr.contains("none")) {
            return 0;
        }

        int totalMinutes = 0;

        String[] parts = timeStr.split(" ");

        for (int i = 0; i < parts.length; i++) {
            try {
                int value = Integer.parseInt(parts[i]);

                if (i + 1 < parts.length) {
                    String unit = parts[i + 1];

                    if (unit.startsWith("min")) {
                        totalMinutes += value;
                    } else if (unit.startsWith("hour") || unit.startsWith("hr")) {
                        totalMinutes += value * 60;
                    }
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // Convert to short safely

        return (short) totalMinutes;
    }

    public void seedRecipes() throws Exception {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("Json-Recipes/recipes.json");
        fileNames.add("Json-Recipes/inspiration.json");
        fileNames.add("Json-Recipes/health.json");
        fileNames.add("Json-Recipes/budget.json");
        fileNames.add("Json-Recipes/baking.json");
        JSONParser parser = new JSONParser();

        for (String file : fileNames) {

            JSONArray arrayOfRecipes = (JSONArray) parser.parse(new FileReader(file));
            try {
                User user = new User("goodfood@goodfood.com", "goodfood");
                userRepository.save(user);
            } catch (Exception ignored) {
            }
            User user = userRepository.findUserByDisplayName("goodfood").get();

            for (Object o : arrayOfRecipes) {
                Recipe newRecipe = new Recipe();
                JSONObject recipe = (JSONObject) o;
                String name = (String) recipe.get("name");
                String imageLink = (String) recipe.get("image");
                String description = (String) recipe.get("description");
                Short servings = Long.valueOf(recipe.get("serves").toString()).shortValue();
                String difficulty = (String) recipe.get("difficult");
                String ingredients = recipe.get("ingredients").toString();
                String trimmedIngredients = ingredients.substring(1, ingredients.length() - 1);
                String formattedIngredients = trimmedIngredients.replaceAll("\"", "");
                String steps = recipe.get("steps").toString();
                String trimmedSteps = steps.substring(1, steps.length() - 1);
                String formattedSteps = trimmedSteps.replaceAll("\"", "");
                String nutrients = recipe.get("nutrients").toString();
                String trimmedNutrients = nutrients.substring(1, nutrients.length() - 1);
                String formattedNutrients = trimmedNutrients.replaceAll("\"", "");
                JSONObject times = (JSONObject) recipe.get("times");
                Short prepTime = parseTimeToMinutes(times.get("Preparation").toString());
                Short cookTime = 0;
                try {
                    cookTime = parseTimeToMinutes(times.get("Cooking").toString());
                } catch (Exception ignored) {
                }
                boolean isBudget = Objects.equals(recipe.get("maincategory").toString(), "budget");
                newRecipe.setUser(user);
                newRecipe.setName(name);
                newRecipe.setDescription(description);
                newRecipe.setServings(servings);
                newRecipe.setIngredients(formattedIngredients);
                newRecipe.setImageLink(imageLink);
                newRecipe.setDifficulty(difficulty);
                newRecipe.setSteps(formattedSteps);
                newRecipe.setNutrients(formattedNutrients);
                newRecipe.setPrepTime(prepTime);
                newRecipe.setCookTime(cookTime);
                newRecipe.setIsBudget(isBudget);
                recipeRepository.save(newRecipe);
            }
        }
    }
}
