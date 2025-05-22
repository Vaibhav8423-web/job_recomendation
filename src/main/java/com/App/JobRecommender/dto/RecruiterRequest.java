package com.App.JobRecommender.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecruiterRequest {

    @NotNull(message = "Company name is Required")
    private String companyName;

    @NotNull(message = "Location is required")
    private String location;
}
