package com.App.JobRecommender.util;

import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Helper {

    @Autowired
    private UserRepo userRepo;

    public static String getEmailOfLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return email;
    }
}
