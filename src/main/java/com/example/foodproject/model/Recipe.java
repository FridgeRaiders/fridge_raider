package com.example.foodproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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

    private String name;

    private String ingredients;

    private String steps;

    private String difficulty;

    @Column(name = "image_link")
    private String imageLink;

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

    @JsonIgnore // added to prevent recursion error
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}