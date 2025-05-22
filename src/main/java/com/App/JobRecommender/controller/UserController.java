package com.App.JobRecommender.controller;

import com.App.JobRecommender.dto.JobSeekerRequest;
import com.App.JobRecommender.dto.SkillsDTO;
import com.App.JobRecommender.dto.UserRequest;
import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobApplication;
import com.App.JobRecommender.entities.JobSeekerProfile;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.service.*;
import com.App.JobRecommender.util.Helper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private JobService jobService;
    private JobSeekerService jobSeekerService;
    private FileService fileService;
    private JobApplicationService jobApplicationService;

    public UserController(UserService userService, JobService jobService, JobSeekerService jobSeekerService, FileService fileService, JobApplicationService jobApplicationService){
        this.userService = userService;
        this.jobService = jobService;
        this.jobSeekerService = jobSeekerService;
        this.fileService = fileService;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/profile")
    public String profile(){
        return "Welcome user";
    }

    @PostMapping("/create/profile")
    public JobSeekerProfile createProfile(@ModelAttribute  JobSeekerRequest jobSeekerRequest){
        JobSeekerProfile jobSeekerProfile = JobSeekerProfile.builder()
                .degree(jobSeekerRequest.getDegree())
                .experienceYears(jobSeekerRequest.getExperienceYears())
                .build();

        if(jobSeekerRequest.getMultipartFile()!=null && !jobSeekerRequest.getMultipartFile().isEmpty()){
            String fileName = UUID.randomUUID().toString()+".pdf";
            String fileURL = fileService.uploadFile(jobSeekerRequest.getMultipartFile(),fileName);
            jobSeekerProfile.setResumeUrl(fileURL);
        }
        return jobSeekerService.saveJobSeeker(jobSeekerProfile,jobSeekerRequest.getSkills());
    }

    @PutMapping("/update")
    public User updateUser(@Valid @RequestBody UserRequest userRequest){
        User user = User.builder()
                .name(userRequest.getName())
                .password(userRequest.getPassword())
                .build();

        String email = Helper.getEmailOfLoggedInUser();
        return userService.updateUser(user, email);
    }

    @PutMapping("/update/profile")
    public JobSeekerProfile updateJobSeekerProfile(@ModelAttribute JobSeekerRequest jobSeekerRequest){
        String email = Helper.getEmailOfLoggedInUser();
        User user = userService.getUserByEmail(email);
        JobSeekerProfile jobSeekerProfile = JobSeekerProfile.builder()
                .experienceYears(jobSeekerRequest.getExperienceYears())
                .degree(jobSeekerRequest.getDegree())
                .build();

        if(jobSeekerRequest.getMultipartFile()!=null && !jobSeekerRequest.getMultipartFile().isEmpty()){
            String fileName = UUID.randomUUID().toString()+".pdf";
            String fileURL = fileService.uploadFile(jobSeekerRequest.getMultipartFile(),fileName);
            jobSeekerProfile.setResumeUrl(fileURL);
        }

        return jobSeekerService.updateJobSeeker(jobSeekerProfile,user,jobSeekerRequest.getSkills());
    }

    @GetMapping("/search")
    public Page<Job> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "salary") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        return jobService.getAll(page,size,sortBy,direction);
    }

    @GetMapping("/search/{location}")
    public Page<Job> getJobsByLocation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "salary") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @PathVariable String location
    ){
        return jobService.getByLocation(location,page,size,sortBy,direction);
    }

    @GetMapping("/search/skills")
    public Page<Job> getJobBySkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "salary") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestBody SkillsDTO skillsDTO
            ){
        return jobService.getBySkills(skillsDTO.getSkills(),page,size,sortBy,direction);
    }

    @GetMapping("/search/skillsAndLocation")
    public Page<Job> getJobBySkillsAndLocation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "salary") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestBody SkillsDTO skillsDTO,
            @RequestParam String location
    ){
        return jobService.getBySkillsAndLocations(skillsDTO.getSkills(),location,page,size,sortBy,direction);
    }

    @PostMapping("/apply/{id}")
    public JobApplication applyToJob(@PathVariable String id){
        Job job = jobService.getById(id);
        String email = Helper.getEmailOfLoggedInUser();

        return jobApplicationService.applyForJob(email,job);
    }

    @GetMapping("/applied")
    public Page<JobApplication> appliedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "timeOfApplied") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        String email = Helper.getEmailOfLoggedInUser();
        return jobApplicationService.jobsAppliedByUser(email,page,size,sortBy,direction);
    }
}
