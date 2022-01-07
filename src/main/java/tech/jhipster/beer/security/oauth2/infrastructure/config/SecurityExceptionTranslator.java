package tech.jhipster.beer.security.oauth2.infrastructure.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import tech.jhipster.beer.technical.infrastructure.primary.exception.ExceptionTranslator;

@ControllerAdvice
public class SecurityExceptionTranslator extends ExceptionTranslator implements SecurityAdviceTrait {}
