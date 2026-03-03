package kim.nzxy.demo.common.web;


import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<MultiReadHttpServletRequest> requestBodyReaderFilter() {
        FilterRegistrationBean<MultiReadHttpServletRequest> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MultiReadHttpServletRequest());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    public static class MultiReadHttpServletRequest extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            filterChain.doFilter(new RequestWrapper(request), response);
        }
    }

}