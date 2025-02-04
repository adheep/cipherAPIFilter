package com.example.patientformapp.filter;

import com.example.patientformapp.util.AESUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class EncryptingHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintWriter writer = new PrintWriter(outputStream);
    private ServletOutputStream servletOutputStream;
    private boolean usingWriter;

    public EncryptingHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (usingWriter) {
            throw new IllegalStateException("getWriter() has already been called for this response");
        }
        if (servletOutputStream == null) {
            servletOutputStream = new ServletOutputStreamWrapper(outputStream, super.getOutputStream());
        }
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (servletOutputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called for this response");
        }
        usingWriter = true;
        return writer;
    }

    public void finishResponse() throws IOException {
        if (usingWriter) {
            writer.flush();
        } else if (servletOutputStream != null) {
            servletOutputStream.flush();
        }
        String responseData = outputStream.toString();
        try {
            String encryptedData = AESUtil.encrypt(responseData);
            Map<String, String> responseMap = Map.of("data", encryptedData);
            String jsonResponse = new ObjectMapper().writeValueAsString(responseMap);
            byte[] jsonResponseBytes = jsonResponse.getBytes();
            super.resetBuffer(); // Clear the existing response
            super.getOutputStream().write(jsonResponseBytes);
        } catch (Exception e) {
            throw new IOException("Failed to encrypt response data", e);
        }
    }
}