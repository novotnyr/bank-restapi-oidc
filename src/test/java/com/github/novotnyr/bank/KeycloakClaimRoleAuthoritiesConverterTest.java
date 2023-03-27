package com.github.novotnyr.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;

import static com.github.novotnyr.bank.KeycloakClaimRoleAuthoritiesConverter.RESOURCE_ACCESS_CLAIM;

class KeycloakClaimRoleAuthoritiesConverterTest {
    private KeycloakClaimRoleAuthoritiesConverter authoritiesConverter;

    @BeforeEach
    void setUp() {
        this.authoritiesConverter = new KeycloakClaimRoleAuthoritiesConverter("megabank");
    }

    @Test
    void testJwtClaim() throws JsonProcessingException {
        String resourceAccessJson = "{\"megabank\":{\"roles\":[\"withdrawer\"]},\"account\":{\"roles\":[\"manage-account\",\"manage-account-links\",\"view-profile\"]}}";
        String tokenValue = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1MTYyMzkwMjJ9.Q0-H7SMLDKveVbsaz2XV10cAwhusyWi_qvWpIWbvnzDFOSqG37L_nISiy1vnmSW1Pg-eMYCrnwvV7cLzHcrr_h071NQAN5zgZ0X0FpjUn4KgqzsYZu6vHq_amQ3xsTF88-tLvqaS7cB1bq2gHfNbc2rzimEg6gHikENInqA2g-Krn2y1DaaCE9_lZX-tOWhbSWpgayH1VzcOKXkdJm6p9-8tb7dxRRxJhVuQldjyqFcd9K1SXYtb64FMLB78d9LteJ4qIE8vXYoASrRmeZRa69mtMCYq7DIs3h-UfKb2WnWX3bCEpbHq0DVMQvFK5BdkbRoFXhdOrkgiq4pXkcfzMA";

        ObjectMapper objectMapper = new ObjectMapper();
        Map resourceAccess = objectMapper.readValue(resourceAccessJson, Map.class);

        Jwt jwt = Jwt.withTokenValue(tokenValue)
                     .claim(RESOURCE_ACCESS_CLAIM, resourceAccess)
                     .header("alg", "RS256")
                     .build();

        Collection<GrantedAuthority> authorities = this.authoritiesConverter.convert(jwt);
        Assertions.assertNotNull(authorities);
        Assertions.assertEquals(1, authorities.size());
        Assertions.assertEquals("withdrawer", authorities.iterator().next().toString());
    }
}