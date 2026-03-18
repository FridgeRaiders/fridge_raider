package com.example.foodproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String display_name;

    @OneToMany(mappedBy = "user")
    private List<Recipe> recipes;

    public User(String email, String display_name) {
        this.email = email;
        this.display_name = display_name;
    }

    public User(String email){
        this.email = email;
    }
}
