package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.entities.Job;
import com.App.JobRecommender.entities.Skill;
import com.App.JobRecommender.entities.User;
import com.App.JobRecommender.exception.JobIsNotPresentException;
import com.App.JobRecommender.repo.JobRepo;
import com.App.JobRecommender.repo.SkillsRepo;
import com.App.JobRecommender.repo.UserRepo;
import com.App.JobRecommender.service.JobService;
import com.App.JobRecommender.util.Helper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private JobRepo jobRepo;
    private SkillsRepo skillRepo;
    private UserRepo userRepo;

    public JobServiceImpl(JobRepo jobRepo, SkillsRepo skillRepo, UserRepo userRepo){
        this.jobRepo = jobRepo;
        this.skillRepo = skillRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Job create(Job job, Set<String> skills) {
        String id = UUID.randomUUID().toString();
        Set<Skill> managedSkills = skills.stream()
                .map(name -> {
                    String skillName = name.trim();
                    return skillRepo.findByNameIgnoreCase(skillName)
                            .orElseGet(() -> skillRepo.save(Skill.builder().name(skillName).build()));
                })
                .collect(Collectors.toSet());

        //this job is created by
        String email = Helper.getEmailOfLoggedInUser();
        User admin = userRepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User with this email id :"+email+"is not present"));
        job.setId(id);
        job.setCreatedBy(admin);
        job.setReqSkills(managedSkills);
        return jobRepo.save(job);
    }

    @Override
    public Job getById(String id) {
        return jobRepo.findById(id).orElseThrow(()->new JobIsNotPresentException("Job is not present with this id"));
    }

    @Override
    public Page<Job> getAll(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equals("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page,size,sort);
        return jobRepo.findAll(pageable);
    }

    @Override
    public Job update(Job job, Set<String> skills){
        Job existingJob = jobRepo.findById(job.getId()).orElseThrow(()->new JobIsNotPresentException("Job is not present with this id"));

        // check if current user can update this job
        String loggedInUser = Helper.getEmailOfLoggedInUser();
        if(!existingJob.getCreatedBy().getEmail().equalsIgnoreCase(loggedInUser)){
            throw new AccessDeniedException("You can only update jobs that you have posted");
        }

        if(job.getDescription()!=null && !job.getDescription().isBlank())
        existingJob.setDescription(job.getDescription());

        if(job.getRole()!=null && !job.getRole().isBlank())
        existingJob.setRole(job.getRole());

        if(job.getSalary()!=null)
        existingJob.setSalary(job.getSalary());

        if(job.getLocation()!=null && !job.getLocation().isBlank())
        existingJob.setLocation(job.getLocation());

        if(skills.size()>0) {
            Set<Skill> managedSkills = skills.stream()
                    .map(name -> {
                        String skillName = name.trim();
                        return skillRepo.findByNameIgnoreCase(skillName)
                                .orElseGet(() -> skillRepo.save(Skill.builder().name(skillName).build()));
                    })
                    .collect(Collectors.toSet());

            existingJob.setReqSkills(managedSkills);
        }
        return jobRepo.save(existingJob);
    }

    @Override
    public void delete(String id) {
        Job job = jobRepo.findById(id).orElseThrow(()->new JobIsNotPresentException("Job is not present with this id"));

        String loggedInUser = Helper.getEmailOfLoggedInUser();
        if(!job.getCreatedBy().getEmail().equalsIgnoreCase(loggedInUser)){
            throw new AccessDeniedException("You can only delete jobs that you have posted");
        }

        jobRepo.deleteById(id);
    }

    @Override
    public Page<Job> getByLocation(String location, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equals("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page,size,sort);
        return jobRepo.findByLocationIgnoreCase(location,pageable);
    }

    @Override
    public Page<Job> getBySkills(Set<String> skills, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equals("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page,size,sort);
        return jobRepo.findByReqSkillsIn(skills,pageable);
    }

    @Override
    public Page<Job> getBySkillsAndLocations(Set<String> skills, String location, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equals("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page,size,sort);
        return jobRepo.findByLocationAndSkills(location,skills,pageable);
    }

    @Override
    public Page<Job> getJobPostedByAdmin(int page, int size, String sortBy, String direction) {
        String email = Helper.getEmailOfLoggedInUser();
        Sort sort = direction.equals("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page,size,sort);
        return jobRepo.findJobPostedByAdmin(email,pageable);
    }


}
