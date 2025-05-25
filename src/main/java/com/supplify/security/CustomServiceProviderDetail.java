package com.supplify.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.supplify.entity.ServiceProvider;

import java.util.Collection;

public class CustomServiceProviderDetail implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServiceProvider serviceProvider;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomServiceProviderDetail(ServiceProvider serviceProvider, Collection<? extends GrantedAuthority> authorities) {
        this.serviceProvider = serviceProvider;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return serviceProvider.getPassword();
    }

    @Override
    public String getUsername() {
        return serviceProvider.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
