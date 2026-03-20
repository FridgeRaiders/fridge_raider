package com.example.foodproject.repository;


import com.example.foodproject.model.Recipe;
import com.example.foodproject.model.Saved;
import com.example.foodproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedRepository extends JpaRepository<Saved, Long> {

    // check if a saved record already exists for a given user and recipe
    // already saved = true, not saved yet = false
    boolean existsByUserAndRecipe(User user, Recipe recipe);

    // find a specific saved record for a given user and recipe
    // Optional<Saved> = might contain a Saved object, or might be empty if nothing was found
    Optional<Saved> findByUserAndRecipe(User user, Recipe recipe);

    // find all saved records belonging to one user
    List<Saved> findByUser(User user);

}

