package com.example.config;

import com.example.filter.JustFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<JustFilter> jsutFilter(){
        FilterRegistrationBean<JustFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JustFilter());
        registrationBean.addUrlPatterns("/api/*"); // 指定过滤的URL路径
        return registrationBean;
    }
}
