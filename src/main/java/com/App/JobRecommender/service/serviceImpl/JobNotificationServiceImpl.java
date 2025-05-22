package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobSeekerProfile;
import com.App.JobRecommender.service.EmailService;
import com.App.JobRecommender.service.JobNotificationService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobNotificationServiceImpl implements JobNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(JobNotificationServiceImpl.class);
    private final EmailService emailService;

    public JobNotificationServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendJobRecommendations(JobSeekerProfile jobSeekerProfile, List<Job> matchingJobs) {
        String userEmail = jobSeekerProfile.getUser().getEmail();
        String userName = jobSeekerProfile.getUser().getName();

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Dear ").append(userName).append(",\n\n");
        emailContent.append("Based on your skills, we found these top job matches for you:\n\n");

        for (int i = 0; i < matchingJobs.size(); i++) {
            Job job = matchingJobs.get(i);
            emailContent.append(i + 1).append(". ").append(job.getRole())
                .append(" at ").append(job.getCreatedBy().getName())
                .append("\n   Required Skills: ").append(job.getReqSkills().stream()
                    .map(skill -> skill.getName())
                    .collect(Collectors.joining(", ")))
                .append("\n   Salary: ").append(job.getSalary())
                .append("\n\n");
        }

        emailContent.append("Login to your account to apply for these positions.\n\n");
        emailContent.append("Best regards,\nJob Recommender Team");

        try {
            emailService.sendMail(
                userEmail,
                "Top Job Matches for Your Profile",
                emailContent.toString()
            );
            logger.info("Sent job recommendations to user: {}", userEmail);
        } catch (Exception e) {
            logger.error("Failed to send job recommendations to user: {}", userEmail, e);
        }
    }

    @Override
    public void notifyMatchingJobSeekers(Job job, List<JobSeekerProfile> matchingProfiles) {
        for (JobSeekerProfile profile : matchingProfiles) {
            String userEmail = profile.getUser().getEmail();
            String userName = profile.getUser().getName();

            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Dear ").append(userName).append(",\n\n");
            emailContent.append("We found a new job posting that matches your skills!\n\n");
            emailContent.append("Job Details:\n");
            emailContent.append("Role: ").append(job.getRole()).append("\n");
            emailContent.append("Posted By: ").append(job.getCreatedBy().getName()).append("\n");
            emailContent.append("Location: ").append(job.getLocation()).append("\n");
            emailContent.append("Required Skills: ").append(job.getReqSkills().stream()
                .map(skill -> skill.getName())
                .collect(Collectors.joining(", "))).append("\n");
            emailContent.append("Salary: ").append(job.getSalary()).append("\n\n");
            emailContent.append("Login to your account to apply for this position.\n\n");
            emailContent.append("Best regards,\nJob Recommender Team");

            try {
                emailService.sendMail(
                    userEmail,
                    "New Job Matching Your Skills: " + job.getRole(),
                    emailContent.toString()
                );
                logger.info("Sent job notification to user: {}", userEmail);
            } catch (Exception e) {
                logger.error("Failed to send job notification to user: {}", userEmail, e);
            }
        }
    }
} 