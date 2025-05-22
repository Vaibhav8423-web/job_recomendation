package com.App.JobRecommender.repo;

import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface JobRepo extends JpaRepository<Job,String> {
    Page<Job> findByLocationIgnoreCase(String location, Pageable pageable);

    @Query("SELECT DISTINCT j FROM Job j JOIN j.reqSkills s WHERE s.name IN :skills")
    Page<Job> findByReqSkillsIn(Set<String> skills, Pageable pageable);

    @Query("SELECT DISTINCT j FROM Job j JOIN j.reqSkills s " +
            "WHERE LOWER(j.location) = LOWER(:location) AND s.name IN :skills")
    Page<Job> findByLocationAndSkills(@Param("location") String location,
                                      @Param("skills") Set<String> skills,
                                      Pageable pageable);

    @Query("SELECT DISTINCT j FROM Job j WHERE j.createdBy.email = :email")
    Page<Job> findJobPostedByAdmin(@Param("email")String email, Pageable pageable);
}
