package com.example.patientformapp.filter;

import com.example.patientformapp.util.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

public class DecryptingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] requestBody;

    public DecryptingHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        String encryptedData = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            Map<String, String> requestData = new ObjectMapper().readValue(encryptedData, Map.class);
            String data = requestData.get("data");
            String decryptedData = AESUtil.decrypt(data);
            this.requestBody = decryptedData.getBytes();
        } catch (Exception e) {
            throw new IOException("Failed to decrypt request data", e);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedServletInputStream(this.requestBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.requestBody)));
    }

    private static class CachedServletInputStream extends ServletInputStream {

        private ByteArrayInputStream inputStream;

        public CachedServletInputStream(byte[] requestBody) {
            this.inputStream = new ByteArrayInputStream(requestBody);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // No-op implementation
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}