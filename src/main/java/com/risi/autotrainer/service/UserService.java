package com.risi.autotrainer.service;

import com.risi.autotrainer.domain.User;
import com.risi.autotrainer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = repository.findByUsername(username);
        if (user.isEmpty())
            throw new UsernameNotFoundException("User " + username + " not found.");
        return user.get();
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    public void deleteUser(User user) {
        repository.delete(user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void emailTemporaryPassword(User user) {
        emailService.sendTemporaryPassword(user);
    }

    String generateTemporaryPassword(User user) {
        // Generate a random length for the password 5-8 characters.
        int length = (int) (Math.random() * 4) + 5;
        // Creates a random password.
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < length; ++i)
            tempPassword.append((char) ((Math.random() * 32) + 33));

        var optionalUser = repository.findByUsername(user.getUsername());
        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException("Cannot create temporary password for non existent user.");
        user = optionalUser.get();
        user.setTemporaryPassword(passwordEncoder.encode(tempPassword.toString()));
        repository.save(user);
        return tempPassword.toString();
    }
}
