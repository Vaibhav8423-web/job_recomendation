package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.entities.RecruiterProfile;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.repo.RecruiterRepo;
import com.App.JobRecommender.repo.UserRepo;
import com.App.JobRecommender.service.RecruiterService;
import com.App.JobRecommender.util.Helper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RecruiterServiceImpl implements RecruiterService {

    private RecruiterRepo recruiterRepo;
    private UserRepo userRepo;

    public RecruiterServiceImpl(RecruiterRepo recruiterRepo, UserRepo userRepo){
        this.recruiterRepo = recruiterRepo;
        this.userRepo = userRepo;
    }

    @Override
    public RecruiterProfile saveRecruiter(RecruiterProfile recruiterProfile) {
        String id = UUID.randomUUID().toString();
        String email = Helper.getEmailOfLoggedInUser();

        User user = userRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User is not present"));
        recruiterProfile.setId(id);
        recruiterProfile.setUser(user);
        return recruiterRepo.save(recruiterProfile);
    }

    @Override
    public RecruiterProfile updateRecruiter(RecruiterProfile recruiterProfile, User user) {
        RecruiterProfile existingRecruiterProfile = recruiterRepo.findByUser(user).orElseThrow(()->new UsernameNotFoundException("User is not present"));
        existingRecruiterProfile.setLocation(recruiterProfile.getLocation());
        existingRecruiterProfile.setCompanyName(recruiterProfile.getCompanyName());
        return recruiterRepo.save(existingRecruiterProfile);
    }
}
