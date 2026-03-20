package com.example.foodproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "saved_recipes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "recipe_id"})
)

public class Saved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // many saves to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User model has not been created yet, hence the error

    // many saves to one recipe
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe; // Recipe model has not been created yet, hence the error

    public Saved(User user, Recipe recipe) {
        this.user = user;
        this.recipe = recipe;
    }

}

