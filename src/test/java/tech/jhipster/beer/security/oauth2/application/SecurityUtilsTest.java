package tech.jhipster.beer.security.oauth2.application;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;

import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import tech.jhipster.beer.UnitTest;
import tech.jhipster.beer.security.oauth2.domain.AuthoritiesConstants;

@UnitTest
class SecurityUtilsTest {

  @BeforeEach
  @AfterEach
  void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldNotGetAuthorities() {
    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void shouldNotGetCurrentUserLoginForNull() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(null);
    SecurityContextHolder.setContext(securityContext);
    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).isEmpty();

    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void shouldGetCurrentUserLoginWithUserDetails() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
    User user = new User("admin", "admin", authorities);
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(user, "admin", authorities));
    SecurityContextHolder.setContext(securityContext);
    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).contains("admin");

    assertThat(SecurityUtils.getAuthorities()).contains(AuthoritiesConstants.ADMIN);
  }

  @Test
  void shouldGetCurrentUserLoginWithJwtAuthenticationToken() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

    Jwt jwt = Jwt
      .withTokenValue("token")
      .header("alg", JwsAlgorithms.RS256)
      .subject("jhipster")
      .claim("preferred_username", "admin")
      .build();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
    JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);

    securityContext.setAuthentication(token);
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.getCurrentUserLogin()).contains("admin");
    assertThat(SecurityUtils.getAuthorities()).contains(AuthoritiesConstants.ADMIN);
    assertThat(SecurityUtils.isAuthenticated()).isTrue();
  }

  @Test
  void shouldGetCurrentUserLoginForOAuth2() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Map<String, Object> claims = new HashMap<>();
    claims.put("groups", AuthoritiesConstants.USER);
    claims.put("sub", 123);
    claims.put("preferred_username", "admin");
    OidcIdToken idToken = new OidcIdToken(ID_TOKEN, Instant.now(), Instant.now().plusSeconds(60), claims);
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    OidcUser user = new DefaultOidcUser(authorities, idToken);
    OAuth2AuthenticationToken auth2AuthenticationToken = new OAuth2AuthenticationToken(user, authorities, "oidc");
    securityContext.setAuthentication(auth2AuthenticationToken);
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.getCurrentUserLogin()).contains("admin");
    assertThat(SecurityUtils.getAuthorities()).contains(AuthoritiesConstants.USER);
    assertThat(SecurityUtils.isAuthenticated()).isTrue();
  }

  @Test
  void shouldNotGetCurrentUserLoginForOAuth2() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Map<String, Object> claims = new HashMap<>();
    claims.put("groups", AuthoritiesConstants.USER);
    claims.put("sub", 123);
    OidcIdToken idToken = new OidcIdToken(ID_TOKEN, Instant.now(), Instant.now().plusSeconds(60), claims);
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    OidcUser user = new DefaultOidcUser(authorities, idToken);
    OAuth2AuthenticationToken auth2AuthenticationToken = new OAuth2AuthenticationToken(user, authorities, "oidc");
    securityContext.setAuthentication(auth2AuthenticationToken);
    SecurityContextHolder.setContext(securityContext);

    Optional<String> login = SecurityUtils.getCurrentUserLogin();

    assertThat(login).isEmpty();
  }

  @Test
  void shouldGetCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
    SecurityContextHolder.setContext(securityContext);
    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).contains("admin");

    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void shouldNotGetCurrentUserLoginForAnotherInstance() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new TestingAuthenticationToken(null, null));
    SecurityContextHolder.setContext(securityContext);

    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).isEmpty();

    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void testExtractAuthorityFromClaims() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("groups", Arrays.asList(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER));

    List<GrantedAuthority> expectedAuthorities = Arrays.asList(
      new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN),
      new SimpleGrantedAuthority(AuthoritiesConstants.USER)
    );

    List<GrantedAuthority> authorities = SecurityUtils.extractAuthorityFromClaims(claims);

    assertThat(authorities).isNotNull().isNotEmpty().hasSize(2).containsAll(expectedAuthorities);
  }

  @Test
  void testExtractAuthorityFromClaims_NamespacedRoles() {
    Map<String, Object> claims = new HashMap<>();
    claims.put(SecurityUtils.CLAIMS_NAMESPACE + "roles", Arrays.asList(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER));

    List<GrantedAuthority> expectedAuthorities = Arrays.asList(
      new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN),
      new SimpleGrantedAuthority(AuthoritiesConstants.USER)
    );

    List<GrantedAuthority> authorities = SecurityUtils.extractAuthorityFromClaims(claims);

    assertThat(authorities).isNotNull().isNotEmpty().hasSize(2).containsAll(expectedAuthorities);
  }

  @Test
  void testIsAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isTrue();
  }

  @Test
  void shouldBeAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isTrue();
  }

  @Test
  void shouldNotBeAuthenticatedWithNull() {
    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isFalse();
  }

  @Test
  void shouldAnonymousIsNotAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isFalse();
  }

  @Test
  void testAnonymousIsNotAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isFalse();
  }

  @Test
  void testHasCurrentUserThisAuthority() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.USER)).isTrue();
    assertThat(SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)).isFalse();
  }

  @Test
  void shouldHasCurrentUserAnyOfAuthorities() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)).isTrue();
    assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.ADMIN)).isFalse();
  }

  @Test
  void shouldNotHaveCurrentUserAnyOfAuthoritiesForNull() {
    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());

    assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)).isFalse();
  }

  @Test
  void shouldHasCurrentUserNoneOfAuthorities() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)).isFalse();
    assertThat(SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.ADMIN)).isTrue();
  }
}
