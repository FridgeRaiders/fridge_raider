package com.example.foodproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RECIPES")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String ingredients;

    private String nutrients;

    private Integer servings;

    private Integer prep_time;

    private Integer cook_time;

    private Boolean is_budget;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}




