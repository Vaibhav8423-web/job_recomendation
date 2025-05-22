package com.App.JobRecommender.service;

import com.App.JobRecommender.entities.Job;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.Set;

public interface JobService {
    Job create(Job job, Set<String> skills);
    Job getById(String id);
    Page<Job> getAll(int page, int size, String sortBy, String direction);
    Job update(Job job, Set<String> skills);
    void delete(String id);
    Page<Job> getByLocation(String location, int page, int size, String sortBy, String direction);
    Page<Job> getBySkills(Set<String> skills, int page, int size, String sortBy, String direction);
    Page<Job> getBySkillsAndLocations(Set<String> skills, String location, int page, int size, String sortBy, String direction);
    Page<Job> getJobPostedByAdmin(int page, int size, String sortBy, String direction);
}
