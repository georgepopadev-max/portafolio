package com.apidocgen.dto;

public class GenerationStatusDto {
    private String status;
    private String currentStep;
    private int stepNumber;
    private int totalSteps;
    private int aiRequestsProcessed;
    private String message;

    public GenerationStatusDto() {}

    public GenerationStatusDto(String status, String currentStep, int stepNumber, 
                               int totalSteps, int aiRequestsProcessed, String message) {
        this.status = status;
        this.currentStep = currentStep;
        this.stepNumber = stepNumber;
        this.totalSteps = totalSteps;
        this.aiRequestsProcessed = aiRequestsProcessed;
        this.message = message;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    public int getAiRequestsProcessed() { return aiRequestsProcessed; }
    public void setAiRequestsProcessed(int aiRequestsProcessed) { this.aiRequestsProcessed = aiRequestsProcessed; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}