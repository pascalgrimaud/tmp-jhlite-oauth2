package tech.jhipster.beer.security.oauth2.infrastructure.config;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.jhipster.beer.UnitTest;
import tech.jhipster.beer.security.oauth2.domain.ApplicationSecurityDefaults;

@UnitTest
class ApplicationSecurityPropertiesTest {

  private ApplicationSecurityProperties properties;

  @BeforeEach
  void setup() {
    properties = new ApplicationSecurityProperties();
  }

  @Test
  void shouldGetSecurityContentSecurityPolicy() {
    ApplicationSecurityProperties obj = properties;
    String val = ApplicationSecurityDefaults.CONTENT_SECURITY_POLICY;
    assertThat(obj.getContentSecurityPolicy()).isEqualTo(val);
    obj.setContentSecurityPolicy("foobar");
    assertThat(obj.getContentSecurityPolicy()).isEqualTo("foobar");
  }

  @Test
  @DisplayName("should get OAuth2 audience")
  void shouldGetOAuth2Audience() {
    ApplicationSecurityProperties.OAuth2 obj = properties.getOauth2();

    assertThat(obj.getAudience()).isEmpty();
    obj.setAudience(List.of("account", "api://default"));
    assertThat(obj.getAudience()).contains("account", "api://default");
  }
}
