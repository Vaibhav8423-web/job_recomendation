package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.entities.JobSeekerProfile;
import com.App.JobRecommender.entities.Skill;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.enums.Role;
import com.App.JobRecommender.repo.JobSeekerRepo;
import com.App.JobRecommender.repo.SkillsRepo;
import com.App.JobRecommender.repo.UserRepo;
import com.App.JobRecommender.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private SkillsRepo skillRepo;
    private  Role role = Role.ROLE_ADMIN;
    private JobSeekerRepo jobSeekerRepo;

    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, SkillsRepo skillRepo, JobSeekerRepo jobSeekerRepo){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.skillRepo = skillRepo;
        this.jobSeekerRepo = jobSeekerRepo;
    }

    @Override
    public User saveUser(User user) {

        Optional<User> existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            // If user exists, return a message or null or throw exception depending on business logic
            throw new RuntimeException("User with this email already exists.");
        }

        String id = UUID.randomUUID().toString();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepo.save(user);
    }

    @Override
    public User getUserById(String id) {
        return userRepo.findById(id).orElseThrow(()->new RuntimeException("User with id "+id+" is not present"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User with mail "+email+" not present"));
    }

    @Override
    public User updateUser(User user, String email) {
        User existingUser = userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User is not present with mail "+user.getEmail()));
        existingUser.setName(user.getName());
        existingUser.setRole(user.getRole());
        existingUser.setPassword(user.getPassword());

        return userRepo.save(existingUser);
    }

    @Override
    public void deleteUser(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User with this mail "+email+" not present"));
        userRepo.deleteByEmail(email);
    }

    @Override
    public Page<User> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort sort = sortBy.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageeable = PageRequest.of(page,size,sort);
        return userRepo.findAll(pageeable);
    }

    @Override
    public Page<User> getBySkills(Set<String> skills, int page, int size, String sortBy, String direction) {
        PageRequest pageable = PageRequest.of(page,size);
        return jobSeekerRepo.findUsersBySkillNames(skills,role,pageable);
    }

    @Override
    public Page<User> getBySkillsAndExperience(Set<String> skills, int experience, int page, int size, String sortBy, String direction) {
        PageRequest pageable = PageRequest.of(page,size);
        return jobSeekerRepo.findUsersBySkillsAndExperience(skills,experience,role,pageable);
    }

}
