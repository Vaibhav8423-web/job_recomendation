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

public interface JobSeekerRepo extends JpaRepository<JobSeekerProfile,String> {

    Optional<JobSeekerProfile> findByUser(User user);

    // Find users with any skill from a list
    @Query("SELECT DISTINCT j.user FROM JobSeekerProfile j JOIN j.skills s WHERE s.name IN :skillNames"+" ORDER BY j.user.name ASC")
    Page<User> findUsersBySkillNames(@Param("skillNames") Set<String> skillNames, @Param("role")Role role, Pageable pageable);

    @Query("SELECT j.user FROM JobSeekerProfile j JOIN j.skills s " +
            "WHERE s.name IN :skillNames AND j.experienceYears >= :minExperience "+
            "ORDER BY j.user.name ASC")
    Page<User> findUsersBySkillsAndExperience(@Param("skillNames") Set<String> skillNames,
                                              @Param("minExperience") int minExperience, Role role, Pageable pageable);
}
