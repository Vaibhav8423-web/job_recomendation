package com.App.JobRecommender.service.serviceImpl;

import com.App.JobRecommender.entities.Skill;
import com.App.JobRecommender.repo.SkillsRepo;
import com.App.JobRecommender.service.SkillService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {

    private SkillsRepo skillsRepo;

    public SkillServiceImpl(SkillsRepo skillsRepo){
        this.skillsRepo = skillsRepo;
    }
    @Override
    public Skill getById(int id) {
        return skillsRepo.findById(id).orElseThrow(()->new RuntimeException("User with id "+id+"not present"));
    }

    @Override
    public Skill getByName(String name) {
        return skillsRepo.findByNameIgnoreCase(name).orElseThrow(()->new RuntimeException("Ski"));
    }

    @Override
    public void deleteById(int id) {

    }
}
