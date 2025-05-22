package com.App.JobRecommender.service;

import com.App.JobRecommender.entities.RecruiterProfile;
import com.App.JobRecommender.entities.User;

public interface RecruiterService {
    RecruiterProfile saveRecruiter(RecruiterProfile recruiterProfile);
    RecruiterProfile updateRecruiter(RecruiterProfile recruiterProfile, User user);
}
