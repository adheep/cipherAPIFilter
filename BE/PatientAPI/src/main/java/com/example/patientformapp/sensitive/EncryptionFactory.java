package com.example.patientformapp.sensitive;

public class EncryptionFactory {
    public static Encryption getEncryption(String type) {
        switch (type.toUpperCase()) {
            case "XOR":
                return new XOREncryption();
            case "AES":
                return new AESEncryption();
            default:
                throw new IllegalArgumentException("Unknown encryption type: " + type);
        }
    }
}
