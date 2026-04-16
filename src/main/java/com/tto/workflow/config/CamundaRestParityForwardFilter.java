package com.tto.workflow.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(
    name = "camunda.parity.forward-enabled",
    havingValue = "true",
    matchIfMissing = false)
public class CamundaRestParityForwardFilter extends OncePerRequestFilter {

  private static final String PARITY_PREFIX = "/api/v1/camunda";
  private static final String ENGINE_REST_PREFIX = "/engine-rest";
  private static final String FORWARDED_FLAG = "camunda.parity.forwarded";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getAttribute(FORWARDED_FLAG) != null) {
      filterChain.doFilter(request, response);
      return;
    }

    String contextPath = request.getContextPath();
    String requestUri = request.getRequestURI();
    String effectiveUri = requestUri.substring(contextPath.length());
    if (!effectiveUri.startsWith(PARITY_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String suffix = effectiveUri.substring(PARITY_PREFIX.length());
    String target = ENGINE_REST_PREFIX + suffix;
    String query = request.getQueryString();
    if (query != null && !query.isBlank()) {
      target = target + "?" + query;
    }

    request.setAttribute(FORWARDED_FLAG, Boolean.TRUE);
    RequestDispatcher dispatcher = request.getRequestDispatcher(target);
    dispatcher.forward(request, response);
  }
}
