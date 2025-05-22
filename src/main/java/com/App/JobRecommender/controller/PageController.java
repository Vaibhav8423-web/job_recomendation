package com.App.JobRecommender.controller;

import com.App.JobRecommender.dto.UserRequest;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.enums.Role;
import com.App.JobRecommender.service.UserService;
import com.App.JobRecommender.util.CommonUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class PageController {

    private UserService userService;

    public PageController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/home")
    public String homePage(){
        return "welcome";
    }

    @PostMapping("/register")
    public User userRegister(@Valid @RequestBody UserRequest userRequest){
        User user = User.builder()
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .name(userRequest.getName())
                .build();

        Role rol = CommonUtils.mapToRoleEnum(userRequest.getRole());
        user.setRole(rol);
        return  userService.saveUser(user);
    }
}
