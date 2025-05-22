package com.App.JobRecommender.service;

import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobSeekerProfile;
import java.util.List;

public interface JobNotificationService {

    void sendJobRecommendations(JobSeekerProfile jobSeekerProfile, List<Job> matchingJobs);

    void notifyMatchingJobSeekers(Job job, List<JobSeekerProfile> matchingProfiles);
} 