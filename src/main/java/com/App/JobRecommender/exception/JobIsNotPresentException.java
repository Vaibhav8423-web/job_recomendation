package com.App.JobRecommender.exception;

public class JobIsNotPresentException extends RuntimeException{

    public JobIsNotPresentException(String message) {
        super(message);
    }
}
