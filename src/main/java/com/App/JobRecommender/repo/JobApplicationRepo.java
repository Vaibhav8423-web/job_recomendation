package com.App.JobRecommender.repo;

import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobApplication;
import com.App.JobRecommender.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobApplicationRepo extends JpaRepository<JobApplication,String>{
    Page<JobApplication> findByUser(User user, Pageable pageable);
    Optional<JobApplication> findByUserAndJob(User user, Job job);
    @Query("SELECT j.user FROM JobApplication j WHERE j.job = :job")
    Page<User> findByJob(@Param("job") Job job, Pageable pageable);
}
