package com.App.JobRecommender.util;

import com.App.JobRecommender.enums.ApplicationStatus;
import com.App.JobRecommender.enums.Role;

public class CommonUtils {

    public static Role mapToRoleEnum(String str){
        if(str.trim().toLowerCase().equals("admin")) return Role.ROLE_ADMIN;
        return Role.ROLE_USER;
    }

    public static ApplicationStatus mapToApplicationStatusEnum(String str){
        if(str.trim().toLowerCase().equals("accept")) return ApplicationStatus.ACCEPTED;
        else return ApplicationStatus.REJECTED;
    }
}
