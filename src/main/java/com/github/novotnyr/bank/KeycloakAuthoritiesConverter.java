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
 *     <pre>
 *         "realm_access : { "roles":["visitor"] }
 *     </pre>
 * </p>
 */
public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String REALM_ACCESS_CLAIM = "realm_access";

    public static final String ROLES_CLAIM = "roles";

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        if (!source.hasClaim(REALM_ACCESS_CLAIM)) {
            return Collections.emptyList();
        }
        Object claim = source.getClaim(REALM_ACCESS_CLAIM);
        if (!(claim instanceof Map)) {
            return Collections.emptyList();
        }
        Map<String, Object> realmAccess = (Map<String, Object>) claim;
        if (!realmAccess.containsKey(ROLES_CLAIM)) {
            return Collections.emptyList();
        }
        Object rolesClaimObject = realmAccess.get(ROLES_CLAIM);
        if (!(rolesClaimObject instanceof Collection<?> roleObjects)) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Object roleObject : roleObjects) {
            String role = roleObject.toString();
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);
            authorities.add(simpleGrantedAuthority);
        }
        return authorities;
    }
}