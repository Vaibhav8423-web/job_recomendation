package com.App.JobRecommender.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.NotFound;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull(message = "Skills can't be null")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Same reference, it's equal
        if (o == null || getClass() != o.getClass()) return false; // Different types, not equal
        Skill skill = (Skill) o;
        return id != null && id.equals(skill.id); // Compare based on id (or name)
    }

    // Override hashCode to generate a hash based on id (or name)
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0; // Use id (or name) as the basis for hash code
    }
}
