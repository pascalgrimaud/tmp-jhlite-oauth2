package tech.jhipster.beer.security.oauth2.infrastructure.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.jhipster.beer.UnitTest;

@UnitTest
@ExtendWith(SpringExtension.class)
class SecurityConfigurationTest {

  @InjectMocks
  SecurityConfiguration securityConfiguration;

  @Mock
  ClientRegistrationRepository clientRegistrationRepository;

  @Mock
  RestTemplateBuilder restTemplateBuilder;

  @Test
  void shouldUserAuthoritiesMapper() {
    assertThatCode(() -> securityConfiguration.userAuthoritiesMapper()).doesNotThrowAnyException();
  }

  @Test
  @Disabled
  void shouldJwtDecoder() {
    assertThatCode(() -> securityConfiguration.jwtDecoder(clientRegistrationRepository, restTemplateBuilder)).doesNotThrowAnyException();
  }
}
