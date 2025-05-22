package com.App.JobRecommender.controller;

import com.App.JobRecommender.dto.*;
import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.JobApplication;
import com.App.JobRecommender.entities.RecruiterProfile;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.enums.Role;
import com.App.JobRecommender.service.JobApplicationService;
import com.App.JobRecommender.service.JobService;
import com.App.JobRecommender.service.RecruiterService;
import com.App.JobRecommender.service.UserService;
import com.App.JobRecommender.util.CommonUtils;
import com.App.JobRecommender.util.Helper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;
    private JobService jobService;
    private RecruiterService recruiterService;
    private JobApplicationService jobApplicationService;

    public AdminController(UserService userService, JobApplicationService jobApplicationService, JobService jobService, RecruiterService recruiterService){
        this.userService = userService;
        this.jobService = jobService;
        this.recruiterService = recruiterService;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/profile")
    public String adminProfile(){
        return "welcome admin";
    }

    //Searching of all users by admin in different conditions

    @GetMapping("/search")
    public Page<User> searchAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
            ){
        return userService.getAllUsers(page,size,sortBy,direction);
    }

    @GetMapping("/search/skills")
    public Page<User> searchUserBySkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestBody SkillsDTO skillsDTO
            ){
        return userService.getBySkills(skillsDTO.getSkills(),page,size,sortBy,direction);
    }

    @GetMapping("search/skillsAndExperience")
    public Page<User> searchUserBySkillsAndExperience(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestBody SkillsDTO skillsDTO,
            @RequestParam int experience
    ){
        return userService.getBySkillsAndExperience(skillsDTO.getSkills(),experience,page,size,sortBy,direction);
    }

    //create job

    @PostMapping("/create")
    public Job createJob(@Valid @RequestBody JobRequest jobreq){
        Job job = Job.builder()
                .role(jobreq.getRole())
                .description(jobreq.getDescription())
                .salary(jobreq.getSalary())
                .location(jobreq.getLocation())
                .build();
        Set<String> skills = jobreq.getSkills();
        return jobService.create(job,skills);
    }

    // update job

    @PutMapping("/{id}")
    public Job updateJobById(@PathVariable String id, @Valid @RequestBody JobRequest jobRequest){
        Job job = Job.builder()
                .id(id)
                .role(jobRequest.getRole())
                .description(jobRequest.getDescription())
                .salary(jobRequest.getSalary())
                .location(jobRequest.getLocation())
                .build();
        Set<String> skills = jobRequest.getSkills();
        return jobService.update(job,skills);
    }

    //delete job
    @DeleteMapping("/{id}")
    public void deleteJobById(@PathVariable String id){
        jobService.delete(id);
    }

    @PutMapping("/update")
    public User updateUser(@Valid @RequestBody UserRequest userRequest){
        User user = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .build();

        Role rol = CommonUtils.mapToRoleEnum(userRequest.getRole());
        user.setRole(rol);

        String email = Helper.getEmailOfLoggedInUser();
        return userService.updateUser(user, email);
    }

    @PutMapping("/update/profile")
    public RecruiterProfile updateRecruiterProfile(@Valid @RequestBody RecruiterRequest recruiterRequest){
        String email = Helper.getEmailOfLoggedInUser();
        User user = userService.getUserByEmail(email);
        RecruiterProfile recruiterProfile = RecruiterProfile.builder()
                .location(recruiterRequest.getLocation())
                .companyName(recruiterRequest.getCompanyName())
                .build();
        return recruiterService.updateRecruiter(recruiterProfile,user);
    }

    //view jobs
    @GetMapping("/jobs")
    public Page<Job> viewAllJobs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "salary") String sortBy,
        @RequestParam(defaultValue = "asc") String direction
    ){
       return jobService.getAll(page,size,sortBy,direction);
    }

    @GetMapping("/jobs/posted")
    public Page<Job> viewJobsPostedByAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "salary") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        return jobService.getJobPostedByAdmin(page,size,sortBy,direction);
    }

    @PostMapping("/create/profile")
    public RecruiterProfile createProfile(@Valid @RequestBody RecruiterRequest recruiterRequest){
        RecruiterProfile recruiterProfile = RecruiterProfile.builder()
                .companyName(recruiterRequest.getCompanyName())
                .location(recruiterRequest.getLocation())
                .build();

        return recruiterService.saveRecruiter(recruiterProfile);
    }

    @GetMapping("/applied/{id}")
    public Page<User> usersAppliedForParticularJob(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "timeOfApplied") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @PathVariable String id
    ){
        Job job = jobService.getById(id);
        String email = Helper.getEmailOfLoggedInUser();
        return jobApplicationService.usersAppliedOnParticularJob(email,job,page,size,sortBy,direction);
    }

    @PutMapping("/status/update")
    public JobApplication updateApp(
            @RequestBody UpdateApplicationStatusRequest updateApplicationStatusRequest
            ){
        String email = Helper.getEmailOfLoggedInUser();
        return jobApplicationService.updateStatusOfUser(email,updateApplicationStatusRequest);
    }
}
