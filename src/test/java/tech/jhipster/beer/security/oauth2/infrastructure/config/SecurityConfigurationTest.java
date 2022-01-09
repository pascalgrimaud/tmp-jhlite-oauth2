package tech.jhipster.beer.security.oauth2.infrastructure.config;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import java.text.ParseException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
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

  @Mock
  ApplicationSecurityProperties applicationSecurityProperties;

  @Test
  void shouldUserAuthoritiesMapper() {
    assertThatCode(() -> securityConfiguration.userAuthoritiesMapper()).doesNotThrowAnyException();
  }

  @Test
  void shouldJwtDecoder() {
    try (MockedStatic<JwtDecoders> jwtDecoders = Mockito.mockStatic(JwtDecoders.class)) {
      NimbusJwtDecoder jwtDecoder = new NimbusJwtDecoder(withoutSigning());
      jwtDecoders.when(JwtDecoders.fromOidcIssuerLocation(anyString())).thenReturn(jwtDecoder);
      ReflectionTestUtils.setField(securityConfiguration, "issuerUri", "http://DO_NOT_CALL:9080/auth/realms/jhipster");

      ApplicationSecurityProperties.OAuth2 oauth2 = new ApplicationSecurityProperties.OAuth2();
      oauth2.setAudience(List.of("account", "api://default"));
      when(applicationSecurityProperties.getOauth2()).thenReturn(oauth2);

      assertThatCode(() -> securityConfiguration.jwtDecoder(clientRegistrationRepository, restTemplateBuilder)).doesNotThrowAnyException();
    }
  }

  //------------------------------------------------------------------------
  // Copyright - helped by NimbusJwtDecoderTests in Spring Security project
  // See https://github.com/spring-projects/spring-security
  //------------------------------------------------------------------------
  private static JWTProcessor<SecurityContext> withoutSigning() {
    return new MockJwtProcessor();
  }

  private static class MockJwtProcessor extends DefaultJWTProcessor<SecurityContext> {

    @Override
    public JWTClaimsSet process(SignedJWT signedJWT, SecurityContext context) throws BadJOSEException {
      try {
        return signedJWT.getJWTClaimsSet();
      } catch (ParseException ex) {
        throw new BadJWTException(ex.getMessage(), ex);
      }
    }
  }
}
