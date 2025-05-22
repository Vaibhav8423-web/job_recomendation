package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobSeekerProfile;
import com.App.JobRecommender.service.AIService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIServiceImpl implements AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);
    private final OpenAiService openAiService;
    
    public AIServiceImpl(@Value("${openai.api.key}") String openaiApiKey) {
        this.openAiService = new OpenAiService(openaiApiKey);
    }

    @Override
    public Set<String> extractSkillsFromResume(String resumeText) {
        try {
            String prompt = String.format(
                "Extract technical skills from the following resume text. Return only the skills as a comma-separated list:" +
                "\n\nResume text:\n%s", resumeText);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(Arrays.asList(new ChatMessage("user", prompt)))
                .temperature(0.3)
                .build();

            String response = openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();

            return Arrays.stream(response.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        } catch (Exception e) {
            logger.error("Error extracting skills from resume: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    @Override
    public double calculateSkillMatch(Set<String> jobSkills, Set<String> userSkills) {
        if (jobSkills.isEmpty() || userSkills.isEmpty()) {
            return 0.0;
        }

        // Convert to lowercase for case-insensitive comparison
        Set<String> normalizedJobSkills = jobSkills.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
        Set<String> normalizedUserSkills = userSkills.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        // Calculate intersection
        Set<String> matchingSkills = new HashSet<>(normalizedJobSkills);
        matchingSkills.retainAll(normalizedUserSkills);

        // Calculate match percentage
        return (double) matchingSkills.size() / jobSkills.size() * 100.0;
    }

    @Override
    public List<Job> findTopMatchingJobs(JobSeekerProfile jobSeekerProfile, List<Job> allJobs, int limit) {
        if (jobSeekerProfile.getSkills() == null || jobSeekerProfile.getSkills().isEmpty()) {
            return new ArrayList<>();
        }

        // Convert job seeker skills to strings
        Set<String> userSkills = jobSeekerProfile.getSkills().stream()
            .map(skill -> skill.getName())
            .collect(Collectors.toSet());

        // Calculate match percentage for each job and sort
        return allJobs.stream()
            .map(job -> new AbstractMap.SimpleEntry<>(
                job,
                calculateSkillMatch(
                    job.getReqSkills().stream().map(skill -> skill.getName()).collect(Collectors.toSet()),
                    userSkills
                )
            ))
            .filter(entry -> entry.getValue() > 0) // Filter out 0% matches
            .sorted(Map.Entry.<Job, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    @Override
    public List<JobSeekerProfile> findMatchingJobSeekers(Job job, List<JobSeekerProfile> allProfiles, double minMatchPercentage) {
        if (job.getReqSkills() == null || job.getReqSkills().isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> jobSkills = job.getReqSkills().stream()
            .map(skill -> skill.getName())
            .collect(Collectors.toSet());

        return allProfiles.stream()
            .map(profile -> new AbstractMap.SimpleEntry<>(
                profile,
                calculateSkillMatch(
                    jobSkills,
                    profile.getSkills().stream().map(skill -> skill.getName()).collect(Collectors.toSet())
                )
            ))
            .filter(entry -> entry.getValue() >= minMatchPercentage)
            .sorted(Map.Entry.<JobSeekerProfile, Double>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
} 