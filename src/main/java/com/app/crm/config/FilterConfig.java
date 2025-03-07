package com.app.crm.config;

import com.app.crm.filter.SessionValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SessionValidationFilter> sessionValidationFilter() {
        FilterRegistrationBean<SessionValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SessionValidationFilter());
        registrationBean.addUrlPatterns("/admin/*", "/counselor/*"); // Apply to restricted URLs
        return registrationBean;
    }
}