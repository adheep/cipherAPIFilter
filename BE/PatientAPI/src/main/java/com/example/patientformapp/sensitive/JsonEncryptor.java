package com.example.patientformapp.sensitive;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class JsonEncryptor {
    private Encryption encryption;
    private Set<String> attributesToEncrypt;

    public JsonEncryptor(Encryption encryption, Set<String> attributesToEncrypt) {
        this.encryption = encryption;
        this.attributesToEncrypt = attributesToEncrypt;
    }

    private String convertToDateString(Object value) {
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return strValue; // Already in correct format
            }
        } else if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            if (jsonArray.length() == 3) {
                int year = jsonArray.optInt(0);
                int month = jsonArray.optInt(1);
                int day = jsonArray.optInt(2);
                if (year > 0 && month > 0 && day > 0) { // Basic validation
                    try {
                        return String.format("%04d-%02d-%02d", year, month, day); // Format as YYYY-MM-DD
                    } catch (Exception e) { // Handle formatting errors (e.g., invalid month/day)
                        return null; // Or handle the error differently
                    }
                }
            }
        }
        return null; // Return null if not a valid date format
    }

    public JSONObject encryptJson(JSONObject json, String key) throws Exception {
        JSONObject encryptedJson = new JSONObject();
        for (String attribute : json.keySet()) {
            Object value = json.get(attribute);
            if (value instanceof JSONArray) {
                String dateString = convertToDateString(value);
                if (dateString != null) {
                    if(attributesToEncrypt.contains(attribute)){
                        encryptedJson.put(attribute, encryption.encrypt(dateString, key));
                    } else {
                        encryptedJson.put(attribute, dateString);
                    }
                } else {
                    encryptedJson.put(attribute, encryptJsonArray(attribute, (JSONArray) value, key));
                }
            } else if (value instanceof String && attributesToEncrypt.contains(attribute)) {
                encryptedJson.put(attribute, encryption.encrypt((String) value, key));
            } else if (value instanceof JSONObject) {
                encryptedJson.put(attribute, encryptJson((JSONObject) value, key));
            } else if (value instanceof JSONArray) {
                encryptedJson.put(attribute, encryptJsonArray(attribute, (JSONArray) value, key));
            } else if (attributesToEncrypt.contains(attribute)) {
                encryptedJson.put(attribute, encryption.encrypt(value.toString(), key));
            } else {
                encryptedJson.put(attribute, value);
            }
        }
        return encryptedJson;
    }

    private JSONArray encryptJsonArray(String parentAttribute, JSONArray array, String key) throws Exception {
        JSONArray encryptedArray = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);

            if (value instanceof JSONArray) {  // Handle nested JSONArrays
                String dateString = convertToDateString(value); // Try converting to date string
                if (dateString != null) {
                    if (attributesToEncrypt.contains(parentAttribute)) {
                        encryptedArray.put(encryption.encrypt(dateString, key));
                    } else { // If it's not a date or doesn't need encryption, recursively process the nested array
                        encryptedArray.put(dateString);
                    }
                }
            } else if (value instanceof String && attributesToEncrypt.contains(parentAttribute)) {
                encryptedArray.put(encryption.encrypt((String) value, key));
            } else if (value instanceof JSONObject) {
                encryptedArray.put(encryptJson((JSONObject) value, key));
            } else {  // Other value types (including already converted date strings)
                encryptedArray.put(value);
            }
        }
        return encryptedArray;
    }

    public static void main(String[] args) throws Exception {
        try (InputStream input = JsonEncryptor.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            Properties properties = new Properties();
            properties.load(input);
            String attributesToEncryptString = properties.getProperty("attributesToEncrypt");
            String[] attributesToEncryptArray = attributesToEncryptString.split(",");

            Set<String> attributesToEncrypt = new HashSet<>(Arrays.asList(attributesToEncryptArray));

            String jsonString = "{\"cvv\":\"909\",\"firstName\":\"ADELIO\",\"lastName\":\"ALARIC\",\"gender\":\"Male\",\"defaultDeliveryDate\": [2025,1,23],\"pharmacyCode\":\"99\",\"copay\":10.0,\"supplies\":false,\"scripts\":[{\"itemType\":\"Supply\",\"drugName\":\"SHARPS 1.5 QT\",\"quantity\":1,\"rxNumber\":\"162879176722\",\"daysSupply\":1,\"refillsRemaining\":10,\"amountDue\":0.0,\"isRefrigerated\":false,\"shortDrugName\":\"SHARPS\"},{\"itemType\":\"Drug\",\"drugName\":\"ACTEMRA 162 MG/0.9ML SYR\",\"quantity\":1,\"rxNumber\":\"162879176723\",\"daysSupply\":1,\"refillsRemaining\":10,\"amountDue\":10.0,\"isRefrigerated\":true,\"shortDrugName\":\"ACTEMRA\"}],\"addresses\":[{\"type\":\"HOME\",\"line1\":\"1ST CROSS ROAD\",\"city\":\"SCHENECTADY\",\"state\":\"NY\",\"zipCode\":\"123450001\",\"defaultAddress\":true,\"isSignatureRequired\":false}],\"paymentCards\":[{\"resourceType\":\"PaymentCard\",\"meta\":{\"profile\":[\"http://hl7.org/fhir/StructureDefinition/PaymentReconciliation\"]},\"address\":{\"line\":[\"4155 GREEN ARBORS LN\"],\"city\":\"CINCINNATI\",\"state\":\"OH\",\"postalCode\":\"45249\"},\"cardBrand\":\"MC\",\"expirationDate\":\"1970-01-01\",\"accountName\":\"TEST\",\"accountNumber\":\"5454\",\"identifier\":{\"value\":\"1367361\"},\"defaultPayment\":false},{\"resourceType\":\"PaymentCard\",\"meta\":{\"profile\":[\"http://hl7.org/fhir/StructureDefinition/PaymentReconciliation\"]},\"address\":{\"line\":[\"4155 GREEN ARBORS LN\"],\"city\":\"CINCINNATI\",\"state\":\"OH\",\"postalCode\":\"45249\"},\"cardBrand\":\"MC\",\"expirationDate\":\"2028-04-17\",\"accountName\":\"TEST WG\",\"accountNumber\":\"5656\",\"identifier\":{\"value\":\"1367362\"},\"defaultPayment\":true}],\"interventions\":[{\"assessmentProtocolId\":9148,\"patientInterventionId\":430065992,\"assessmentName\":\"PCC CID Refill\"}],\"deliveryDates\":[[2025,1,23]],\"confirmationType\":\"alternate\",\"lastUsedPaymentCard\":12121,\"isMedicarePPPEligible\":true}";
            String jsonString1 = "{\"deliveryDates\":[[2025,1,23]]}";

            JSONObject json = new JSONObject(jsonString);
            String key = "95fcfd28-3f57-4cd3-a8d4-cf7104b0d98d".substring(0, 16);

            Encryption xorEncryption = EncryptionFactory.getEncryption("AES");
            JsonEncryptor jsonEncryptor = new JsonEncryptor(xorEncryption, attributesToEncrypt);

            JSONObject encryptedJson = jsonEncryptor.encryptJson(json, key);
            System.out.println("Encrypted JSON: " + encryptedJson.toString());

            // For decryption, you can use the same process with the decrypt method
            JSONObject decryptedJson = jsonEncryptor.encryptJson(encryptedJson, key);
            System.out.println("Decrypted JSON: " + decryptedJson.toString());
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}