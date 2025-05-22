package com.App.JobRecommender.service;

import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobSeekerProfile;

import java.util.List;
import java.util.Set;

public interface AIService {

    Set<String> extractSkillsFromResume(String resumeText);

    double calculateSkillMatch(Set<String> jobSkills, Set<String> userSkills);

    List<Job> findTopMatchingJobs(JobSeekerProfile jobSeekerProfile, List<Job> allJobs, int limit);

    List<JobSeekerProfile> findMatchingJobSeekers(Job job, List<JobSeekerProfile> allProfiles, double minMatchPercentage);
} 