package com.example.foodproject.controller;


import com.example.foodproject.model.User;
import com.example.foodproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller //temporary, change to rest controller once returning views
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users/after-login") // adding the AuthO sign up as a user in the Users table - MUST KEEP
    public RedirectView afterLogin() {
        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        userRepository
                .findUserByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));

        return new RedirectView("/");
    }

    @GetMapping("/users/{displayName}") // getting user by display name - DONT HAVE TO KEEP
    @ResponseBody
    public Optional<User> findByDisplayName(@PathVariable String displayName){
        System.out.println(userRepository.findUserByDisplayName(displayName));
        return userRepository.findUserByDisplayName(displayName);
    }

    @PutMapping("/users/{id}") // setting display name MUST KEEP FUNDAMENTAL LOGIC, NOT RETURN TYPE.
    @ResponseBody
    public Optional<User> updateUserName(@PathVariable Long id, @RequestBody User newUser) {
        return userRepository.findById(id)
                .map( user -> {
                    user.setDisplayName(newUser.getDisplayName());
                    return userRepository.save(user);
                });



    }






}
