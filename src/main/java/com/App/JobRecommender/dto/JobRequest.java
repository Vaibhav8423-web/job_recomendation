package com.App.JobRecommender.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobRequest {


    private String role;


    private String description;


    private String location;


    private int salary;

    private Set<String> skills;
}
