package com.example.patientformapp.sensitive;

public class XOREncryption implements Encryption {
    @Override
    public String encrypt(String data, String key) {
        return xorOperation(data, key);
    }

    @Override
    public String decrypt(String data, String key) {
        return xorOperation(data, key);
    }

    private String xorOperation(String data, String key) {
        char[] dataChars = data.toCharArray();
        char[] keyChars = key.toCharArray();
        char[] result = new char[dataChars.length];

        for (int i = 0; i < dataChars.length; i++) {
            result[i] = (char) (dataChars[i] ^ keyChars[i % keyChars.length]);
        }

        return new String(result);
    }
}
