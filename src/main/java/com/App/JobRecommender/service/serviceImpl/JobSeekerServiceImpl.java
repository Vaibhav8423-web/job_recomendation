package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.entities.JobSeekerProfile;
import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.Skill;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.repo.JobSeekerRepo;
import com.App.JobRecommender.repo.SkillsRepo;
import com.App.JobRecommender.repo.UserRepo;
import com.App.JobRecommender.service.JobSeekerService;
import com.App.JobRecommender.service.JobService;
import com.App.JobRecommender.service.AIService;
import com.App.JobRecommender.service.JobNotificationService;
import com.App.JobRecommender.util.Helper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobSeekerServiceImpl implements JobSeekerService {

    private JobSeekerRepo jobSeekerRepo;
    private SkillsRepo skillsRepo;
    private UserRepo userRepo;
    private JobService jobService;
    private AIService aiService;
    private JobNotificationService jobNotificationService;

    public JobSeekerServiceImpl(
            JobSeekerRepo jobSeekerRepo, 
            SkillsRepo skillsRepo, 
            UserRepo userRepo,
            JobService jobService,
            AIService aiService,
            JobNotificationService jobNotificationService) {
        this.jobSeekerRepo = jobSeekerRepo;
        this.skillsRepo = skillsRepo;
        this.userRepo = userRepo;
        this.jobService = jobService;
        this.aiService = aiService;
        this.jobNotificationService = jobNotificationService;
    }

    @Override
    public JobSeekerProfile saveJobSeeker(JobSeekerProfile jobSeekerProfile, Set<String> skills) {
        String id = UUID.randomUUID().toString();
        jobSeekerProfile.setId(id);

        Set<Skill> managedSkills = skills.stream()
                .map(name -> {
                    String skillName = name.trim();
                    return skillsRepo.findByNameIgnoreCase(skillName)
                            .orElseGet(() -> skillsRepo.save(Skill.builder().name(skillName).build()));
                })
                .collect(Collectors.toSet());

        String email = Helper.getEmailOfLoggedInUser();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User is not present"));
        jobSeekerProfile.setUser(user);
        jobSeekerProfile.setSkills(managedSkills);
        
        // Save the job seeker profile
        JobSeekerProfile savedProfile = jobSeekerRepo.save(jobSeekerProfile);

        // Get all available jobs (first page of 100 jobs sorted by latest)
        var jobs = jobService.getAll(0, 100, "id", "desc").getContent();
        
        // Find top 5 matching jobs
        var matchingJobs = aiService.findTopMatchingJobs(savedProfile, jobs, 5);
        
        // Send job recommendations if there are matching jobs
        if (!matchingJobs.isEmpty()) {
            jobNotificationService.sendJobRecommendations(savedProfile, matchingJobs);
        }

        return savedProfile;
    }

    @Override
    public JobSeekerProfile updateJobSeeker(JobSeekerProfile jobSeekerProfile, User user, Set<String> skills){

        JobSeekerProfile existingJobSeekerProfile = jobSeekerRepo.findByUser(user).orElseThrow(()->new UsernameNotFoundException("User is not present"));

        if(skills.size()>0) {
            Set<Skill> managedSkills = skills.stream()
                    .map(name -> {
                        String skillName = name.trim();
                        return skillsRepo.findByNameIgnoreCase(skillName)
                                .orElseGet(() -> skillsRepo.save(Skill.builder().name(skillName).build()));
                    })
                    .collect(Collectors.toSet());
            existingJobSeekerProfile.setSkills(managedSkills);
        }

        if(jobSeekerProfile.getResumeUrl()!=null && !jobSeekerProfile.getResumeUrl().isBlank()){
            existingJobSeekerProfile.setResumeUrl(jobSeekerProfile.getResumeUrl());
        }

        // Save the updated profile
        JobSeekerProfile updatedProfile = jobSeekerRepo.save(existingJobSeekerProfile);

        // Get all available jobs (first page of 100 jobs sorted by latest)
        var jobs = jobService.getAll(0, 100, "id", "desc").getContent();
        
        // Find top 5 matching jobs based on updated profile
        var matchingJobs = aiService.findTopMatchingJobs(updatedProfile, jobs, 5);
        
        // Send job recommendations if there are matching jobs
        if (!matchingJobs.isEmpty()) {
            jobNotificationService.sendJobRecommendations(updatedProfile, matchingJobs);
        }

        return updatedProfile;
    }
}
