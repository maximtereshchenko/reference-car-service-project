package com.andersenlab.carservice.application;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.Map;

final class CarServiceJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtAuthenticationConverter original = new JwtAuthenticationConverter();

    CarServiceJwtAuthenticationConverter() {
        original.setJwtGrantedAuthoritiesConverter(this::grantedAuthorities);
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        return original.convert(source);
    }

    private Collection<GrantedAuthority> grantedAuthorities(Jwt source) {
        Map<String, Collection<String>> realmAccess = source.getClaim("realm_access");
        Collection<String> roles = realmAccess.get("roles");
        return roles.stream()
                .map(Role::grantedAuthority)
                .toList();
    }
}
