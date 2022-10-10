package com.fenrir.masterdetail.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fenrir.masterdetail.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String username;
    @JsonIgnore private String email;
    @JsonIgnore private String password;
    private GrantedAuthority authority;

    public UserDetailsImpl(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authority = new SimpleGrantedAuthority(user.getRole().name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
