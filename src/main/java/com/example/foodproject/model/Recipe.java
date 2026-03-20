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

    private String name;

    private String description;

    private String image;

    @Column(columnDefinition = "jsonb")
    private String ingredients;

    @Column(columnDefinition = "jsonb")
    private String nutrients;

    @Column(columnDefinition = "jsonb")
    private String steps;

    private Short servings;

    @Column(name = "prep_time")
    private Short prepTime;

    @Column(name = "cook_time")
    private Short cookTime;

    @Column(name = "is_budget")
    private Boolean isBudget;

    private Integer difficulty;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}