package com.example.gatekeeperpattern.config;

import com.example.gatekeeperpattern.filter.SqlInjectionGatekeeperFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SqlInjectionGatekeeperFilter> sqlInjectionGatekeeperFilter() {
        FilterRegistrationBean<SqlInjectionGatekeeperFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SqlInjectionGatekeeperFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}