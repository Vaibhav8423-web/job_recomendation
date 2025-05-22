package com.App.JobRecommender.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
public class JobSeekerRequest {
    @NotNull(message = "degree can't kept null")
    String degree;

    @NotNull(message = "experience is required")
    int experienceYears;

    MultipartFile multipartFile;

    Set<String> skills;
}
