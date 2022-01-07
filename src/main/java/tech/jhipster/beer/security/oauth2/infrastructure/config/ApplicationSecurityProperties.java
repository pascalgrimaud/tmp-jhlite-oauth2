package tech.jhipster.beer.security.oauth2.infrastructure.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.security", ignoreUnknownFields = false)
public class ApplicationSecurityProperties {

  private String contentSecurityPolicy = ApplicationSecurityDefaults.CONTENT_SECURITY_POLICY;

  private final Authentication authentication = new Authentication();

  public Authentication getAuthentication() {
    return authentication;
  }

  public String getContentSecurityPolicy() {
    return contentSecurityPolicy;
  }

  public void setContentSecurityPolicy(String contentSecurityPolicy) {
    this.contentSecurityPolicy = contentSecurityPolicy;
  }

  public static class Authentication {

    private final OAuth2 oauth2 = new OAuth2();

    public Authentication.OAuth2 getOauth2() {
      return oauth2;
    }

    public static class OAuth2 {

      private List<String> audience = new ArrayList<>();

      public List<String> getAudience() {
        return Collections.unmodifiableList(audience);
      }

      public void setAudience(@NotNull List<String> audience) {
        this.audience.addAll(audience);
      }
    }
  }
}
