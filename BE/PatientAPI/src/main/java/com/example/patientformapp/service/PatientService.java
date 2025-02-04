package com.example.patientformapp.service;

import com.example.patientformapp.model.Patient;
import com.example.patientformapp.model.PatientResponse;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PatientService {
    private static final Random RANDOM = new Random();

    public static String generatePatientID() {
        int id = RANDOM.nextInt(90000) + 10000; // Generates a random number between 10000 and 99999
        return String.valueOf(id);
    }

    public PatientResponse savePatient(Patient patient) {
        // Logic to save patient details
        System.out.println("Patient details saved successfully!\n"+patient.toString());
        String patientID = generatePatientID();
        return new PatientResponse("success", "Patient details saved successfully! Patient ID: " + patientID);
    }
}