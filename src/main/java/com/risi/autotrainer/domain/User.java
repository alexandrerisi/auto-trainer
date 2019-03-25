package com.risi.autotrainer.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;
    private final String username;
    private String password;
    private boolean enabled;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean accountNonExpired;
    private Map<Priority, Exercise> exercisePriority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

}
