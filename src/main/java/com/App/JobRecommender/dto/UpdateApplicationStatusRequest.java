package com.App.JobRecommender.dto;

import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    private String applicationId;
    private String newStatus;
}
