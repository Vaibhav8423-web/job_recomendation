package com.App.JobRecommender.service;

import com.App.JobRecommender.dto.UpdateApplicationStatusRequest;
import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobApplication;
import com.App.JobRecommender.entities.User;
import org.springframework.data.domain.Page;

public interface JobApplicationService {
    JobApplication applyForJob(String email, Job job);
    Page<JobApplication> jobsAppliedByUser(String email, int page, int size, String sortBy, String direction);
    Page<User> usersAppliedOnParticularJob(String email, Job job, int page, int size, String sortBy, String direction);
    JobApplication updateStatusOfUser(String email, UpdateApplicationStatusRequest updateApplicationStatusRequest);
}
