package com.example.patientformapp.model;

public class PatientResponse {
    private String status;
    private String message;

    public PatientResponse() { }

    public PatientResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "status='" + status + '\'' +
                ", message=" + message +
                '}';
    }
}
