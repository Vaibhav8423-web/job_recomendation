package com.App.JobRecommender.repo;

import com.App.JobRecommender.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillsRepo extends JpaRepository<Skill,Integer> {
    Optional<Skill> findByNameIgnoreCase(String name);
}
