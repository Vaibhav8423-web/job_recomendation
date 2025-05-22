package com.App.JobRecommender.dto;

import com.App.JobRecommender.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserRequest {
    @NotNull(message = "Name can't be null")
    @Size(min = 2 , max = 40)
    private String name;

    @Email
    @NotNull(message = "Email can't be null")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull @Size(min=8, message = "Password length should be 8 or more")
    private String password;

    @NotNull(message = "Role can't be null")
    private String role;

}
