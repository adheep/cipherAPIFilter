package com.example.patientformapp;

import com.example.patientformapp.util.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.patientformapp.model.Patient;

@SpringBootApplication
public class PatientFormAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientFormAppApplication.class, args);
    }
























    public static void main1(String[] args) {
        try {
            // Create a sample patient object
            Patient patient = new Patient();
            patient.setName("John Doe");
            patient.setAge(30);
            patient.setAddress("123 Main St");

            // Convert patient object to JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String patientJson = objectMapper.writeValueAsString(patient);
            System.out.println("Original JSON: " + patientJson);

            // Encrypt the JSON string
            String encryptedData = AESUtil.encrypt(patientJson);
            System.out.println("Encrypted Data: " + encryptedData);

            // Decrypt the encrypted data
            String decryptedData = AESUtil.decrypt(encryptedData);
            System.out.println("Decrypted JSON: " + decryptedData);

            // Convert JSON string back to patient object
            Patient decryptedPatient = objectMapper.readValue(decryptedData, Patient.class);
            System.out.println("Decrypted Patient: " + decryptedPatient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}