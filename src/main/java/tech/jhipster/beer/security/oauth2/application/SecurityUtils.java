package tech.jhipster.beer.security.oauth2.application;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import tech.jhipster.beer.security.oauth2.domain.AuthoritiesConstants;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

  public static final String CLAIMS_NAMESPACE = "https://www.jhipster.tech/";

  private SecurityUtils() {}

  /**
   * Get the login of the current user.
   *
   * @return the login of the current user.
   */
  public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
  }

  private static String extractPrincipal(Authentication authentication) {
    if (authentication == null) {
      return null;
    } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
      return springSecurityUser.getUsername();
    } else if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return (String) jwtAuthenticationToken.getToken().getClaims().get("preferred_username");
    } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
      Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
      if (attributes.containsKey("preferred_username")) {
        return (String) attributes.get("preferred_username");
      }
    } else if (authentication.getPrincipal() instanceof String principal) {
      return principal;
    }
    return null;
  }

  /**
   * Get the authorities of current user
   *
   * @return the authorities of current user
   */
  public static Set<String> getAuthorities() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    if (securityContext.getAuthentication() != null) {
      return securityContext.getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise.
   */
  public static boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
  }

  /**
   * Checks if the current user has any of the authorities.
   *
   * @param authorities the authorities to check.
   * @return true if the current user has any of the authorities, false otherwise.
   */
  public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority)));
  }

  /**
   * Checks if the current user has none of the authorities.
   *
   * @param authorities the authorities to check.
   * @return true if the current user has none of the authorities, false otherwise.
   */
  public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
    return !hasCurrentUserAnyOfAuthorities(authorities);
  }

  /**
   * Checks if the current user has a specific authority.
   *
   * @param authority the authority to check.
   * @return true if the current user has the authority, false otherwise.
   */
  public static boolean hasCurrentUserThisAuthority(String authority) {
    return hasCurrentUserAnyOfAuthorities(authority);
  }

  private static Stream<String> getAuthorities(Authentication authentication) {
    Collection<? extends GrantedAuthority> authorities = authentication instanceof JwtAuthenticationToken
      ? extractAuthorityFromClaims(((JwtAuthenticationToken) authentication).getToken().getClaims())
      : authentication.getAuthorities();
    return authorities.stream().map(GrantedAuthority::getAuthority);
  }

  public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
    return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
  }

  @SuppressWarnings("unchecked")
  private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
    return (Collection<String>) claims.getOrDefault(
      "groups",
      claims.getOrDefault("roles", claims.getOrDefault(CLAIMS_NAMESPACE + "roles", new ArrayList<>()))
    );
  }

  private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
    return roles.stream().filter(role -> role.startsWith("ROLE_")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
