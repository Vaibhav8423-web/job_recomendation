package com.App.JobRecommender.service;

import com.App.JobRecommender.entities.JobSeekerProfile;
import com.App.JobRecommender.entities.User;

import java.util.Set;

public interface JobSeekerService {
    JobSeekerProfile saveJobSeeker(JobSeekerProfile jobSeekerProfile, Set<String> skills);
    JobSeekerProfile updateJobSeeker(JobSeekerProfile jobSeekerProfile, User user, Set<String> skills);
}
