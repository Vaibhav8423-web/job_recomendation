package com.App.JobRecommender.service;

import com.App.JobRecommender.entities.Skill;

import java.util.Optional;

public interface SkillService {
    Skill getById(int id);
    Skill getByName(String name);
    void deleteById(int id);
}
