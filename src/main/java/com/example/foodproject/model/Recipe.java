package com.example.foodproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String ingredients;

    private String nutrients;

    private Short servings;

    @Column(name = "prep_time")
    private Short prepTime;

    @Column(name = "cook_time")
    private Short cookTime;

    @Column(name = "is_budget")
    private Boolean isBudget;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}