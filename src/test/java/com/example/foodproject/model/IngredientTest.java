package com.example.foodproject.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IngredientTest {
        private Ingredient validIngredient = new Ingredient("tomato");

        @Test
        public void ingredientCreatedAndAdded() {
            System.out.println(validIngredient);
            assertThat(validIngredient.getName(), containsString("tomato"));
        }

    }

