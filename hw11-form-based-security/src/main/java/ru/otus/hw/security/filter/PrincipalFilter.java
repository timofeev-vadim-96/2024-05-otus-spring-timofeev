package ru.otus.hw.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class PrincipalFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        this.logger.info("Principal: %s".formatted(principal));

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
