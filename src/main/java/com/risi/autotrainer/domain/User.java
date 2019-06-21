package com.risi.autotrainer.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;
    @Email
    private final String username;
    @NonNull
    private String password;
    private String temporaryPassword;
    private boolean enabled = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonExpired = true;
    private Map<Priority, Exercise> exercisePriority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

}
