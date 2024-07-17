package com.mysite.social.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private OAuth2User oAuth2User;
    private String provider;
    private String providerId;
    private String name;

    public CustomOAuth2User(OAuth2User oAuth2User, String provider, String providerId, String name) {
        this.oAuth2User = oAuth2User;
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }
    
}