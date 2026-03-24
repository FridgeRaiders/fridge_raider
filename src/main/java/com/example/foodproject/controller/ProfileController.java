package com.example.foodproject.controller;

import com.example.foodproject.dto.IngredientDTO;
import com.example.foodproject.model.User;
import com.example.foodproject.repository.UserRepository;
import com.example.foodproject.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


import java.util.List;
import java.util.Optional;


@Controller
public class ProfileController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/profile/after-login") // adding the AuthO sign up as a user in the Users table - MUST KEEP
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

    @GetMapping("/profile")
    public ModelAndView profilePage() {
        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = (String) principal.getAttributes().get("email");

        User user = userRepository.findUserByEmail(email).orElseThrow();

        ModelAndView myProfile = new ModelAndView("profile");
        myProfile.addObject("user", user);

        return myProfile;
    }

    @GetMapping("/profile/{displayName}") // getting user by display name, need to create html file
    @ResponseBody
    public Optional<User> findByDisplayName(@PathVariable String displayName){
        System.out.println(userRepository.findUserByDisplayName(displayName));
        return userRepository.findUserByDisplayName(displayName);
    }

    @PostMapping("/profile/update")
    public RedirectView updateUserName(@RequestParam String displayName) {
        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = (String) principal.getAttributes().get("email");

        userRepository.findUserByEmail(email)
                .ifPresent(user -> {
                    user.setDisplayName(displayName);
                    userRepository.save(user);
                });

        return new RedirectView("/profile");
    }
}
