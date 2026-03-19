package com.example.foodproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name="display_name", unique = true, nullable = true) // unique set to true, like a twitter @
    private String displayName;

    @OneToMany(mappedBy = "user")
    private List<Recipe> recipes;

    public User(String email, String display_name) {
        this.email = email;
        this.displayName = display_name;
    }

    public User(String email){
        this.email = email;
    }
}
