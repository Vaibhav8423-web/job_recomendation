package com.App.JobRecommender.repo;

import com.App.JobRecommender.entities.RecruiterProfile;
import com.App.JobRecommender.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruiterRepo extends JpaRepository<RecruiterProfile,String> {

   Optional< RecruiterProfile> findByUser(User user);
}
