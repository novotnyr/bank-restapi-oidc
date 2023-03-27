package com.github.novotnyr.bank;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Extracts Keycloak roles to authorities.
 * <p>
 *     Source:
 *     <pre>{
 *  "megabank": {
 *    "roles": ["withdrawer"]
 *  },
 *  "account": {
 *    "roles": ["manage-account", "manage-account-links", "view-profile"]
 *  }
 *}
 *</pre>
 * </p>
 */
public class KeycloakClaimRoleAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    public static final String RESOURCE_ACCESS_CLAIM = "resource_access";

    public static final String ROLES_CLAIM = "roles";

    private final String oAuth2ClientId;


    public KeycloakClaimRoleAuthoritiesConverter(String oAuth2ClientId) {
        this.oAuth2ClientId = oAuth2ClientId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> resourceAccessValues = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);
        if (resourceAccessValues == null) {
            return Collections.emptyList();
        }
        Object clientRolesObject = resourceAccessValues.get(this.oAuth2ClientId);
        if (!(clientRolesObject instanceof Map<?, ?>)) {
            return Collections.emptyList();
        }
        Map<String, ?> clientRoles = (Map<String, ?>) clientRolesObject;
        Object rolesObject = clientRoles.get(ROLES_CLAIM);
        if (!(rolesObject instanceof Collection<?>)) {
            return Collections.emptyList();
        }
        Collection<Object> rolesList = (Collection<Object>) rolesObject;
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Object roleObject : rolesList) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleObject.toString());
            authorities.add(authority);
        }
        return authorities;
    }
}