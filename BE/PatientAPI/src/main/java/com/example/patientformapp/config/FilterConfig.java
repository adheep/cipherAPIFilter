package com.example.patientformapp.config;

import com.example.patientformapp.filter.EncryptionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<EncryptionFilter> encryptionFilter() {
        FilterRegistrationBean<EncryptionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new EncryptionFilter());
        registrationBean.addUrlPatterns("/patients/*"); // Apply to specific URL patterns
        return registrationBean;
    }
}
