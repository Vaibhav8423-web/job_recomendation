package com.App.JobRecommender.exception;

public class JobApplicationNotPresentException extends RuntimeException{

    public JobApplicationNotPresentException(String message) {
        super(message);
    }
}
