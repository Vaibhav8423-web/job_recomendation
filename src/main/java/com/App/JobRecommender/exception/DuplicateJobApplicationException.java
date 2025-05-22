package com.App.JobRecommender.exception;

public class DuplicateJobApplicationException extends RuntimeException{

    public DuplicateJobApplicationException(String message) {
        super(message);
    }
}
