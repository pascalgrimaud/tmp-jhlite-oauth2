package tech.jhipster.beer.security.oauth2.infrastructure.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import tech.jhipster.beer.UnitTest;

/**
 * Test class for the {@link AudienceValidator} utility class.
 */
@UnitTest
class AudienceValidatorTest {

  private final AudienceValidator validator = new AudienceValidator(List.of("api://default"));

  @Test
  void shouldInvalidAudience() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("aud", "bar");
    Jwt badJwt = mock(Jwt.class);
    when(badJwt.getAudience()).thenReturn(new ArrayList(claims.values()));
    assertThat(validator.validate(badJwt).hasErrors()).isTrue();
  }

  @Test
  void shouldValidAudience() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("aud", "api://default");
    Jwt jwt = mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(new ArrayList(claims.values()));
    assertThat(validator.validate(jwt).hasErrors()).isFalse();
  }
}
