package com.App.JobRecommender.repo;

import com.App.JobRecommender.entities.JobSeekerProfile;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserRepo extends JpaRepository<User,String>{
    Optional<User> findByEmail(String email);
    Optional<User> deleteByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u WHERE u.role != :role")
    Page<User> findAll(@Param("role") Role role, Pageable pageable);


}
