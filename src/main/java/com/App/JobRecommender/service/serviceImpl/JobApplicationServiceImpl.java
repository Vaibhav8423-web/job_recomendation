package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.dto.UpdateApplicationStatusRequest;
import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobApplication;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.enums.ApplicationStatus;
import com.App.JobRecommender.exception.DuplicateJobApplicationException;
import com.App.JobRecommender.exception.JobApplicationNotPresentException;
import com.App.JobRecommender.repo.JobApplicationRepo;
import com.App.JobRecommender.repo.UserRepo;
import com.App.JobRecommender.service.EmailService;
import com.App.JobRecommender.service.JobApplicationService;
import com.App.JobRecommender.util.CommonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private UserRepo userRepo;
    private JobApplicationRepo jobApplicationRepo;
    private EmailService emailService;

    public JobApplicationServiceImpl(UserRepo userRepo, JobApplicationRepo jobApplicationRepo, EmailService emailService){
        this.userRepo = userRepo;
        this.jobApplicationRepo = jobApplicationRepo;
        this.emailService = emailService;
    }

    @Override
    public JobApplication applyForJob(String email, Job job) {
        User user = userRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User is not present"));

        Optional<JobApplication> existing = jobApplicationRepo.findByUserAndJob(user,job);
        if(existing.isPresent()) throw new DuplicateJobApplicationException("User already applied for this job");

        String id = UUID.randomUUID().toString();
        JobApplication jobApplication = JobApplication.builder()
                .id(id)
                .applicationStatus(ApplicationStatus.APPLIED)
                .job(job)
                .user(user)
                .resumeURL(user.getJobSeekerProfile().getResumeUrl())
                .timeOfApplied(LocalDate.now())
                .build();

        //email configuration
        String sendTo = jobApplication.getUser().getEmail();
        String subject = "Thanks applying for : "+jobApplication.getJob().getRole();
        String text = "Hello "+jobApplication.getUser().getName()+", \n\nThanks for applying your current status is "+jobApplication.getApplicationStatus();
        emailService.sendMail(sendTo,subject,text);

        return jobApplicationRepo.save(jobApplication);
    }

    @Override
    public Page<JobApplication> jobsAppliedByUser(String email, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page,size,sort);
        User user = userRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User is not present"));
        return jobApplicationRepo.findByUser(user,pageable);
    }

    @Override
    public Page<User> usersAppliedOnParticularJob(String email, Job job, int page, int size, String sortBy, String direction){
        User user = userRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User is not present"));
        if(!job.getCreatedBy().getId().equals(user.getId())) throw new AccessDeniedException("You are not allowed to access");

        Sort sort = direction.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page,size,sort);
        return jobApplicationRepo.findByJob(job,pageable);
    }

    @Override
    public JobApplication updateStatusOfUser(String email, UpdateApplicationStatusRequest updateApplicationStatusRequest) {
        User user = userRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User is not present"));
        JobApplication existingJobApplication = jobApplicationRepo.findById(updateApplicationStatusRequest.getApplicationId()).orElseThrow(()->new JobApplicationNotPresentException("Job application is not found"));
        Job job = existingJobApplication.getJob();
        if(!user.getId().equals(job.getCreatedBy().getId())) throw new AccessDeniedException("You are not allowed to change the status");
        ApplicationStatus newStatus = CommonUtils.mapToApplicationStatusEnum(updateApplicationStatusRequest.getNewStatus());
        existingJobApplication.setApplicationStatus(newStatus);

        //email configuration
        String sendTo = existingJobApplication.getUser().getEmail();
        String subject = "Update on your job application for : "+existingJobApplication.getJob().getRole();
        String text = "Hello "+existingJobApplication.getUser().getName()+", \n\nYour job application status has beed updated to "+newStatus;
        emailService.sendMail(sendTo,subject,text);

        return jobApplicationRepo.save(existingJobApplication);
    }
}
