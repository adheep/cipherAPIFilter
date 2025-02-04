package com.example.patientformapp.controller;

import com.example.patientformapp.model.Patient;
import com.example.patientformapp.model.PatientResponse;
import com.example.patientformapp.service.PatientService;
import com.example.patientformapp.util.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private ObjectMapper customObjectMapper;

    @PostMapping
    public PatientResponse addPatient(@RequestBody String patientJson) {
        try {
            // Deserialize the request body using the custom ObjectMapper
            Patient patient = customObjectMapper.readValue(patientJson, Patient.class);
            // Save patient and get the response
            if(patient.getName()==null) {
                System.out.println("Incorrect Data or content-type mismatch");
                return new PatientResponse("error", "Incorrect Data or content-type mismatch");
            } else {
                System.out.println("Processed successfully");
                return patientService.savePatient(patient);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new PatientResponse("error", "Failed to process request");
        }
    }
}
