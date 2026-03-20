package com.example.foodproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name="display_name", unique = true, nullable = true) // unique set to true, like a twitter @
    private String displayName;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Recipe> recipes;

    public User(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public User(String email) {
        this.email = email;
    }
}