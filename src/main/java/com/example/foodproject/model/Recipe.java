package com.example.foodproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
//@Data
@Getter
@Setter
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

    private Short servings;

    private Short prep_time;

    private Short cook_time;

    private Boolean is_budget;

    @JsonIgnore // added to prevent recursion error
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}




