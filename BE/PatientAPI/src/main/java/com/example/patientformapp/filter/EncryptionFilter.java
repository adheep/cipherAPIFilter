package com.example.patientformapp.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EncryptionFilter implements Filter {

    private static final String ENCRYPTED_CONTENT_TYPE = "application/com.arxdig.api.e2+json";
    private static final String PLAIN_CONTENT_TYPE = "application/json";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    //doFilter for body encrypt/decrypt
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contentType = httpRequest.getContentType();

        if (ENCRYPTED_CONTENT_TYPE.equals(contentType)) {
            // Decrypt the request data
            DecryptingHttpServletRequestWrapper wrappedRequest = new DecryptingHttpServletRequestWrapper(httpRequest);

            // Encrypt the response data
            EncryptingHttpServletResponseWrapper wrappedResponse = new EncryptingHttpServletResponseWrapper(httpResponse);

            // Proceed with the request
            chain.doFilter(wrappedRequest, wrappedResponse);

            // Finish the response
            wrappedResponse.finishResponse();
        } else {
            // Proceed without encryption/decryption
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}