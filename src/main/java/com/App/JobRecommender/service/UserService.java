package com.App.JobRecommender.service;

import com.App.JobRecommender.entities.User;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface UserService {
     User saveUser(User user);
     User getUserById(String id);
     User getUserByEmail(String email);
     User updateUser(User user, String email);
     void deleteUser(String id);
     Page<User> getAllUsers(int page, int size, String sortBy, String direction);
     Page<User> getBySkills(Set<String> skills, int page, int size, String sortBy, String direction);
     Page<User> getBySkillsAndExperience(Set<String> skills,int experience, int page, int size, String sortBy, String direction);

}
